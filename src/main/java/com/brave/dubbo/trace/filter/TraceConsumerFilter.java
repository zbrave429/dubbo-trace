package com.brave.dubbo.trace.filter;

import com.brave.dubbo.trace.TraceConstants;
import com.brave.dubbo.trace.TraceContext;
import com.brave.dubbo.trace.Tracer;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * trace功能植入
 *
 *  消费者过滤器
 *  从trace缓存中取traceId放入Attachments属性内
 *
 * @author <a href='1286998496@qq.com'>zhangyong</a>
 * @date 2022-03-19 18:09
 */
@Activate(group = {CommonConstants.CONSUMER})
public class TraceConsumerFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(TraceConsumerFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        Tracer tracer = TraceContext.get();
        if (!Objects.isNull(tracer)){
            setTrace(invocation, tracer);
        }
        return invoker.invoke(invocation);
    }

    private void setTrace(Invocation invocation, Tracer tracer){

        if(StringUtils.isNotBlank(tracer.getTraceId())){
            invocation.getAttachments().put(TraceConstants.TRACE_ID, tracer.getTraceId());
        }

        if(StringUtils.isNotBlank(tracer.getSpanId())){
            invocation.getAttachments().put(TraceConstants.SPAN_ID,
                    tracer.getSpanId() + TraceConstants.SPAN_SEPARATOR + tracer.getLogicId().addAndGet(1));
        } else {
            invocation.getAttachments().put(TraceConstants.SPAN_ID, String.valueOf(tracer.getLogicId().addAndGet(1)));
        }
        if (logger.isDebugEnabled()){
            logger.debug("setTrace success, trace={}", tracer);
        }
    }

}