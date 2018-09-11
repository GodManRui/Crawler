package reeiss.bonree.ble_test.bean;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

public class PreventLosingCommon {
    public static final int Dev_Type_Shuidi = 100;
    static final String Server_ShuiDi = "00001802-0000-1000-8000-00805f9b34fb";
    static final String Server_Rectangle = "00001804-0000-1000-8000-00805f9b34fb";
    static final int Dev_Type_Rectangle = 200;
    //电池电量显示
    public static String Server_Battery_Level = "0000180f-0000-1000-8000-00805f9b34fb";
    public static String CH_Battery_Level = "00002a19-0000-1000-8000-00805f9b34fb";
    //立即使能报警
    public static String Server_Immediate_Alert_ShuiDi = "00001802-0000-1000-8000-00805f9b34fb";
    public static String CH_Immediate_Alert_ShuiDi = "00002a06-0000-1000-8000-00805f9b34fb";
    public static String CH_Immediate_Alert_Rectangle = "0000ffe4-0000-1000-8000-00805f9b34fb";
    public static byte Common_High_immediate_Alert = 0x01; //High immediateAlertLevel
    public static byte Common_Middling_immediate_Alert = 0x03; //Middling immediateAlertLevel
    public static byte Common_No_immediate_Alert = 0x00; //No immediateAlertLevel
    // PrivateServices  私有/自定义 服务
    public static String Server_ShuiDi_Private = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static String Server_Rectangle_Custom = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static String Server_Rectangle_Private = "0000ffe0-0000-1000-8000-00805f9b34fb";

    //按键消息监听   单击收到0x01       0x02双击
    public static String CH_ShuiDi_Key_Press = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static String CH_Rectangle_Key_Press = "0000ffe2-0000-1000-8000-00805f9b34fb";

    //断开连接是否报警  0x01 报警  0x00不报警
    public static String Server_LinkLost_Alert_ShuiDi = "00001803-0000-1000-8000-00805f9b34fb";
    public static String CH_LinkLost_Alert_ShuiDi = "00002a06-0000-1000-8000-00805f9b34fb";

    public static String CH_LinkLost_Alert_Rectangle = "0000ffe3-0000-1000-8000-00805f9b34fb";

    public static byte Common_LinkLost_100Alert = 0x01;
    public static byte Common_LinkLost_200Alert = 0x02;
    public static int Common_LinkLost_No_Alert = 0xFF;
    public static int Dev_Type = -1;

    //获得断开后是否报警的Server
    public static UUID getServerLinkLostAlert() {
        switch (Dev_Type) {
            case Dev_Type_Shuidi:
                return UUID.fromString(Server_LinkLost_Alert_ShuiDi);
            case Dev_Type_Rectangle:
                return UUID.fromString(Server_Rectangle_Custom);
        }
        return null;
    }

    //获得断开后是否报警的CH
    public static UUID getCHLinkLostAlert() {
        switch (Dev_Type) {
            case Dev_Type_Shuidi:
                return UUID.fromString(CH_LinkLost_Alert_ShuiDi);
            case Dev_Type_Rectangle:
                return UUID.fromString(CH_LinkLost_Alert_Rectangle);
        }
        return null;
    }

    //获得主动报警的Server
    public static UUID getServerImmediateAlert() {
        switch (Dev_Type) {
            case Dev_Type_Shuidi:
                return UUID.fromString(Server_Immediate_Alert_ShuiDi);
            case Dev_Type_Rectangle:
                return UUID.fromString(Server_Rectangle_Custom);
        }
        return null;
    }

    //获得主动报警的CH
    public static UUID getCHImmediateAlert() {
        switch (Dev_Type) {
            case Dev_Type_Shuidi:
                return UUID.fromString(CH_Immediate_Alert_ShuiDi);
            case Dev_Type_Rectangle:
                return UUID.fromString(CH_Immediate_Alert_Rectangle);
        }
        return null;
    }

    //获得按键被单击的Server
    public static UUID getServerOnClick() {
        switch (Dev_Type) {
            case Dev_Type_Shuidi:
                return UUID.fromString(Server_ShuiDi_Private);
            case Dev_Type_Rectangle:
                return UUID.fromString(Server_Rectangle_Custom);
        }
        return null;
    }

    //获得按键被单击的CH
    public static UUID getCHOnClick() {
        switch (Dev_Type) {
            case Dev_Type_Shuidi:
                return UUID.fromString(CH_ShuiDi_Key_Press);
            case Dev_Type_Rectangle:
                return UUID.fromString(CH_Rectangle_Key_Press);
        }
        return null;
    }

    public static void setDeviceType(BluetoothGatt xfBluetoothGatt) {
        BluetoothGattService shuidi = xfBluetoothGatt.getService(UUID.fromString(Server_ShuiDi));
        if (shuidi != null) {
            Dev_Type = Dev_Type_Shuidi;
        }
        BluetoothGattService rectangle = xfBluetoothGatt.getService(UUID.fromString(Server_Rectangle));
        if (rectangle != null) {
            Dev_Type = Dev_Type_Rectangle;
        }
    }
}
