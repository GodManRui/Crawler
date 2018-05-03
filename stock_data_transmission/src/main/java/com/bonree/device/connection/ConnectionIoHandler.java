package com.bonree.device.connection;

import com.bonree.connection.pb.ConnectionMessage;

/**
 * 连接IO相关的处理接口
 *
 */
public interface ConnectionIoHandler {
	
	/**
	 * 新的连接建立
	 * 
	 * @param connection
	 */
	void connectionCreated(Connection connection);
	
	/**
	 * 已有连接收到消息
	 * 
	 * @param connection
	 * @param message
	 */
	void messageReceived(Connection connection, ConnectionMessage.Envelope envelope);
	
	/**
	 * 连接已经断开
	 * 
	 * @param connection 断开的连接
	 */
	void connectionClosed(Connection connection);
}
