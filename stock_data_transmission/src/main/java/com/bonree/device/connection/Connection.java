package com.bonree.device.connection;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import com.bonree.connection.pb.ConnectionMessage;
import com.bonree.connection.pb.ConnectionMessage.Envelope;

public class Connection {
	//唯一标志ID号
	private int ID;
	
	private Socket client;
	
	private ConnectionIoHandler ioHandler;
	
	private boolean closed;
	
	private static ArrayList<Connection> cycledConnections = new ArrayList<Connection>();
	
	private Connection() {}
	
	public static Connection obtain(Socket client) {
		Connection conn = null;
		if(!cycledConnections.isEmpty()) {
			conn = cycledConnections.remove(0);
		}
		
		if(conn == null) {
			conn = new Connection();
		}
		
		conn.ID = IDFactory.next();
		conn.client = client;
		conn.closed = false;
		conn.ioHandler = emptyConnetionIoHandler;
		
		return conn;
	}
	
	public void setIOHandler(ConnectionIoHandler handler) {
		this.ioHandler = handler != null ? handler : emptyConnetionIoHandler;
	}
	
	public ConnectionIoHandler getIoHandler() {
		return this.ioHandler;
	}
	
	public int getId() {
		return ID;
	}
	
	public Socket getClient() {
		return client;
	}
	
	public boolean isClosed() {
		return this.closed;
	}
	
	public void close() {
		if(this.closed) {
			return;
		}
		
		this.closed = true;
		try {
			client.close();
		} catch (IOException e) {}
		
		cycledConnections.add(this);
	}
	
	public boolean send(ConnectionMessage.Envelope envelope) {
		try {
			envelope.writeDelimitedTo(client.getOutputStream());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	//向对端发送初始化信息
	void init() {
		ConnectionMessage.Initialization.Builder initBuilder = ConnectionMessage.Initialization.newBuilder();
		initBuilder.setInitID(ID);

		ConnectionMessage.Envelope.Builder enveBuilder = ConnectionMessage.Envelope
				.newBuilder();
		enveBuilder
				.setCategory(ConnectionMessage.Envelope.CATEGORY.INITIALIZATION_VALUE);
		enveBuilder.setContent(initBuilder.build().toByteString());

		send(enveBuilder.build());
	}
	
	void beatHeart() {
		ConnectionMessage.Heartbeat.Builder hbBuilder = ConnectionMessage.Heartbeat.newBuilder();
		hbBuilder.setID(ID);
		ConnectionMessage.Envelope.Builder signalBuilder = ConnectionMessage.Envelope.newBuilder();
		signalBuilder.setCategory(ConnectionMessage.Envelope.CATEGORY.HEARTBEAT_VALUE)
				     .setContent(hbBuilder.build().toByteString());
		
		send(signalBuilder.build());
	}
	
	private static ConnectionIoHandler emptyConnetionIoHandler = new ConnectionIoHandler() {

		@Override
		public void connectionCreated(Connection connection) {
		}

		@Override
		public void messageReceived(Connection connection, Envelope envelope) {
		}

		@Override
		public void connectionClosed(Connection connection) {
		}
		
	};
}
