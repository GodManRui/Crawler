package com.bonree.stock.data.storage;

import java.util.concurrent.LinkedBlockingQueue;

import com.bonree.stock.data.pb.DataFormat;

/**
 * 数据仓库
 * 
 * @author chen
 *
 */
public class Storage {
	
	/**
	 * 暂存数据的阻塞队列
	 */
	private LinkedBlockingQueue<DataFormat.JsonParcel> dataQueue = new LinkedBlockingQueue<DataFormat.JsonParcel>();
	
	/**
	 * 存储信息
	 * 
	 * @param data
	 */
	public void store(DataFormat.JsonParcel data) {
		try {
			dataQueue.put(data);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取数据
	 * 
	 * @return
	 * @throws InterruptedException 
	 */
	public DataFormat.JsonParcel fetch() throws InterruptedException {
		return dataQueue.take();
	}
	
	private static Storage mInstance = new Storage();
	
	private Storage() {}
	
	public static Storage instance() {
		return mInstance;
	}
}
