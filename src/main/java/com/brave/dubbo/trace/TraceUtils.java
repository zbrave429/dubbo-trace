package com.brave.dubbo.trace;

/**
 * trace
 *
 * @author <a href='1286998496@qq.com'>zhangyong</a>
 * @date 2022-03-19 19:04
 */
public class TraceUtils {

    private static final ThreadLocal<Tracer> tracerThreadLocal = new ThreadLocal<>();

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
