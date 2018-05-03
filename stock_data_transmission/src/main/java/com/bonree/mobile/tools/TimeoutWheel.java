package com.bonree.mobile.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 一个检测超时的转盘类，
 * 
 * @author chenyp
 *
 */
public class TimeoutWheel<T> {
	private ArrayList<Set<T>> slots;
	private int size;
	private int currentIndex;
	
	private ScheduledExecutorService schedThread = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> worker;
	
	private Checker checker = new Checker();
	
	private Map<T, Integer> indexs = new HashMap<T, Integer>();
	
	private Timeout<T> timeout;
	
	/**
	 * 连接超时时间通知
	 * 
	 */
	public static interface Timeout<T> {
		void timeout(T target);
	}
	
	public TimeoutWheel(int timeoutSeconds) {
		if(timeoutSeconds < 2) {
			throw new IllegalArgumentException();
		}
		
		this.size = timeoutSeconds;
		this.currentIndex = 0;
		this.slots = new  ArrayList<Set<T>>(timeoutSeconds);
		for(int i = 0; i < timeoutSeconds; i++) {
			this.slots.add(new HashSet<T>());
		}
	}
	
	public void setTimeout(Timeout<T> timeout) {
		this.timeout = timeout;
	}
	
	public void start() {
		if(worker != null) {
			return;
		}
		
		worker = schedThread.scheduleAtFixedRate(checker, 0, 1, TimeUnit.SECONDS);
	}
	
	public void suspend() {
		if(worker != null) {
			worker.cancel(false);
		}
	}
	
	public void resume() {
		if(worker != null && worker.isCancelled()) {
			worker = schedThread.scheduleAtFixedRate(checker, 0, 1, TimeUnit.SECONDS);
		}
	}
	
	public void stop() {
		schedThread.shutdownNow();
	}

	public void update(T target) {
		synchronized(indexs) {
			int index = indexs.containsKey(target) ? indexs.get(target) : -1;
			if(index >= 0) {
				slots.get(index).remove(target);
			}
			
			int insertIndex = (currentIndex - 1 + size) % size;
			slots.get(insertIndex).add(target);
			indexs.put(target, insertIndex);
		}
	}
	
	private class Checker implements Runnable {
		private ArrayList<T> targets = new ArrayList<T>();

		@Override
		public void run() {
			targets.clear();
			synchronized(indexs) {
				Iterator<T> iter = slots.get(currentIndex).iterator();
				while(iter.hasNext()) {
					targets.add(iter.next());
					iter.remove();
				}
				
				currentIndex = (currentIndex + 1) % size;
			}
			
			for(T target : targets) {
				try {
					if(timeout != null) {
						ThreadPools.submit(new TimeoutExec(target));
					}
				} catch(Exception e){}
			}
		}
	}
	
	private class TimeoutExec implements Runnable {
		private T target;
		
		public TimeoutExec(T target) {
			this.target = target;
		}

		@Override
		public void run() {
			if(timeout != null) {
				timeout.timeout(target);
			}
		}
		
	}
}
