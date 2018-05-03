package com.bonree.stock.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bonree.connection.pb.ConnectionMessage.ExceptionData;
import com.bonree.device.connection.ConnectionAcceptor;
import com.bonree.device.connection.ConnectionAcceptor.AcceptorOverListener;
import com.bonree.mobile.base.R;
import com.bonree.mobile.tools.WakeTools;
import com.bonree.stock.data.io.DataDistributor;
import com.bonree.stock.data.pb.DataFormat;
import com.bonree.stock.data.storage.Storage;
import com.bonree.stock.service.iface.IDataUpload;

/**
 * 负责收集证券app中最新更新的证券数据信息
 * 
 * @author chen
 *
 */
public class DataCollectionService extends Service {
	private static final String TAG = "DataCollectionService";
	
	private static final String EXTRA_PACKAGE_NAME = "extra_package_name";
	private static final String EXTRA_PLUGIN_NAME = "extra_plugin_name";
	private static final String EXTRA_PORT = "extra_port";
	
	private static final String LOCALHOST = "127.0.0.1";
	
	//状态栏通知组件的ID号
	private static final int NOTIFICATION_ID = 1;
	private Notification mNotification;
	
	private ConnectionAcceptor connectionAcceptor;
	private DataDistributor dataDistributor;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.i(TAG, "oncreate");
		
		WakeTools.wakePhone();
		
		//设置通知界面参数
		mNotification = new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.ic_notification)
		.setContentTitle(getResources().getString(R.string.notification_content_title))
		.setContentText(getResources().getString(R.string.notification_ready))
		.setWhen(System.currentTimeMillis())
		.build();
		
		//设置服务为前台服务
		//这里ID不能是0，不然可能会出错
		startForeground(NOTIFICATION_ID, mNotification);
		
		dataDistributor = new DataDistributor();
		
		connectionAcceptor = new ConnectionAcceptor();
		connectionAcceptor.setIOHandler(dataDistributor);
		connectionAcceptor.setAcceptorOverListener(new AcceptorOverHandler());
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		dataDistributor.launch();
		
		//这里可以根据intent中的参数获取连接的端口号
		int port = intent.getIntExtra(EXTRA_PORT, 8086);
		connectionAcceptor.setInetAddress(LOCALHOST, port);
		connectionAcceptor.launch();
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		String remotePackage = intent.getStringExtra(EXTRA_PACKAGE_NAME);
		String remoteAppName = intent.getStringExtra(EXTRA_PLUGIN_NAME);
		
		Log.i(TAG, "onBind --> " + remotePackage);
		Log.i(TAG, "onBind --> " + remoteAppName);
		
		return new DataUploader();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		connectionAcceptor.setAcceptorOverListener(null);
		connectionAcceptor.shutdown();
		
		WakeTools.realsePhone();
	}
	
	private class DataUploader extends IDataUpload.Stub {

		@Override
		public void diliverData(String jsonData)
				throws RemoteException {
			Log.i(TAG, "received--" + jsonData);
			
			DataFormat.JsonParcel.Builder parcel = DataFormat.JsonParcel.newBuilder();
			parcel.setId(System.currentTimeMillis());
			parcel.setJson(jsonData);
			
			Storage.instance().store(parcel.build());
		}

		@Override
		public void exceptionCaught(String message) throws RemoteException {
			ExceptionData.Builder builder = ExceptionData.newBuilder();
			builder.setDetail(message);
			dataDistributor.sendException(builder.build());
		}
		
	}

	private class AcceptorOverHandler implements AcceptorOverListener {

		@Override
		public void acceptorOver(boolean exception) {
			Log.i(TAG, "Connection acceptor is shutdown, restart...");
			
			//如果是异常终止，则重启线程
			if(exception) {
				connectionAcceptor.launch();
			}
		}
		
	}
}
