package reeiss.bonree.ble_test;

public class ShuiDiCommon {
    //电池电量显示
    static String Server_Battery_Level = "0000180f-0000-1000-8000-00805f9b34fb";
    static String CH_Battery_Level = "00002a19-0000-1000-8000-00805f9b34fb";


    //立即使能报警
    static String Server_Immediate_Alert = "00001802-0000-1000-8000-00805f9b34fb";
    static String CH_Immediate_Alert = "00002a06-0000-1000-8000-00805f9b34fb";
    static byte Common_High_immediate_Alert = 0x01; //High immediateAlertLevel
    static byte Common_Middling_immediate_Alert = 0x03; //Middling immediateAlertLevel
    static byte Common_No_immediate_Alert = 0x00; //No immediateAlertLevel


    // PrivateServices  私有服务
    static String Server_Private = "0000ffe0-0000-1000-8000-00805f9b34fb";
    //按键消息监听   单击收到0x01
    static String CH_Key_Press = "0000ffe1-0000-1000-8000-00805f9b34fb";

    //断开连接是否报警  0x01 报警  0x00不报警
    static String Server_LinkLost_Alert = "00001803-0000-1000-8000-00805f9b34fb";
    static String CH_LinkLost_Alert = "00002a06-0000-1000-8000-00805f9b34fb";
    static byte Common_LinkLost_100Alert = 0x01;
    static byte Common_LinkLost_200Alert = 0x02;
    static int Common_LinkLost_No_Alert = 0xFF;


}
