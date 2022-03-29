package com.brave.dubbo.trace.thread;

import com.alibaba.ttl.TtlRunnable;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * AsyncTaskFactory
 *  异步任务执行工厂
 *
 *  spring 项目使用示例
 *
 *  public class AsyncTaskFactoryConfig {
 *
 *     private int timeout;
 *
 *     private String threadPrefix;
 *      // 初始化
 *     @PostConstruct
 *     private void init() {
 *         ThreadPoolConfig asyncConfig = new ThreadPoolConfig(timeout, threadPrefix);
 *         AsyncTaskFactory.init(asyncConfig);
 *     }
 *      // 销毁方法
 *     @PreDestroy
 *     private void destroy() {
 *         AsyncTaskFactory.destroy();
 *     }
 *
 * }
 *
 * @author <a href='1286998496@qq.com'>zhangyong</a>
 * @date 2022-01-09 11:20
 */
public class AsyncTaskFactory {

    private static ExecutorService EXECUTOR;

    private static ThreadPoolConfig config;

    public static <V> FutureTask<V> submit(Callable<V> callable){
        return submit(callable, "");
    }

    public static <V> FutureTask<V> submit(Callable<V> callable, String taskName){

        if(EXECUTOR == null || config == null){
            throw new RuntimeException(" AsyncFactory is not init");
        }

        if (callable == null){
            throw new NullPointerException("callable is null");
        }

        TraceFutureTask<V> traceFutureTask = new TraceFutureTask<>(callable, taskName);
        EXECUTOR.submit(Objects.requireNonNull(TtlRunnable.get(traceFutureTask)));

        return traceFutureTask;
    }

    public static <V> V get(FutureTask<V> task) throws ExecutionException, InterruptedException, TimeoutException {
        return get(task, 0);
    }

    public static <V> V get(FutureTask<V> task, int timeout) throws ExecutionException, InterruptedException, TimeoutException {

        if(task == null){
            return null;
        }

        if(timeout <= 0 && config.getTimeout() > 0){
            timeout = config.getTimeout();
        }

        if(timeout <= 0){
            return task.get();
        } else {
            return task.get(timeout, TimeUnit.SECONDS);
        }
    }

    /**
     * 初始化线程池
     *
     * @param threadPoolConfig 配置参数
     */
    public static void init(ThreadPoolConfig threadPoolConfig){
        config = threadPoolConfig;
        EXECUTOR = TraceThreadPool.threadPoolExecutor();
        if(config == null){
            config = new ThreadPoolConfig();
        }
    }

    public static void init(){
        config = new ThreadPoolConfig();
        EXECUTOR = TraceThreadPool.threadPoolExecutor();
    }

    public static void destroy(){

        if(EXECUTOR != null && !EXECUTOR.isShutdown()){
            EXECUTOR.shutdown();
        }
    }

    public static ThreadPoolConfig getConfig(){
        return config;
    }

}
