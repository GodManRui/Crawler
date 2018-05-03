package com.bonree.device.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 连接监听服务
 *
 */
public class ConnectionAcceptor {
	private AcceptorOverListener acceptorOverListener;
	
	private Acceptor acceptor;
	
	private ConnectionIoHandler ioHandler;
	
	public void setInetAddress(String ip, int port) {
		this.acceptor = new Acceptor(ip, port);
	}
	
	public void setAcceptorOverListener(AcceptorOverListener threadOverListener) {
		this.acceptorOverListener = threadOverListener;
	}
	
	public void setIOHandler(ConnectionIoHandler handler) {
		this.ioHandler = handler;
	}
	
	/**
	 * 启动连接服务
	 */
	public void launch() {
		if(acceptor == null) {
			throw new RuntimeException("launch failed because InetSocketAddress is not set!");
		}
		
		if(acceptor.isRunning()) {
			throw new IllegalStateException("ConnectionAcceptor is already running!");
		}
		
		Thread main = new Thread(acceptor);
		main.setName("ConnectionAcceptor");
		main.start();
	}
	
	/**
	 * 关闭连接服务
	 */
	public void shutdown() {
		if(acceptor != null) {
			acceptor.quit();
		}
	}
	
	/**
	 * Socket监听程序,接收远端Socket连接
	 * 
	 * @author chen
	 *
	 */
	private class Acceptor implements Runnable {
		private ServerSocket server;
		
		private String ipAddress;
		private int port;
		
		private boolean running;
		private boolean quitRequest;
		
		public Acceptor(String ip, int port) {
			this.ipAddress = ip;
			this.port = port;
			this.running = false;
			this.quitRequest = false;
		}
		
		/**
		 * 当前存在线程执行此任务
		 * 
		 * @return
		 */
		public boolean isRunning() {
			return this.running;
		}
		
		/**
		 * 结束执行此任务的线程
		 */
		public void quit() {
			quitRequest = true;
		}
		
		@Override
		public void run() {
			try {
				running = true;
				
				server = new ServerSocket();
				//为了注册到Selector中必须设置为非阻塞模式
				
				server.setReuseAddress(true);
				server.bind(new InetSocketAddress(ipAddress, port));
				
				while(!quitRequest) {
					Socket client = server.accept();
					
					Connection connection = Connection.obtain(client);
					connection.setIOHandler(ioHandler);
					connection.init();
					
					//添加到连接池中
					ConnectionPool.pool().putConnection(connection);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				boolean exception = !quitRequest;
				
				running = false;
				quitRequest = false;
				
				if(server != null) {
					try {
						server.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					server = null;
				}
				
				//如果已经注册了线程结束监听类，则通知此监听者此线程已经结束
				if(acceptorOverListener != null) {
					acceptorOverListener.acceptorOver(exception);
				}
			}
		}
	}
	
	/**
	 * 监听线程的结束事件
	 *
	 */
	public static interface AcceptorOverListener {
		/**
		 * 线程已经结束
		 * 
		 * @param client
		 */
		void acceptorOver(boolean exception);
	}
}
