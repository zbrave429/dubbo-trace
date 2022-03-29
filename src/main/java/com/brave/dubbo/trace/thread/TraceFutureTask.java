package com.brave.dubbo.trace.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * AsyncFutureTask
 *
 * @author <a href='1286998496@qq.com'>zhangyong</a>
 * @date 2022-01-09 11:04
 */
public class TraceFutureTask<V> extends FutureTask<V> {

    private static final Logger logger = LoggerFactory.getLogger(TraceFutureTask.class);

    private final String taskName;

    public TraceFutureTask(Callable<V> callable, String taskName) {
        super(callable);
        this.taskName = taskName;
    }

    public TraceFutureTask(Runnable runnable, V result, String taskName) {
        super(runnable, result);
        this.taskName = taskName;
    }

    @Override
    protected void done() {
        super.done();
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        try{
            super.run();
        } finally {
            if (logger.isDebugEnabled()){
                logger.debug("task:{} run end, used time {} ms", taskName, (System.currentTimeMillis() - startTime));
            }
        }
    }
}
