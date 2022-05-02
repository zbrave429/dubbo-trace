package com.brave.dubbo.trace.monitor;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

@Activate(group = {CommonConstants.CONSUMER})
public class AppNameAppendFilter implements Filter {
    
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcContext.getContext().setAttachment(CommonConstants.APPLICATION_KEY,invoker.getUrl().getParameter(CommonConstants.APPLICATION_KEY));
        return invoker.invoke(invocation);
    }
}
