package com.brave.dubbo.trace.monitor;

import com.brave.dubbo.trace.monitor.constants.CatConstants;
import com.brave.dubbo.trace.thread.TraceFutureTask;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.remoting.RemotingException;
import org.apache.dubbo.remoting.TimeoutException;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.support.RpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Activate(group = {CommonConstants.PROVIDER, CommonConstants.CONSUMER},order = -9000)
public class CatTransaction implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(TraceFutureTask.class);

    private final static String DUBBO_BIZ_ERROR="DUBBO_BIZ_ERROR";

    private final static String DUBBO_TIMEOUT_ERROR="DUBBO_TIMEOUT_ERROR";
    
    private final static String DUBBO_REMOTING_ERROR="DUBBO_REMOTING_ERROR";


    private static final ThreadLocal<Cat.Context> CAT_CONTEXT = new ThreadLocal<>();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if(!DubboCat.isEnable()){
            return invoker.invoke(invocation);
        }
        URL url = invoker.getUrl();
        String sideKey = url.getParameter(CommonConstants.SIDE_KEY);
        String loggerName = invoker.getInterface().getSimpleName()+"."+invocation.getMethodName();
        String type = CatConstants.CROSS_CONSUMER;
        if(CommonConstants.PROVIDER_SIDE.equals(sideKey)){
            type= CatConstants.CROSS_SERVER;
        }

        boolean init = false;
        Transaction transaction = null;

        try{
            transaction = Cat.newTransaction(type, loggerName);
            Cat.Context context = getContext();
            if(CommonConstants.CONSUMER_SIDE.equals(sideKey)){
                createConsumerCross(url,transaction);
                Cat.logRemoteCallClient(context);
            }else{
                createProviderCross(url,transaction);
                Cat.logRemoteCallServer(context);
            }
            setAttachment(context);
            init = true;
        } catch (Exception e) {
            logger.error("[DUBBO] DubboCat init error.", e);
        }

        Result result=null;
        try{
            result =  invoker.invoke(invocation);

            if (!init) {
                return result;
            }

            boolean isAsync = RpcUtils.isAsync(invoker.getUrl(), invocation);

            //????????????????????????????????????,????????????????????????(<AsyncRpcResult>hasException->getRpcResult->resultFuture.get()
            if (isAsync) {
                transaction.setStatus(Message.SUCCESS);
                return result;
            }

            if(result.hasException()){
                //???????????????????????????????????????
                Throwable throwable = result.getException();
                Event event;
                if(RpcException.class==throwable.getClass()){
                    Throwable caseBy = throwable.getCause();
                    if(caseBy!=null&&caseBy.getClass()==TimeoutException.class){
                        event = Cat.newEvent(DUBBO_TIMEOUT_ERROR,loggerName);
                    }else{
                        event = Cat.newEvent(DUBBO_REMOTING_ERROR,loggerName);
                    }
                }else if(RemotingException.class.isAssignableFrom(throwable.getClass())){
                    event = Cat.newEvent(DUBBO_REMOTING_ERROR,loggerName);
                }else{
                    event = Cat.newEvent(DUBBO_BIZ_ERROR,loggerName);
                }
                event.setStatus(result.getException());
                completeEvent(event);
                transaction.addChild(event);
                transaction.setStatus(result.getException().getClass().getSimpleName());
            }else{
                transaction.setStatus(Message.SUCCESS);
            }
            return result;
        }catch (RuntimeException e){
            if (init) {
                Cat.logError(e);
                Event event;
                if (RpcException.class == e.getClass()) {
                    Throwable caseBy = e.getCause();
                    if (caseBy != null && caseBy.getClass() == TimeoutException.class) {
                        event = Cat.newEvent(DUBBO_TIMEOUT_ERROR, loggerName);
                    } else {
                        event = Cat.newEvent(DUBBO_REMOTING_ERROR, loggerName);
                    }
                } else {
                    event = Cat.newEvent(DUBBO_BIZ_ERROR, loggerName);
                }
                event.setStatus(e);
                completeEvent(event);
                transaction.addChild(event);
                transaction.setStatus(e.getClass().getSimpleName());
            }
            if(result==null){
                throw e;
            }else{
                return result;
            }
        }finally {
            if (transaction != null) {
                transaction.complete();
            }
            CAT_CONTEXT.remove();
        }
    }

    static class DubboCatContext implements Cat.Context{

        private final Map<String,String> properties = new HashMap<>();

        @Override
        public void addProperty(String key, String value) {
            properties.put(key,value);
        }

        @Override
        public String getProperty(String key) {
            return properties.get(key);
        }
    }

    private String getProviderAppName(URL url){
        String appName = url.getParameter(CatConstants.PROVIDER_APPLICATION_NAME);
        if(StringUtils.isEmpty(appName)){
            String interfaceName  = url.getParameter(CommonConstants.INTERFACE_KEY);
            appName = interfaceName.substring(0,interfaceName.lastIndexOf('.'));
        }
        return appName;
    }

    private void setAttachment(Cat.Context context){
        RpcContext.getContext().setAttachment(Cat.Context.ROOT,context.getProperty(Cat.Context.ROOT));
        RpcContext.getContext().setAttachment(Cat.Context.CHILD,context.getProperty(Cat.Context.CHILD));
        RpcContext.getContext().setAttachment(Cat.Context.PARENT,context.getProperty(Cat.Context.PARENT));
    }

    private Cat.Context getContext(){
        Cat.Context context = CAT_CONTEXT.get();
        if(context==null){
            context = initContext();
            CAT_CONTEXT.set(context);
        }
        return context;
    }

    private Cat.Context initContext(){
        Cat.Context context = new DubboCatContext();
        Map<String,String> attachments = RpcContext.getContext().getAttachments();
        if(attachments!=null&&attachments.size()>0){
            for(Map.Entry<String,String> entry:attachments.entrySet()){
                if(Cat.Context.CHILD.equals(entry.getKey())||Cat.Context.ROOT.equals(entry.getKey())||Cat.Context.PARENT.equals(entry.getKey())){
                    context.addProperty(entry.getKey(),entry.getValue());
                }
            }
        }
        return context;
    }

    private void createConsumerCross(URL url,Transaction transaction){
        Event crossAppEvent =   Cat.newEvent(CatConstants.CONSUMER_CALL_APP,getProviderAppName(url));
        Event crossServerEvent =   Cat.newEvent(CatConstants.CONSUMER_CALL_SERVER,url.getHost());
        Event crossPortEvent =   Cat.newEvent(CatConstants.CONSUMER_CALL_PORT,url.getPort()+"");
        crossAppEvent.setStatus(Event.SUCCESS);
        crossServerEvent.setStatus(Event.SUCCESS);
        crossPortEvent.setStatus(Event.SUCCESS);
        completeEvent(crossAppEvent);
        completeEvent(crossPortEvent);
        completeEvent(crossServerEvent);
        transaction.addChild(crossAppEvent);
        transaction.addChild(crossPortEvent);
        transaction.addChild(crossServerEvent);
    }

    private void completeEvent(Event event){
        event.complete();
    }

    private void createProviderCross(URL url,Transaction transaction){
        String consumerAppName = RpcContext.getContext().getAttachment(CommonConstants.APPLICATION_KEY);
        if(StringUtils.isEmpty(consumerAppName)){
            consumerAppName= RpcContext.getContext().getRemoteHost()+":"+ RpcContext.getContext().getRemotePort();
        }
        Event crossAppEvent = Cat.newEvent(CatConstants.PROVIDER_CALL_APP,consumerAppName);
        Event crossServerEvent = Cat.newEvent(CatConstants.PROVIDER_CALL_SERVER, RpcContext.getContext().getRemoteHost());
        crossAppEvent.setStatus(Event.SUCCESS);
        crossServerEvent.setStatus(Event.SUCCESS);
        completeEvent(crossAppEvent);
        completeEvent(crossServerEvent);
        transaction.addChild(crossAppEvent);
        transaction.addChild(crossServerEvent);
    }
}
