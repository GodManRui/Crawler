package com.bonree.device.connection;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ID生成工具类
 *
 */
public class IDFactory {
	
	private static AtomicInteger base = new AtomicInteger(1);
	
	/**
	 * 返回下一个可用ID
	 * @return ID号
	 */
	public static int next() {
		return base.getAndIncrement();
	}
}
