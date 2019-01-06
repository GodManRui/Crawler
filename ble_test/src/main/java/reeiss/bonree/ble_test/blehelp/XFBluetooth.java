package reeiss.bonree.ble_test.blehelp;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.text.TextUtils;
import android.util.Log;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Arrays;

import reeiss.bonree.ble_test.bean.BleDevConfig;
import reeiss.bonree.ble_test.bean.PreventLosingCommon;
import reeiss.bonree.ble_test.utils.T;

public class XFBluetooth {
    public static XFBluetooth xfBluetooth;
    public static String CURRENT_DEV_MAC = "";
    private final Context context;
    public boolean isScaning;          //正在扫描？
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private ArrayList<XFBluetoothCallBack> mListCallBack;
    private ScanCallback callback = getCallback();
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (isScaning)
                for (int i = 0; i < mListCallBack.size(); i++) {
                    mListCallBack.get(i).onScanResult(device, rssi);
                }
        }
    };
    private BluetoothGatt mXFBluetoothGatt;
    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        //链接状态的回调
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.e("jerryzhu", status + " 主类回调连接状态: " + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                CURRENT_DEV_MAC = gatt.getDevice().getAddress();
            }
//            CURRENT_DEV_MAC = (newState == BluetoothProfile.STATE_CONNECTED) ? gatt.getDevice().getAddress() : "";
            if (mListCallBack.size() > 0) {
                for (int i = 0; i < mListCallBack.size(); i++) {
                    mListCallBack.get(i).onConnectionStateChange(gatt, status, newState);
                }
            }
            if (newState != BluetoothProfile.STATE_CONNECTED) {
                CURRENT_DEV_MAC = "";
                PreventLosingCommon.Dev_Type = -1;
                reset();
            }
        }

        //发现服务的回调
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (mListCallBack.size() > 0) {
                for (int i = 0; i < mListCallBack.size(); i++) {
                    mListCallBack.get(i).onServicesDiscovered(gatt, status);
                }
            }
        }


        //读操作的回调
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (mListCallBack.size() > 0) {
                for (int i = 0; i < mListCallBack.size(); i++) {
                    mListCallBack.get(i).onCharacteristicRead(gatt, characteristic, status);
                }
            }
        }

        //写操作的回调
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.e("jerry", "写操作的回调: " + Arrays.toString(characteristic.getValue()) + (status == BluetoothGatt.GATT_SUCCESS ? "成功" : "失败"));
            if (mListCallBack.size() > 0) {
                for (int i = 0; i < mListCallBack.size(); i++) {
                    mListCallBack.get(i).onCharacteristicWrite(gatt, characteristic, status);
                }
            }
        }

        //通知操作的回调（此处接收BLE设备返回数据） 点击返回1
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (mListCallBack.size() > 0) {
                for (int i = 0; i < mListCallBack.size(); i++) {
                    mListCallBack.get(i).onCharacteristicChanged(gatt, characteristic);
                }
            }
        }

        //信号强度回调
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            if (mListCallBack.size() > 0) {
                for (int i = 0; i < mListCallBack.size(); i++) {
                    mListCallBack.get(i).onReadRemoteRssi(gatt, rssi, status);
                }
            }
        }
    };

    private XFBluetooth(Context context) {
        this.context = context;
        //获取蓝牙适配器
        BluetoothManager
                bluetoothManager = (BluetoothManager) context
                .getSystemService(Context.BLUETOOTH_SERVICE);
        assert bluetoothManager != null;
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mListCallBack = new ArrayList<XFBluetoothCallBack>();
    }

    public static XFBluetooth getInstance(Context context) {
        synchronized (XFBluetooth.class) {
            if (xfBluetooth == null) {
                xfBluetooth = new XFBluetooth(context);
            }
            return xfBluetooth;
        }
    }

    public static BleDevConfig getCurrentDevConfig() {
        if (TextUtils.isEmpty(CURRENT_DEV_MAC)) return null;
        return LitePal.where("mac=?", CURRENT_DEV_MAC).findFirst(BleDevConfig.class);
    }

    public static BleDevConfig getMacDevConfig(String mac) {
        if (TextUtils.isEmpty(mac)) return null;
        return LitePal.where("mac=?", mac).findFirst(BleDevConfig.class);
    }

    public boolean isOpenBlueTooth() {
        return mBluetoothAdapter != null && mBluetoothAdapter.enable();
    }

    //判断应用是否已经授权权限
    public static boolean HasPermission(Activity activity) {
//        Utils.checkPermission();
       /* if (VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(activity.getApplicationContext())) {
            Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION");
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            intent.setFlags(268435456);
            activity.startActivity(intent);
        }*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasPermissionLocation = activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            int hasPermissionFineLocation = activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int hasPermission = activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            int hasPermissionBlue = activity.checkSelfPermission(Manifest.permission.BLUETOOTH);
            int hasPermissionAdmin = activity.checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN);
            int hasPermissionCamera = activity.checkSelfPermission(Manifest.permission.CAMERA);

            int hasPermissionDialog = activity.checkSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW);
            if (hasPermissionLocation != PackageManager.PERMISSION_GRANTED || hasPermissionFineLocation != PackageManager.PERMISSION_GRANTED
                    || hasPermission != PackageManager.PERMISSION_GRANTED
                    || hasPermissionBlue != PackageManager.PERMISSION_GRANTED || hasPermissionAdmin != PackageManager.PERMISSION_GRANTED
                    || hasPermissionCamera != PackageManager.PERMISSION_GRANTED
                    || hasPermissionDialog != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,
                                Manifest.permission.CAMERA, Manifest.permission.SYSTEM_ALERT_WINDOW},
                        1006);
            }
        }
        return true;
    }

    public ScanCallback getCallback() {
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            return new ScanCallback() {

                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    if (isScaning && mListCallBack.size() > 0)
                        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                            BluetoothDevice device = result.getDevice();
                            for (int i = 0; i < mListCallBack.size(); i++) {
                                mListCallBack.get(i).onScanResult(device, result.getRssi());
                            }
                        }
                }

                @Override
                public void onScanFailed(int errorCode) {
                    T.show(context.getApplicationContext(), "启动扫描失败:  " + errorCode);
                    Log.e("jerry", "启动扫描失败: " + errorCode);
                    super.onScanFailed(errorCode);
                }
            };
        }
        return null;
    }

    public void reset() {
        try {
            Log.e("jerry", "reset: 重置");
            if (mXFBluetoothGatt != null) {
                mXFBluetoothGatt.close();
                mXFBluetoothGatt = null;
            }
        } catch (Exception e) {
            Log.e("jerryzhu", "mXFBluetoothGatt.close() 异常！！！ " + e.toString());
        }
    }

    public void disconnect() {
        try {
            Log.e("jerry", "reset: 断开");
            if (mXFBluetoothGatt != null) {
                mXFBluetoothGatt.disconnect();
            }
        } catch (Exception e) {
            Log.e("jerryzhu", "mXFBluetoothGatt.close() 异常！！！ " + e.toString());
        }
    }

    public BluetoothGatt getXFBluetoothGatt() {
        return mXFBluetoothGatt;
    }

    public boolean addBleCallBack(XFBluetoothCallBack mXFBluetoothControl) {
        return !mListCallBack.contains(mXFBluetoothControl) && mListCallBack.add(mXFBluetoothControl);
    }

    public boolean removeBleCallBack(XFBluetoothCallBack mXFBluetoothControl) {
        return mListCallBack.remove(mXFBluetoothControl);
    }

    public void scan() {
        isScaning = true;
        if (mBluetoothAdapter == null) {
            T.show(context, "蓝牙未开启或设备不支持蓝牙！");
            return;
        }
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            if (mBluetoothLeScanner == null) {
                T.show(context, "蓝牙未开启或设备不支持蓝牙！");
                return;
            }
            Log.e("JerryZhu", "开始扫描: ");
            mBluetoothLeScanner.startScan(callback);
        } else
            mBluetoothAdapter.startLeScan(mLeScanCallback);

    }

    public void StopScan() {
        if (mBluetoothAdapter == null || !isScaning) {
            return;
        }
        isScaning = false;
        Log.e("JerryZhu", "停止扫描: ");
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP && mBluetoothLeScanner != null) {
            mBluetoothLeScanner.stopScan(callback);
        } else if (mBluetoothAdapter != null) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    public void connect(BluetoothDevice device) {
        if (device != null) {
            mXFBluetoothGatt = device.connectGatt(context, true, gattCallback);
        }
    }

    public void connect(String mac) {
        if (mac != null && !TextUtils.isEmpty(mac)) {
            BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(mac);
            mXFBluetoothGatt = remoteDevice.connectGatt(context, false, gattCallback);
            Log.e("jerry", "正在连接: " + Thread.currentThread().getName());
        }
    }

    public BluetoothAdapter getAdapter() {
        return mBluetoothAdapter;
    }

    interface XFBluetoothControl {
        //发现设备的回调
        void onScanResult(BluetoothDevice device, int rssi);

        //发现服务的回调
        void onConnectionStateChange(BluetoothGatt gatt, int status, int newState);

        //发现服务的回调
        void onServicesDiscovered(BluetoothGatt gatt, int status);

        //读操作的回调
        void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

        //写操作的回调
        void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

        //通知操作的回调（此处接收BLE设备返回数据） 点击返回1
        void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);

        //信号强度回调
        void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status);
    }
}
