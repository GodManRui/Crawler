package com.bonree.mobile.tools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ThreadPools {
	
	private static ExecutorService cachedPool = Executors.newCachedThreadPool();
	
	private static final int scheduledPoolSize = 5;
	private static ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(scheduledPoolSize);
	
	public static Future<?> submit(Runnable command) {
		return cachedPool.submit(command);
	}
	
	public static ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
		return scheduledPool.scheduleAtFixedRate(command, initialDelay, period, unit);
	}
}
