package com.bonree.device.connection;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.bonree.connection.pb.ConnectionMessage;
import com.bonree.mobile.tools.ThreadPools;
import com.bonree.mobile.tools.TimeoutWheel;
import com.bonree.mobile.tools.TimeoutWheel.Timeout;

/**
 * 连接池
 *
 */
public class ConnectionPool {
	private static final int HEARTBEAT_TIMEOUT = 10;
	private static final int HEARTBEAT_INTERVAL = 3;
	
	private List<Connection> connections = new ArrayList<Connection>();
	
	private TimeoutWheel<Connection> timeoutWatcher = new TimeoutWheel<Connection>(HEARTBEAT_TIMEOUT);
	
	private static ConnectionPool pool = new ConnectionPool();
	
	public static ConnectionPool pool() {
		return pool;
	}
	
	private ConnectionPool() {
		timeoutWatcher.setTimeout(new ConnectionTimeout());
		timeoutWatcher.start();
		ThreadPools.scheduleAtFixedRate(new HeartBeat(), 0, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
	}
	
	/**
	 * 添加新连接
	 * 
	 * @param connection
	 */
	public void putConnection(Connection connection) {
		//读取新连接中的消息
		ThreadPools.submit(new MessageReader(connection));
		//监听心跳消息，适当时候超时
		connections.add(connection);
		timeoutWatcher.update(connection);
	}

	/**
	 * Socket消息读取程序
	 *
	 */
	private class MessageReader implements Runnable {
		private Connection connection;
		
		public MessageReader(Connection connection) {
			this.connection = connection;
		}

		@Override
		public void run() {
			try {
				Socket client = connection.getClient();
				
				while(!connection.isClosed()) {
					ConnectionMessage.Envelope envelope = ConnectionMessage.Envelope.parseDelimitedFrom(client.getInputStream());
					
					if(envelope == null) {
						//获取到的消息为null，此时有可能连接已经断开了，如果连接断开，那么之后再次读取数据还是
						//会直接返回null，所以这里先暂停一小会再进行读取
						try {
							TimeUnit.MILLISECONDS.sleep(500);
						} catch (InterruptedException e) {
							//ignore exception
						}
						
						continue;
					}
					
					//对消息进行预处理
					boolean handled = preHandle(envelope);
					
					//如果消息预处理阶段没有被成功处理，则传递给监听类进行处理
					if(!handled) {
						connection.getIoHandler().messageReceived(connection, envelope);
					}
				}
			} catch (IOException e) {
			}
		}
		
		//消息预处理函数
		private boolean preHandle(ConnectionMessage.Envelope envelope) {
			switch(envelope.getCategory()) {
			case ConnectionMessage.Envelope.CATEGORY.INITIALIZATION_VALUE:
				//连接初始化消息，可以作为连接建立成功的标志
				connection.getIoHandler().connectionCreated(connection);
				return true;
			case ConnectionMessage.Envelope.CATEGORY.HEARTBEAT_VALUE:
				timeoutWatcher.update(connection);
				return true;
			}
			
			return false;
		}
	}
	
	/**
	 * 连接状态检测线程
	 *
	 */
	private class HeartBeat implements Runnable {

		@Override
		public void run() {
			Connection[] tmpConns;
			synchronized(connections) {
				tmpConns = new Connection[connections.size()];
				tmpConns = connections.toArray(tmpConns);
			}
			
			for(Connection conn : tmpConns) {
				conn.beatHeart();
			}
		}
		
	}
	
	private class ConnectionTimeout implements Timeout<Connection> {

		@Override
		public void timeout(Connection connection) {
			synchronized(connections) {
				connections.remove(connection);
			}
			
			connection.close();
			connection.getIoHandler().connectionClosed(connection);
		}
		
	}
}
