package com.brave.dubbo.trace.thread;

import com.alibaba.ttl.threadpool.TtlExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * DefaultThreadPool
 *
 * @author <a href='1286998496@qq.com'>zhangyong</a>
 * @date 2022-01-09 10:15
 */
public class TraceThreadPool{

	private static final Logger logger = LoggerFactory.getLogger(TraceFutureTask.class);

	public static ExecutorService threadPoolExecutor(){

		int processors = Runtime.getRuntime().availableProcessors();
		int max = 5 * processors;
		long keepAliveTime = 3600L;
		int blockSize = 200;
		
		return threadPoolExecutor(
				processors, max, keepAliveTime, TimeUnit.SECONDS,
				blockSize,
				new DefaultThreadFactory());
	}

	public static ExecutorService threadPoolExecutor(int corePoolSize,
										    int maximumPoolSize,
										    long keepAliveTime,
										    TimeUnit unit,
										    int blockSize,
										    ThreadFactory threadFactory){

		logger.info("ASYNC-TASK-POOL-THREAD init ：corePoolSize={},\nmaximumPoolSize={},\nkeepAliveTime={},\nblockSize={}"
				, corePoolSize ,maximumPoolSize ,keepAliveTime , blockSize);

		return TtlExecutors.getTtlExecutorService(new ThreadPoolExecutor(
				corePoolSize, maximumPoolSize, keepAliveTime, unit,
				new ArrayBlockingQueue<>(blockSize),
				threadFactory,
				new DefaultPolicy())
		);
	}

	private static class DefaultPolicy implements RejectedExecutionHandler {

		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			logger.warn("queue full, " + executor.toString());
			if (!executor.isShutdown()) {
				r.run();
			}
		}
	}
}
