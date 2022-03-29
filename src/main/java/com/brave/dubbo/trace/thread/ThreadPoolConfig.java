package com.brave.dubbo.trace.thread;

/**
 * 全局配置
 *
 * @author <a href='1286998496@qq.com'>zhangyong</a>
 * @date 2022-01-09 11:58
 */
public class ThreadPoolConfig {

    /**
     * 兜底超时时间 <=0 :无超时时间
     * 单位：秒
     */
    private int timeout;

    /**
     * 线程前缀
     */
    private String threadPrefix = "ASYNC-TASK-POOL-THREAD-";

    public ThreadPoolConfig(int timeout, String threadPrefix) {
        this.timeout = timeout;
        this.threadPrefix = threadPrefix;
    }

    public ThreadPoolConfig(int timeout) {
        this.timeout = timeout;
    }

    public ThreadPoolConfig() {
    }

    public int getTimeout() {
        return timeout;
    }

    public ThreadPoolConfig setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public String getThreadPrefix() {
        return threadPrefix;
    }

    public ThreadPoolConfig setThreadPrefix(String threadPrefix) {
        this.threadPrefix = threadPrefix;
        return this;
    }
}
