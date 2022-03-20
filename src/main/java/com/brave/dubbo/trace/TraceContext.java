package com.brave.dubbo.trace;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * trace
 *
 * @author <a href='1286998496@qq.com'>zhangyong</a>
 * @date 2022-03-19 19:04
 */
public class TraceContext {

    /**
     * 引入阿里TransmittableThreadLocal 解决子线程ThreadLocal参数传递问题
     */
    private static final ThreadLocal<Tracer> tracerThreadLocal = new TransmittableThreadLocal<>();

    public static void set(Tracer tracer){
        tracerThreadLocal.set(tracer);
    }

    public static Tracer get(){
        return tracerThreadLocal.get();
    }

    public static void remove(){
        tracerThreadLocal.remove();
    }

}
