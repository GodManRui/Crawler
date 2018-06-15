package reeiss.bonree.ble_test;

import android.app.Application;

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        XFBluetooth.getInstance(this);
    }
}
