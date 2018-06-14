package reeiss.bonree.ble_test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.Arrays;
import java.util.List;

import reeiss.bonree.ble_test.Bluetooth5.GetBluetoothDevice;


public class BlueActivity extends AppCompatActivity implements GetBluetoothDevice {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic alert;
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        //连接状态改变的回调
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // 连接成功后启动服务发现
                Log.e("jerryzhu", "启动服务发现:" + mBluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED)
                Log.e("JerryZhu", "链接已断开：" + newState);
        }

        //发现服务的回调
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //得到所有Service
                List<BluetoothGattService> supportedGattServices = gatt.getServices();

                for (BluetoothGattService gattService : supportedGattServices) {
                    //得到每个Service的Characteristics
                    List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                    Log.e("JerryZhu", "当前服务： " + gattService.getUuid() + "    Type= " + gattService.getType());
                    for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                        int charaProp = gattCharacteristic.getProperties();
                        Log.e("JerryZhu", "当前Characteristics： " + charaProp);
                        //所有Characteristics按属性分类
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            Log.e("jerryzhu", " Characteristic的UUID为:" + gattCharacteristic.getUuid() + "  可读");
                            if (gattCharacteristic.getUuid().toString().contains("2a19")) {
                                Log.e("JerryZhu", "找到battery UUID ！: ");
                                mBluetoothGatt.readCharacteristic(gattCharacteristic);
                            }
                            //  readUuid.add(gattCharacteristic.getUuid());
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                            Log.e("jerryzhu", " Characteristic的UUID为:" + gattCharacteristic.getUuid() + "  可写");
                            if (gattCharacteristic.getUuid().toString().contains("2a06")) {
                                alert = gattCharacteristic;
                                Log.e("JerryZhu", "找到alert UUID ！: ");
                            }
//                            writeUuid.add(gattCharacteristic.getUuid());
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            Log.e("jerryzhu", " Characteristic的UUID为:" + gattCharacteristic.getUuid() + gattCharacteristic + "    通知");

                            if (gattCharacteristic.getUuid().toString().contains("ffe1")) {
                                mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
                                Log.e("JerryZhu", "找到点击推送 UUID ！: ");
                            }
//                            notifyUuid.add(gattCharacteristic.getUuid());
                        }
                    }
                }
            } else {
                Log.e("jerryzhu", "onServicesDiscovered received: " + status);
            }

        }


        //读操作的回调
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e("jerryzhu", "读取成功 电量= " + Arrays.toString(characteristic.getValue()));
            }
        }

        //写操作的回调
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e("jerryzhu", "写入命令成功：" + Arrays.toString(characteristic.getValue()));
            }
        }

        //通知操作的回调（此处接收BLE设备返回数据） 点击返回1
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.e("jerryzhu", "数据返回 " + Arrays.toString(characteristic.getValue()));
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            Log.e("JerryZhu", "信号 : " + rssi);
        }
    };
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.e("JerryZhu", "onLeScan: " + device.getName() + "  rssi=" + rssi + " address:" + device.getAddress());
            if (device.getName() != null && device.getName().contains("iTAG")) {
                Log.e("JerryZhu", "onLeScan: 停止！");
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                connectBle(device);
            }
        }
    };
    private Bluetooth5 bluetooth5;

    @RequiresApi(api = VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bllue);
        //获取蓝牙适配器
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        bluetooth5 = new Bluetooth5(this, mBluetoothAdapter);

    }

    public void start(View v) {
        Log.e("JerryZhu", "start: 开始扫描");
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            bluetooth5.start();
        } else
            mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    public void stop(View view) {
        if (mBluetoothAdapter != null)
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    public void alert(View view) {
        Log.e("JerryZhu", "alert UUID: " + alert.getUuid());
        alert.setValue(new byte[]{0x01});
        mBluetoothGatt.writeCharacteristic(alert);
        Log.d("jerryzhu", "发送数据成功");
    }

    @Override
    public void GetDevice(BluetoothDevice device) {
        Log.e("JerryZhu", "5.0++获取到设备: " + device.getName());
        connectBle(device);
    }

    private void connectBle(BluetoothDevice device) {
        //第一个参数：上下文，第二个参数：断开连接是否重连，第三个
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
    }
}
