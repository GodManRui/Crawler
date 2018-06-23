package reeiss.bonree.ble_test.blehelp;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import reeiss.bonree.ble_test.blehelp.XFBluetooth.XFBluetoothControl;

public abstract class XFBluetoothCallBack implements XFBluetoothControl {
    @Override
    public void onScanResult(BluetoothDevice device) {

    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {

    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {

    }
}

