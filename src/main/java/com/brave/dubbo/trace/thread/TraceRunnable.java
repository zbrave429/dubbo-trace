package com.brave.dubbo.trace.thread;

/**
 *
 *
 * @author <a href='1286998496@qq.com'>zhangyong</a>
 * @date 2022-03-20 13:55
 */
public class TraceRunnable implements Runnable{

    private Runnable runnable;

    public TraceRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        // TODO 设置traceContext

        try {
            runnable.run();
        } finally {
            // TODO 清除traceContext
        }
    }

    public static TraceRunnable getRunnable(Runnable runnable) {
        return new TraceRunnable(runnable);
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }
}
