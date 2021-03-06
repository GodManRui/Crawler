package reeiss.bonree.ble_test.bean;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
/*
 *//**
 * The profile is in disconnected state
 *//*
public static final int STATE_DISCONNECTED  = 0;
*//** The profile is in connecting state *//*
public static final int STATE_CONNECTING    = 1;
*//** The profile is in connected state *//*
public static final int STATE_CONNECTED     = 2;
*/

/** The profile is in disconnecting state *//*
public static final int STATE_DISCONNECTING = 3;*/
public class DeviceListBean {
    private BluetoothDevice bluetoothDevice;
    private int ConnectState;
    private String devNick = "";

    public String getDevNick() {
        return devNick;
    }

    public void setDevNick(String devNick) {
        this.devNick = devNick;
    }

    public DeviceListBean(BluetoothDevice bluetoothDevice, int connectState, String devNick) {
        this.bluetoothDevice = bluetoothDevice;
        ConnectState = connectState;
        this.devNick = devNick;
    }

    public DeviceListBean(BluetoothDevice bluetoothDevice, int connectState) {
        this.bluetoothDevice = bluetoothDevice;
        ConnectState = connectState;
    }


    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public String getConnectState() {
        switch (ConnectState) {
            case BluetoothGatt.STATE_DISCONNECTED:
                return "未连接";
            case BluetoothGatt.STATE_CONNECTED:
                return "已连接";
            case BluetoothGatt.STATE_CONNECTING:
                return "正在连接..";
        }
        return "";
    }

    public void setConnectState(int connectState) {
        ConnectState = connectState;
    }
}
