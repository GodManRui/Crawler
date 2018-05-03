package com.bonree.stock.data.io;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import android.util.Log;

import com.bonree.connection.pb.ConnectionMessage;
import com.bonree.connection.pb.ConnectionMessage.Envelope;
import com.bonree.device.connection.Connection;
import com.bonree.device.connection.ConnectionIoHandler;
import com.bonree.mobile.tools.ThreadPools;
import com.bonree.stock.data.pb.DataFormat;
import com.bonree.stock.data.pb.DataFormat.DataAnswer;
import com.bonree.stock.data.storage.Storage;
import com.google.protobuf.InvalidProtocolBufferException;

public class DataDistributor implements ConnectionIoHandler {
	
	private Connection mConnection;
	private Object o = new Object();
	
	private static final int ANSWER_TIMEOUT = 5;
	private ReentrantLock lock = new ReentrantLock();
	private Condition dataAnswer = lock.newCondition();
	
	private DataFormat.JsonParcel currentData;
	
	public void launch() {
		ThreadPools.submit(new Distribution());
	}
	
	private class Distribution implements Runnable {
		
		private DataFormat.JsonParcel unSendData;

		@Override
		public void run() {
			while(true) {
				try {
					Connection currentConnection = mConnection;
					
					if(currentConnection == null) {
						//等待连接建立
						synchronized(o) {
							if(currentConnection == null) {
								try {
									o.wait(10 * 1000);
								} catch (InterruptedException e) {
								}
								
								continue;
							}
						}
					}
					
					//从仓库中提取数据，可能阻塞
					currentData = unSendData != null ? unSendData : Storage.instance().fetch();
					
					//重置变量
					unSendData = null;
					
					if(currentData != null) {
						ConnectionMessage.Envelope.Builder enveBuilder = ConnectionMessage.Envelope.newBuilder();
						enveBuilder.setCategory(ConnectionMessage.Envelope.CATEGORY.JSONPARCEL_VALUE);
						enveBuilder.setContent(currentData.toByteString());
						
						currentConnection.send(enveBuilder.build());
						
						boolean answered = false;
						lock.lock();
						try {
							answered = dataAnswer.await(ANSWER_TIMEOUT, TimeUnit.SECONDS);
						} finally {
							lock.unlock();
						}
						
						if(!answered) {
							//消息发送失败，记录发送失败的消息
							unSendData = currentData;
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public void sendException(ConnectionMessage.ExceptionData e) {
		if(mConnection != null) {
			ConnectionMessage.Envelope.Builder enveBuilder = ConnectionMessage.Envelope.newBuilder();
			enveBuilder.setCategory(ConnectionMessage.Envelope.CATEGORY.EXCEPTION_VALUE);
			enveBuilder.setContent(e.toByteString());
			
			mConnection.send(enveBuilder.build());
		}
	}
	
	@Override
	public void connectionCreated(Connection connection) {
		Log.i("TestC", "-----connectionCreated----");
		mConnection = connection;
		synchronized(o) {
			o.notifyAll();
		}
	}

	@Override
	public void messageReceived(Connection connection, Envelope envelope) {
		switch(envelope.getCategory()) {
		case Envelope.CATEGORY.DATAANSWER_VALUE: {
			try {
				DataAnswer answer = DataAnswer.parseFrom(envelope.getContent());
				if(answer.getId() == currentData.getId()) {
					lock.lock();
					try {
						dataAnswer.signalAll();;
					} finally {
						lock.unlock();
					}
				}
			} catch (InvalidProtocolBufferException e) {
				e.printStackTrace();
			}
		}
			break;
		default:
			break;
		}
	}

	@Override
	public void connectionClosed(Connection connection) {
		Log.i("TestC", "-----connectionClosed----");
		if(connection.getId() == mConnection.getId()) {
			mConnection = null;
		}
	}
}
