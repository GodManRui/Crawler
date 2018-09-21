package reeiss.bonree.ble_test.smarthardware.service;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import org.litepal.LitePal;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import reeiss.bonree.ble_test.LocationApplication;
import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.bean.BleDevConfig;
import reeiss.bonree.ble_test.bean.Location;
import reeiss.bonree.ble_test.bean.PreventLosingCommon;
import reeiss.bonree.ble_test.bean.WuRaoWifiConfig;
import reeiss.bonree.ble_test.blehelp.XFBluetooth;
import reeiss.bonree.ble_test.blehelp.XFBluetoothCallBack;
import reeiss.bonree.ble_test.smarthardware.adapter.DevListAdapter;
import reeiss.bonree.ble_test.utils.T;
import reeiss.bonree.ble_test.utils.Utils;

import static reeiss.bonree.ble_test.bean.CommonHelp.getLinkLostAlert;
import static reeiss.bonree.ble_test.bean.CommonHelp.getOnClick;
import static reeiss.bonree.ble_test.bean.PreventLosingCommon.Dev_Type_Shuidi;
import static reeiss.bonree.ble_test.blehelp.XFBluetooth.CURRENT_DEV_MAC;
import static reeiss.bonree.ble_test.blehelp.XFBluetooth.getCurrentDevConfig;

public class BlueService extends Service {

    private XFBluetooth xfBluetooth;
    private ListView vDevLv;
    private ImageView vScan;
    private View vReScan;
    private DevListAdapter adapter;
    private int position;
    private List<BleDevConfig> mDevList;
    private ProgressDialog progressDialog;
    private MediaPlayer mPlayer;
    private double lastRssi;
    private Button btScan;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    FoundPhone();
                    break;
            }
        }
    }

    private ServiceHandler handler;
    final Runnable rssiRunnable = new Runnable() {
        @Override
        public void run() {
            if (xfBluetooth.getXFBluetoothGatt() != null) {
                xfBluetooth.getXFBluetoothGatt().readRemoteRssi();
                handler.postDelayed(this, 3800);
            } else {
                handler.removeCallbacks(this);
            }
        }
    };
    private boolean dontAlert;
    private LocationApplication locationApplication;
    private AlertDialog alertDialog;
    private boolean isDialogMargin;
    private long lastTimeMillis;
    private XFBluetoothCallBack gattCallback = new XFBluetoothCallBack() {

        //链接状态发生改变
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {

            BleDevConfig currentDevConfig = getCurrentDevConfig();
            if (currentDevConfig == null) {
                currentDevConfig = getCurrentDevConfig(gatt.getDevice().getAddress());
            }
            final BleDevConfig finalCurrentDevConfig = currentDevConfig;

            StatusChange(finalCurrentDevConfig, status, newState);

        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

                PreventLosingCommon.setDeviceType(xfBluetooth.getXFBluetoothGatt());

                Log.e("JerryZhu", "onServicesDiscovered: 服务扫描成功，开启按键通知！");
                BluetoothGattCharacteristic chOnclick = getOnClick(xfBluetooth.getXFBluetoothGatt());
                boolean isEnable = xfBluetooth.getXFBluetoothGatt().setCharacteristicNotification(chOnclick, true);
                if (isEnable) {
                    List<BluetoothGattDescriptor> descriptorList = chOnclick.getDescriptors();
                    if (descriptorList != null && descriptorList.size() > 0) {
                        for (BluetoothGattDescriptor descriptor : descriptorList) {
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                        }
                    }
                }
            }
        }

        @Override
        public void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e("JerryZhu", "onServicesDiscovered: 写入按键通知成功" + Arrays.toString(characteristic.getValue()));
            } else {
                Log.e("JerryZhu", "onServicesDiscovered: 写入按键通知失败" + Arrays.toString(characteristic.getValue()));
            }
            if (PreventLosingCommon.Dev_Type != Dev_Type_Shuidi) {
                BluetoothGattCharacteristic linkLostAlert = getLinkLostAlert(xfBluetooth.getXFBluetoothGatt());
                Log.e("JerryZhu", "onServicesDiscovered:  监测到不是水滴！" + linkLostAlert);
                if (linkLostAlert == null) return;
                if (!characteristic.getUuid().equals(linkLostAlert.getUuid())) {
                    Log.e("JerryZhu", "onServicesDiscovered: 正在开启报警");
                    //说明不是写入开启报警返回的，需要写入开启报警
                    linkLostAlert.setValue(new byte[]{1});
                    boolean b = xfBluetooth.getXFBluetoothGatt().writeCharacteristic(linkLostAlert);
                    Log.e("JerryZhu", "写入开启报警: " + b);
                } /*else {
                            //说明是写入报警返回的，排除水滴
                        }*/
            }
        }

        //通知操作的回调（此处接收BLE设备返回数据） 点击返回1
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic) {
            String value = Arrays.toString(characteristic.getValue());
            if (PreventLosingCommon.Dev_Type == Dev_Type_Shuidi) {
                long currentTimeMillis = System.currentTimeMillis();
                if ((currentTimeMillis - lastTimeMillis) < 500) {
                    if (value.equals("[1]")) {
                        value = "[2]";
                        lastTimeMillis = 0;
                    }
                } else lastTimeMillis = currentTimeMillis;

            }
            if (value.equals("[2]")) {
                Message message = handler.obtainMessage(100);
//                message.what = 100;
                handler.sendMessage(message);
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, final int rssi, int status) {

            BleDevConfig currentDevConfig = XFBluetooth.getCurrentDevConfig();
            if (currentDevConfig == null) return;
            int alertMargin = currentDevConfig.getAlertMargin();
            T.show(BlueService.this, "service " + rssi);
            if (Utils.isRemoteAlert(alertMargin, rssi, lastRssi)) {
                PhoneAlert(currentDevConfig, 1);
            } else {
                lastRssi = rssi;
                if (!isDialogMargin) return;
                if (mPlayer != null && mPlayer.isPlaying()) {
                    mPlayer.stop();
                    mPlayer.release();
                    mPlayer = null;
                }
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.cancel();
                    alertDialog = null;
                }
            }

            //  Log.e("JerryZhu", "onReadRemoteRssi: " + rssi);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("jerry", "onCreate: ");

//        handler = new Handler();
        // 默认情况下Service是运行在主线程中，而服务一般又十分耗费时间，如果
        // 放在主线程中，将会影响程序与用户的交互，因此把Service
        // 放在一个单独的线程中执行
        HandlerThread thread = new HandlerThread("MessageDemoThread", Thread.MAX_PRIORITY);
        thread.start();
        // 获取当前线程中的looper对象
        Looper looper = thread.getLooper();
        //创建Handler对象，把looper传递过来使得handler、
        //looper和messageQueue三者建立联系
        handler = new ServiceHandler(looper);

    }

    int GRAY_SERVICE_ID = 1001;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //设置service为前台服务，提高优先级
       /* if (Build.VERSION.SDK_INT < 18) {
            //Android4.3以下 ，此方法能有效隐藏Notification上的图标
            startForeground(GRAY_SERVICE_ID, new Notification());
        } else if (Build.VERSION.SDK_INT > 18 && Build.VERSION.SDK_INT < 25) {
            //Android4.3 - Android7.0，此方法能有效隐藏Notification上的图标
            Intent innerIntent = new Intent(this, GrayInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, new Notification());
        } else {*/
        //Android7.1 google修复了此漏洞，暂无解决方法（现状：Android7.1以上app启动后通知栏会出现一条"正在运行"的通知消息）
        startForeground(GRAY_SERVICE_ID, new Notification());
//        }
        Log.e("jerry", "onStartCommand: ");
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("jerry", "onBind: ");
        return new MyBinder();
    }

    private class MyBinder extends Binder implements IService {

        @Override
        public void init(String path) {
            xfBluetooth = XFBluetooth.getInstance(getApplicationContext());
            xfBluetooth.addBleCallBack(gattCallback);
        }

        @Override
        public void connect(String mac) {
            Log.e("jerry", "connect: " + Thread.currentThread().getName());
            xfBluetooth.connect(mac);
        }

        @Override
        public void setDontAlert(boolean isDontAlert) {
            dontAlert = isDontAlert;
        }
    }

    //双击寻找手机
    private void FoundPhone() {
        //在勿扰 true
        if (checkWuRao()) return;
        if (mPlayer != null && mPlayer.isPlaying()) {
            return;
        }
        BleDevConfig currentDevConfig = XFBluetooth.getCurrentDevConfig();
        try {
            mPlayer = new MediaPlayer();
            assert currentDevConfig != null;
            Uri setDataSourceuri = Uri.parse("android.resource://reeiss.bonree.ble_test/" + currentDevConfig.getRingResId());
            mPlayer.setDataSource(this, setDataSourceuri);
            mPlayer.prepare();
            mPlayer.setLooping(true);
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
                 /*   final MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), currentDev.getRingResId());//重新设置要播放的音频
                    mediaPlayer.start();*/
        AlertDialog.Builder b = new AlertDialog.Builder(this, R.style.AlertDialog);
        b.setTitle("寻找手机");
        b.setMessage(currentDevConfig.getAlias().isEmpty() ? xfBluetooth.getXFBluetoothGatt().getDevice().getName() : currentDevConfig.getAlias());
        b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mPlayer != null) {
                    mPlayer.stop();
                    mPlayer.release();
                    mPlayer = null;
                }
                T.show(BlueService.this, "取消");
            }
        });
        AlertDialog alertDialog = b.setCancelable(false).create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        Log.e("jerryzhu", "service 弹窗  ");
        alertDialog.show();
    }


    //勿扰是否打开，是否在勿扰区域  在勿扰true 不在false
    private boolean checkWuRao() {
        SharedPreferences myPreference = (getSharedPreferences("myPreference", Context.MODE_PRIVATE));
        boolean isOpenWuRao = myPreference.getBoolean("isOpenWuRao", false);
        if (isOpenWuRao) {
            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            if (wm == null) return false;
            // 获取当前所连接wifi的信息
            final WifiInfo wi = wm.getConnectionInfo();
            if (wi == null) return false;
            final String macAddress = wi.getMacAddress();
            WuRaoWifiConfig has = LitePal.where("wifiMac=?", macAddress).findFirst(WuRaoWifiConfig.class);
            return has != null;
        }
        return false;
    }

    /**
     * 链接状态发生改变
     *
     * @param
     * @param status
     * @param newState
     */
    private void StatusChange(BleDevConfig currentDevConfig, int status, final int newState) {
        if (progressDialog != null)
            progressDialog.dismiss();
        if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            handler.removeCallbacks(rssiRunnable);

//            final BleDevConfig currentDevConfig = XFBluetooth.getCurrentDevConfig();
            PreventLosingCommon.Dev_Type = -1;
            if (locationApplication != null) {
                if (locationApplication.locationService.isStart()) {
                    Log.e("jerryzhu", "first 停止定位: ");
                    locationApplication.locationService.stop();
                }

                if (!dontAlert) {

                    if (!TextUtils.isEmpty(locationApplication.mLocation.getMac())) {
                        boolean save = locationApplication.mLocation.save();
                        T.show(this, "丢失位置已保存！");
                        if (save)
                            locationApplication.mLocation = new Location();
                    }

                    //如果开启勿扰，并且当前wifi在设置区域内    开始报警
                    if (!checkWuRao()) {
                        if (currentDevConfig != null) {
                            if (PhoneAlert(currentDevConfig, 0)) return;
                        }
                    }
                }
            } else {
                dontAlert = false;
            }
        }

//        BleDevConfig currentDevConfig = XFBluetooth.getCurrentDevConfig();
        //已连接
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            BluetoothGatt xfBluetoothGatt = xfBluetooth.getXFBluetoothGatt();
            xfBluetoothGatt.discoverServices();
            handler.postDelayed(rssiRunnable, 1000);
            if (locationApplication != null && !locationApplication.locationService.isStart()) {
                Log.e("jerryzhu", " 定位开启: ");
                locationApplication.locationService.start();
            }

            if (currentDevConfig == null) {
                Field[] fields = R.raw.class.getDeclaredFields();
                try {
                    currentDevConfig = new BleDevConfig(CURRENT_DEV_MAC, xfBluetoothGatt.getDevice().getName(), fields[1].getName(), 0, fields[1].getInt(R.raw.class), 3);
                    currentDevConfig.save();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.e("jerry", "原来的状态: " + mDevList.get(position).getAlias() + "  " + mDevList.get(position).getConnectState() + "    " + status + "   " + newState);
        mDevList.get(position).setConnectState(newState);
        //如果当前设备以前设置过别名，那么应该先显示别名
    /*    if (currentDevConfig != null && !TextUtils.isEmpty(currentDevConfig.getAlias()))
            mDevList.get(position).setA(currentDevConfig.getAlias());*/

//        Log.e("jerry", "更新的状态: " + mDevList.get(position).getDevNick() + "  " + mDevList.get(position).getConnectState());
        adapter.setDevList(mDevList);
        vDevLv.setItemsCanFocus(true);
    }

    //报警，断开连接或者超出范围
    private boolean PhoneAlert(BleDevConfig currentDevConfig, int type) {
        if (checkWuRao()) return true;

        if (mPlayer != null && mPlayer.isPlaying()) {
            return true;
        }
        try {
            mPlayer = new MediaPlayer();
            Uri setDataSourceuri = Uri.parse("android.resource://reeiss.bonree.ble_test/" + currentDevConfig.getRingResId());
            mPlayer.setDataSource(this, setDataSourceuri);
            mPlayer.prepare();
            mPlayer.setLooping(true);
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
//                            mPlayer.setVolume(2f, 2f);

        AlertDialog.Builder dialogAlert = new AlertDialog.Builder(this, R.style.AlertDialog);
        dialogAlert.setTitle("丢失报警")
                .setCancelable(false)
                .setMessage(type == 0 ? "防丢器已断开连接！" : "防丢器位置超出范围！")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mPlayer != null) {
                            mPlayer.stop();
                            mPlayer.release();
                            mPlayer = null;
                        }
                        alertDialog = null;
                    }
                });
        alertDialog = dialogAlert.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
        if (type != 0) {     //那么此次创建的是超出范围的dialog，需要监听范围靠近，取消dialog
            isDialogMargin = true;
        }
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.e("jerrydialog", "onDismiss: ");
                isDialogMargin = false;
            }
        });
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.e("jerrydialog", "onCancel: ");
                isDialogMargin = false;
            }
        });
        return false;
    }

}
