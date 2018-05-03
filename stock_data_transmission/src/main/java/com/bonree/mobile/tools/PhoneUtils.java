package com.bonree.mobile.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class PhoneUtils {

	public static boolean isWifiState() {
		return networkType == ConnectivityManager.TYPE_WIFI;
	}
	
	public static boolean isSimReady() {
		return simState == TelephonyManager.SIM_STATE_READY;
	}
	
	public static String simCountry() {
		if(mcc == 460) {
			return "中国";
		}
		
		return "其他国家";
	}
	
	public static String simNetWork() {
		if(mcc == 460) {
			if(mnc == 00 || mnc == 02) {
				return "移动";
			} else if(mnc == 01) {
				return "联通";
			} else if(mnc == 03) {
				return "电信";
			}
		}
		
		return "未知网络";
	}
	
	public static void init(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		
		//默认没有网络连接
		networkType = -1;
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if(networkInfo != null && networkInfo.isConnected()) {
			networkType = networkInfo.getType();
		}
		
		//获取sim卡状态
		simState = tm.getSimState();
		
		mcc = -1;
		mnc = -1;
		if(isSimReady()) {
			String mccAndmnc = tm.getSimOperator();
			if(mccAndmnc != null && mccAndmnc.length() == 5) {
				mcc = Integer.valueOf(mccAndmnc.substring(0, 3));
				mnc = Integer.valueOf(mccAndmnc.substring(3));
			}
		}
	}
	
	private static int networkType;
	private static int simState;
	//Mobile Country Code
	private static int mcc;
	//Mobile Network Code
	private static int mnc;
}
