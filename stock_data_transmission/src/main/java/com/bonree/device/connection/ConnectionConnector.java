package com.bonree.device.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.bonree.mobile.tools.ThreadPools;

public class ConnectionConnector {
	private ConnectionIoHandler ioHandler;
	
	public void setIOHandler(ConnectionIoHandler handler) {
		this.ioHandler = handler;
	}

	public void connect(String ip, int port) {
		ThreadPools.submit(new Connector(ip, port));
	}
	
	public void shutdown() {
		//TODO Nothing to do!
	}
	
	private class Connector implements Runnable {
		private String ip;
		private int port;
		
		public Connector(String ip, int port) {
			this.ip = ip;
			this.port = port;
		}

		@Override
		public void run() {
			Socket client = new Socket();
			Connection connection = Connection.obtain(client);
			connection.setIOHandler(ioHandler);
			
			try {
				client.connect(new InetSocketAddress(ip, port));
				connection.init();
			} catch (IOException e) {
			}
			
			//这里无视socket连接的异常只是为了通过ConnectionPool来通知连接失败的消息
			ConnectionPool.pool().putConnection(connection);
		}
		
	}
}
