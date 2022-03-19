package com.brave.dubbo.trace.filter;

import com.brave.dubbo.trace.TraceConstants;
import com.brave.dubbo.trace.TraceUtils;
import com.brave.dubbo.trace.Tracer;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;
import java.util.Objects;

/**
 * trace功能植入
 *
 * @author <a href='1286998496@qq.com'>zhangyong</a>
 * @date 2022-03-19 18:09
 */

@Activate(group = {CommonConstants.PROVIDER})
public class TraceProviderFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(TraceProviderFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        Map<String, String> attachments = invocation.getAttachments();

        Tracer tracer = buildTracer(attachments);
        try{
            initTraceContext(tracer);
            return invoker.invoke(invocation);
        } finally {
            removeTraceContext();
        }
    }

    private void initTraceContext(Tracer tracer){

        if(Objects.isNull(tracer)){
            return;
        }
        TraceUtils.set(tracer);
        MDC.put(TraceConstants.TRACE_ID, tracer.getTraceId());
        MDC.put(TraceConstants.SPAN_ID, tracer.getSpanId());

        if (logger.isDebugEnabled()){
            logger.debug("initTraceContext success, tracer={}", tracer);
        }
    }

    private void removeTraceContext(){
        Tracer tracer = TraceUtils.get();
        TraceUtils.remove();
        MDC.remove(TraceConstants.TRACE_ID);
        MDC.remove(TraceConstants.SPAN_ID);
        if (logger.isDebugEnabled()){
            logger.debug("removeTraceContext success, tracer={}", tracer);
        }
    }

    private Tracer buildTracer(Map<String, String> attachments){
        return new Tracer(attachments.get(TraceConstants.TRACE_ID),
                attachments.get(TraceConstants.SPAN_ID));
    }
}
