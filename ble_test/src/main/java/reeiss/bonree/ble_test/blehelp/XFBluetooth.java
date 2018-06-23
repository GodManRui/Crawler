package reeiss.bonree.ble_test.blehelp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import reeiss.bonree.ble_test.utils.T;

public class XFBluetooth {
    public static XFBluetooth xfBluetooth;
    private final BluetoothAdapter mBluetoothAdapter;
    private final Context context;
    private BluetoothLeScanner mBluetoothLeScanner;
    private boolean isStopCall;
    private ArrayList<XFBluetoothCallBack> mListCallBack;
    private ScanCallback callback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (isStopCall && mListCallBack.size() > 0)
                if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                    BluetoothDevice device = result.getDevice();
                    for (int i = 0; i < mListCallBack.size(); i++) {
                        mListCallBack.get(i).onScanResult(device);
                    }
                }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (isStopCall)
                for (int i = 0; i < mListCallBack.size(); i++) {
                    mListCallBack.get(i).onScanResult(device);
                }
        }
    };
    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        //链接状态的回调
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (mListCallBack.size() > 0) {
                for (int i = 0; i < mListCallBack.size(); i++) {
                    mListCallBack.get(i).onConnectionStateChange(gatt, status, newState);
                }
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
            Log.e("jerry", "写操作的回调: " + Arrays.toString(characteristic.getValue()));
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
    private BluetoothGatt mXFBluetoothGatt;

    private XFBluetooth(Context context) {
        this.context = context;
        //获取蓝牙适配器
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        assert bluetoothManager != null;
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mListCallBack = new ArrayList();
    }

    public static XFBluetooth getInstance(Context context) {
        synchronized (XFBluetooth.class) {
            if (xfBluetooth == null) {
                xfBluetooth = new XFBluetooth(context);
            }
            return xfBluetooth;
        }
    }

    public BluetoothGatt getXFBluetoothGatt() {
        return mXFBluetoothGatt;
    }

    public boolean addBleCallBack(XFBluetoothCallBack mXFBluetoothControl) {
        return mListCallBack.add(mXFBluetoothControl);
    }

    public boolean removeBleCallBack(XFBluetoothCallBack mXFBluetoothControl) {
        return mListCallBack.remove(mXFBluetoothControl);
    }


    public void scan() {
        isStopCall = true;
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
            mBluetoothLeScanner.startScan(callback);
        } else
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering())
                    stop();
            }
        }, 20000);
    }

    public void stop() {
        isStopCall = false;
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP && mBluetoothLeScanner != null) {
            Log.e("JerryZhu", "停止扫描: ");
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

    public BluetoothAdapter getAdapter() {
        return mBluetoothAdapter;
    }

    interface XFBluetoothControl {
        //发现设备的回调
        void onScanResult(BluetoothDevice device);

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
