package com.bonree.mobile.tools;

import java.lang.reflect.Method;

import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.os.IPowerManager;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.view.IWindowManager;

public class WakeTools {
	private static IBinder wlb = new Binder();
	private static IBinder unlockwlb = new Binder();
	private static IBinder iwmbinder = ServiceManager
			.getService(Context.WINDOW_SERVICE);

	/**
	 * 通过反射的方式调用IPowerManager中唤醒的方法， Android 4.1以前acquireWakeLock的参数和Android4.2后
	 * 的参数不同，所以必须采用此方法动态代理调用
	 * 
	 * @param ipm
	 * @param wlb
	 * @return
	 */
	public static final boolean wakeFormIPowerManagerRe() {
		IPowerManager ipm = IPowerManager.Stub.asInterface(ServiceManager
				.getService(Context.POWER_SERVICE));
		try {
			// 判断SDK版本执行相应操作
			Class<?> ipmClass = ipm.getClass();
			int version = android.os.Build.VERSION.SDK_INT;
			Method[] method = ipmClass.getDeclaredMethods();
			for (Method methodtmp : method) {
				methodtmp.setAccessible(true);
				if (methodtmp.toString().contains("acquireWakeLock")) {
					methodtmp.setAccessible(true);
					// mohan 20150604 5.0以上系统处理
					if (version > 20) {
						methodtmp.invoke(ipm, wlb,
								PowerManager.ACQUIRE_CAUSES_WAKEUP
										| PowerManager.SCREEN_DIM_WAKE_LOCK,
								"phoneserverlock", "", null, "");
					} else if (version >= 19 && version <= 20) {
						// 4.4 以上
						methodtmp.invoke(ipm, wlb,
								PowerManager.ACQUIRE_CAUSES_WAKEUP
										| PowerManager.SCREEN_DIM_WAKE_LOCK,
								"phoneserverlock", "", null);
					} else if (version >= 17 && version < 19) {
						// 4.2-4.3
						methodtmp.invoke(ipm, wlb,
								PowerManager.ACQUIRE_CAUSES_WAKEUP
										| PowerManager.SCREEN_DIM_WAKE_LOCK,
								"phoneserverlock", null);
					} else if (version <= 16) {
						// 4.1以下
						methodtmp.invoke(ipm,
								PowerManager.ACQUIRE_CAUSES_WAKEUP
										| PowerManager.SCREEN_DIM_WAKE_LOCK,
								wlb, "phoneserverlock", null);
					}
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		ipm = null;
		return isScreenOn();
	}

	public static void disablePhoneKeyguard() {
		IWindowManager iwm = IWindowManager.Stub.asInterface(iwmbinder);
		try {
			//此方法都可以让手机解锁,并且调用此函数后keyguard的锁定状态才会改变,但发现部分手机上解不了锁
			iwm.dismissKeyguard();
			//这个不会改变keyguard的状态,只能通过解析dumpsys window信息判断，但有一个好处是，diable之后即使按power键也不会解锁（但会黑屏）
			iwm.disableKeyguard(unlockwlb, "unLock");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解锁
	 * 
	 * @author mohan
	 * @time 2014年10月16日
	 */
	public static void wakePhone() {
		new Thread() {
			@Override
			public void run() {
				wakeFormIPowerManagerRe();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				disablePhoneKeyguard();
				try {
					if (!isScreenOn()) {
						wakeFormIPowerManagerRe();
						Thread.sleep(2000);
					}
					disablePhoneKeyguard();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}.start();

	}

	/**
	 * 释放手机资源
	 */
	public static void realsePhone() {
		IPowerManager ipm = IPowerManager.Stub.asInterface(ServiceManager
				.getService(Context.POWER_SERVICE));
		IWindowManager iwm = IWindowManager.Stub.asInterface(iwmbinder);
		try {
			ipm.releaseWakeLock(wlb, PowerManager.ACQUIRE_CAUSES_WAKEUP
					| PowerManager.SCREEN_DIM_WAKE_LOCK);
//			ipm.releaseWakeLock(wlb, PowerManager.ACQUIRE_CAUSES_WAKEUP
//					| PowerManager.SCREEN_DIM_WAKE_LOCK);
			iwm.reenableKeyguard(unlockwlb);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		ipm = null;
		iwm = null;
	}

	/**
	 * 判断屏幕是否亮起
	 * 
	 * @return
	 * @author mohan
	 * @time 2014年12月31日
	 */
	public static boolean isScreenOn() {
		boolean isScreenOn = true;
		IPowerManager ipm = IPowerManager.Stub.asInterface(ServiceManager
				.getService(Context.POWER_SERVICE));
		try {
			if(android.os.Build.VERSION.SDK_INT < 20){
				isScreenOn = ipm.isScreenOn();
			}else{
				Method method = ipm.getClass().getMethod("isInteractive");
				method.setAccessible(true);
				return (Boolean)(method.invoke(ipm));
//				Method[] methods = ipm.getClass().getDeclaredMethods();
//				for(Method method : methods){
//					if(method.toString().equals("isInteractive")){
//						method.setAccessible(true);
//						method.invoke(ipm);
//					}
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			isScreenOn = false;
		}
		return isScreenOn;
	}

}
