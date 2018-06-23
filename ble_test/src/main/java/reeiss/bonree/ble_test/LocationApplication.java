package reeiss.bonree.ble_test;

import android.app.Application;
import android.app.Service;
import android.os.Vibrator;

import com.baidu.mapapi.SDKInitializer;

import reeiss.bonree.ble_test.blehelp.XFBluetooth;

/**
 * Wang YaHui
 * 2018/6/1817:08
 */

public class LocationApplication extends Application {
    public LocationService locationService;
    public Vibrator mVibrator;

    @Override
    public void onCreate() {
        super.onCreate();
        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());
        mVibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());
        XFBluetooth.getInstance(this);
    }
}
