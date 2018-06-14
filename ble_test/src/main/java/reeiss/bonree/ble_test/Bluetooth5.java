package reeiss.bonree.ble_test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.support.annotation.RequiresApi;
import android.util.Log;

@RequiresApi(api = VERSION_CODES.LOLLIPOP)
public class Bluetooth5 {
    private Context context;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private GetBluetoothDevice iReturnDevice;
    private ScanCallback callback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            // ScanSettings.callback
            Log.e("JerryZhu", "onScanResult: " + callbackType + "   名字=" + device.getName());
            if (device.getName() != null && device.getName().contains("iTAG")) {
                stop();
                iReturnDevice.GetDevice(device);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    public Bluetooth5(Context context, BluetoothAdapter mBluetoothAdapter) {
        this.context = context;
        this.iReturnDevice = (GetBluetoothDevice) context;
        this.mBluetoothAdapter = mBluetoothAdapter;
    }

    public void start() {
        bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        bluetoothLeScanner.startScan(callback);
    }

    public void stop() {
        if (bluetoothLeScanner != null) {
            Log.e("JerryZhu", "停止扫码: ");
            bluetoothLeScanner.stopScan(callback);
        }
    }

    interface GetBluetoothDevice {
        void GetDevice(BluetoothDevice device);
    }
}
