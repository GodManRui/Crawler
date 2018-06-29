package reeiss.bonree.ble_test.bean;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

import static reeiss.bonree.ble_test.bean.PreventLosingCommon.getCHImmediateAlert;
import static reeiss.bonree.ble_test.bean.PreventLosingCommon.getCHOnClick;
import static reeiss.bonree.ble_test.bean.PreventLosingCommon.getServerImmediateAlert;
import static reeiss.bonree.ble_test.bean.PreventLosingCommon.getServerOnClick;

public class CommonHelp {
    /**
     * 获取按键点击
     *
     * @param gatt
     * @return
     */
    public static BluetoothGattCharacteristic getOnClick(BluetoothGatt gatt) {
        UUID server = null;
        server = getServerOnClick();
        if (server != null) {
            BluetoothGattService service = gatt.getService(server);
            if (service != null) {
                return service.getCharacteristic(getCHOnClick());
            }
        }
        return null;
    }

    /**
     * 控制报警
     * @param gatt
     * @return
     */
    public static BluetoothGattCharacteristic getImmediateAlert(BluetoothGatt gatt) {
        UUID server = null;
        server = getServerImmediateAlert();
        if (server != null) {
            BluetoothGattService service = gatt.getService(server);
            if (service != null) {
                return service.getCharacteristic(getCHImmediateAlert());
            }
        }
        return null;
    }
}
