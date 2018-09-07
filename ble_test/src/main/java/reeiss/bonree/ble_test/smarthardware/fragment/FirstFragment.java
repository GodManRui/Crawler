package reeiss.bonree.ble_test.smarthardware.fragment;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
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
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import org.litepal.LitePal;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import reeiss.bonree.ble_test.LocationApplication;
import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.bean.BleDevConfig;
import reeiss.bonree.ble_test.bean.Location;
import reeiss.bonree.ble_test.bean.PreventLosingCommon;
import reeiss.bonree.ble_test.bean.WuRaoWifiConfig;
import reeiss.bonree.ble_test.blehelp.XFBluetooth;
import reeiss.bonree.ble_test.blehelp.XFBluetoothCallBack;
import reeiss.bonree.ble_test.smarthardware.activity.BindDevActivity;
import reeiss.bonree.ble_test.smarthardware.activity.BlueControlActivity;
import reeiss.bonree.ble_test.smarthardware.adapter.DevListAdapter;
import reeiss.bonree.ble_test.utils.T;
import reeiss.bonree.ble_test.utils.Utils;

import static android.content.Context.WIFI_SERVICE;
import static reeiss.bonree.ble_test.bean.CommonHelp.getLinkLostAlert;
import static reeiss.bonree.ble_test.bean.CommonHelp.getOnClick;
import static reeiss.bonree.ble_test.blehelp.XFBluetooth.CURRENT_DEV_MAC;
import static reeiss.bonree.ble_test.blehelp.XFBluetooth.getCurrentDevConfig;

public class FirstFragment extends Fragment {

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
    private Handler handler;
    final Runnable rssiRunnable = new Runnable() {
        @Override
        public void run() {
            if (xfBluetooth.getXFBluetoothGatt() != null) {
                xfBluetooth.getXFBluetoothGatt().readRemoteRssi();
                handler.postDelayed(this, 5000);
            } else {
                handler.removeCallbacks(this);
            }
        }
    };
    private boolean dontAlert;
    private LocationApplication locationApplication;
    private Builder dialogAlert;
    private XFBluetoothCallBack gattCallback = new XFBluetoothCallBack() {

        //链接状态发生改变
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {

            BleDevConfig currentDevConfig = getCurrentDevConfig();
            if (currentDevConfig == null) {
                currentDevConfig = getCurrentDevConfig(gatt.getDevice().getAddress());
            }
            final BleDevConfig finalCurrentDevConfig = currentDevConfig;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    StatusChange(finalCurrentDevConfig, status, newState);
                }
            });
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

                PreventLosingCommon.getDeviceType(xfBluetooth.getXFBluetoothGatt());

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

                BluetoothGattCharacteristic linkLostAlert = getLinkLostAlert(xfBluetooth.getXFBluetoothGatt());
                if (linkLostAlert != null) {
                    linkLostAlert.setValue(new byte[]{1});
                    boolean b = xfBluetooth.getXFBluetoothGatt().writeCharacteristic(linkLostAlert);
                    T.show(getActivity(), "开启报警" + b);
                }
            }
        }

        /**
         * device.getBondState()
         * BOND_BONDED     指明远程设备已经匹配。   和远程设备的匹配并不意味着设备间已经成功连接。它只意味着匹配过程已经在稍早之前完成，
         * 并且连接键已经存储在本地，准备在下次连接的时候使用。
         * <p>
         * BOND_BONDING  指明和远程设备的匹配正在进行中
         * BOND_NONE      指明远程设备未被匹配。
         */
      /*  //扫描获取设备的回调
        @Override
        public void onScanResult(final BluetoothDevice device) {
            Log.e("jerryzhu", "扫描结果: " + device.getName());
            if (device.getName() != null && device.getName().contains("iTAG")) {
                // xfBluetooth.stop();
                for (int i = 0; i < mDevList.size(); i++) {
                    if (mDevList.get(i).getBluetoothDevice().getAddress().equals(device.getAddress())) {
                        Log.e("JerryZhu", "onScanResult: 列表已存在!!!!!!");
                        return;
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (vDevLv.getVisibility() != View.VISIBLE) {
                            vReScan.setVisibility(View.GONE);
                            vDevLv.setVisibility(View.VISIBLE);
                        }
                        BleDevConfig bleDevConfig = LitePal.where("mac=?", device.getAddress()).findFirst(BleDevConfig.class);
                        if (bleDevConfig != null && !TextUtils.isEmpty(bleDevConfig.getAlias()))
                            mDevList.add(new DeviceListBean(device, BluetoothGatt.STATE_DISCONNECTED, bleDevConfig.getAlias()));
                        else
                            mDevList.add(new DeviceListBean(device, BluetoothGatt.STATE_DISCONNECTED));

                        adapter.setDevList(mDevList);
                    }
                });
            }
        }*/
        @Override
        public void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        Log.e("jerry", "run: 写入成功");
                    } else {
                        Log.e("jerry", "run: 写入失败");
                    }
                }
            });
        }

        //链接状态发生改变
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {

            BleDevConfig currentDevConfig = getCurrentDevConfig();
            if (currentDevConfig == null) {
                currentDevConfig = getCurrentDevConfig(gatt.getDevice().getAddress());
            }
            final BleDevConfig finalCurrentDevConfig = currentDevConfig;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    StatusChange(finalCurrentDevConfig, status, newState);
                }
            });
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

                PreventLosingCommon.getDeviceType(xfBluetooth.getXFBluetoothGatt());

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

                BluetoothGattCharacteristic linkLostAlert = getLinkLostAlert(xfBluetooth.getXFBluetoothGatt());
                if (linkLostAlert != null) {
                    linkLostAlert.setValue(new byte[]{1});
                    boolean b = xfBluetooth.getXFBluetoothGatt().writeCharacteristic(linkLostAlert);
                    T.show(getActivity(), "开启报警" + b);
                }
            }
        }

        //通知操作的回调（此处接收BLE设备返回数据） 点击返回1
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic) {
            FoundPhone(characteristic);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, final int rssi, int status) {

            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BleDevConfig currentDevConfig = XFBluetooth.getCurrentDevConfig();
                    if (currentDevConfig == null) return;
                    int alertMargin = currentDevConfig.getAlertMargin();
                    T.show(getActivity(), rssi + "");
                    if (Utils.isRemoteAlert(alertMargin, rssi, lastRssi)) {
                        PhoneAlert(currentDevConfig, 1);
                    } else {
                        if (mPlayer != null && mPlayer.isPlaying()) {
                            mPlayer.stop();
                            mPlayer.release();
                            mPlayer.reset();
                            mPlayer = null;
                        }
                    }
                    lastRssi = rssi;
                }
            });
            //  Log.e("JerryZhu", "onReadRemoteRssi: " + rssi);
        }
    };
  /*  private Runnable scanTimeOut = new Runnable() {
        @Override
        public void run() {
            T.show(getActivity(), "扫描超时已停止");
//            stopScan();
        }
    };*/

    //双击寻找手机
    private void FoundPhone(BluetoothGattCharacteristic characteristic) {
        String value = Arrays.toString(characteristic.getValue());
        if (value.equals("[2]")) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //在勿扰 true
                    if (checkWuRao()) return;
                    BleDevConfig currentDevConfig = XFBluetooth.getCurrentDevConfig();
                    T.show(getActivity(), "寻找手机！！");
                    if (mPlayer != null && mPlayer.isPlaying()) {
                        Log.e("jerry", "run: 正在播放");
                        return;
                    }
                    try {
                        mPlayer = new MediaPlayer();
                        assert currentDevConfig != null;
                        Uri setDataSourceuri = Uri.parse("android.resource://reeiss.bonree.ble_test/" + currentDevConfig.getRingResId());
                        mPlayer.setDataSource(Objects.requireNonNull(getActivity()), setDataSourceuri);
                        mPlayer.prepare();
                        mPlayer.setLooping(true);
                        mPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                 /*   final MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), currentDev.getRingResId());//重新设置要播放的音频
                    mediaPlayer.start();*/
                    AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
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
                            T.show(getActivity(), "取消");
                        }
                    });
                    b.setCancelable(false).create().show();
                }
            });
        }
        Log.e("jerryzhu", "点击了  " + value);
    }

    //勿扰是否打开，是否在勿扰区域  在勿扰true 不在false
    private boolean checkWuRao() {
        SharedPreferences myPreference = ((Objects.requireNonNull(getActivity())).getSharedPreferences("myPreference", Context.MODE_PRIVATE));
        boolean isOpenWuRao = myPreference.getBoolean("isOpenWuRao", false);
        if (isOpenWuRao) {
            WifiManager wm = (WifiManager) getActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
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
        if (status == BluetoothProfile.STATE_CONNECTED && newState == BluetoothProfile.STATE_DISCONNECTED) {
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
                        T.show(getActivity(), "丢失位置已保存！");
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

        if (progressDialog != null)
            progressDialog.dismiss();

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
            mPlayer.setDataSource(getActivity(), setDataSourceuri);
            mPlayer.prepare();
            mPlayer.setLooping(true);
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
//                            mPlayer.setVolume(2f, 2f);

        dialogAlert = new Builder(getActivity());
        dialogAlert.setTitle("丢失报警");
        dialogAlert.setMessage(type == 0 ? "防丢器已断开连接！" : "防丢器位置超出范围！");
        dialogAlert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mPlayer != null) {
                    mPlayer.stop();
                    mPlayer.release();
                    mPlayer = null;
                }
            }
        });
        dialogAlert.setCancelable(false).create().show();
        return false;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getActivity().setTitle("设备管理");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    BleDevConfig currentDevConfig = XFBluetooth.getCurrentDevConfig();
//                    if (currentDevConfig != null && !TextUtils.isEmpty(currentDevConfig.getAlias())) {
//                        mDevList.get(position).setDevNick(currentDevConfig.getAlias());
//                    }
                    String newName = data.getStringExtra("newName");
                    mDevList.get(position).setAlias(newName);
                    adapter.setDevList(mDevList);
                }
            });
        } else if (resultCode == 200) {
            ArrayList<BleDevConfig> addBindDev = (ArrayList<BleDevConfig>) data.getSerializableExtra("addBindDev");
            mDevList.addAll(addBindDev);
            adapter.notifyDataSetChanged();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handler = new Handler();
        initView();
        xfBluetooth = XFBluetooth.getInstance(getActivity());
        xfBluetooth.addBleCallBack(gattCallback);
    }

    private void initView() {
        getActivity().setTitle("设备管理");
        vDevLv = getView().findViewById(R.id.ble_dev_lv);
        getView().findViewById(R.id.bt_add_dev).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addDev();
            }
        });
     /*   vScan = getView().findViewById(R.id.iv_scan);
        vReScan = getView().findViewById(R.id.rl_scan);
        btScan = getView().findViewById(R.id.bt_scan);
        btScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();
            }
        });*/
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("正在连接设备");
        progressDialog.setMessage("请确保设备开机并在您周围...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                xfBluetooth.disconnect();
                xfBluetooth.reset();
            }
        });
        locationApplication = ((LocationApplication) getActivity().getApplication());

        mDevList = LitePal.findAll(BleDevConfig.class);
        adapter = new DevListAdapter(this.mDevList, getActivity());
        vDevLv.setAdapter(adapter);
        vDevLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                stopScan();
                //点击的是未连接的设备，但此时有其他设备已连接
                BleDevConfig deviceListBean = FirstFragment.this.mDevList.get(position);
                if (!TextUtils.isEmpty(CURRENT_DEV_MAC) && !CURRENT_DEV_MAC.equals(deviceListBean.getMac())) {
                    T.show(getActivity(), "请先断开连接");
                    return;
                }
                //点击的是已连接的设备
                if (XFBluetooth.getCurrentDevConfig() != null && deviceListBean.getConnectState().equals("已连接")) {
                    Intent intent = new Intent(getActivity(), BlueControlActivity.class);
                    startActivityForResult(intent, 100);
                    return;
                }
                //点的是可以连接的设备，开始连接
                vDevLv.setItemsCanFocus(false);
                FirstFragment.this.position = position;/*
                deviceListBean.setConnectState(BluetoothGatt.STATE_CONNECTING);
                adapter.setDevList(mDevList);*/
                deviceListBean.setConnectState(BluetoothGatt.STATE_DISCONNECTED);
                adapter.notifyDataSetChanged();
                progressDialog.show();
                xfBluetooth.connect(deviceListBean.getMac());
            }
        });

        vDevLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                final BleDevConfig deviceListBean = FirstFragment.this.mDevList.get(position);
                if (deviceListBean == null) return true;
                final String address = deviceListBean.getMac();

                if (deviceListBean.getConnectState().equals("已连接") && address.equals(CURRENT_DEV_MAC)) {
                    AlertDialog.Builder seleDia = new AlertDialog.Builder(getActivity())
                            .setItems(new String[]{"断开连接", "删除设备"}, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dontAlert = true;
                                    switch (which) {
                                        case 0:
                                            xfBluetooth.disconnect();
                                            break;
                                        case 1:
                                            DelDev(deviceListBean, address);
                                            break;
                                    }
                                }
                            });
                    seleDia.create().show();
                } else
                    DelDev(deviceListBean, address);
                return true;
            }
        });
    }

    //跳转到添加设备界面
    public void addDev() {
        Intent intent = new Intent(getActivity(), BindDevActivity.class);
        startActivityForResult(intent, 10);
    }


    private void DelDev(final BleDevConfig bleDevConfig, String address) {
//        final BleDevConfig bleDevConfig = LitePal.where("mac=?", address).findFirst(BleDevConfig.class);

        AlertDialog.Builder delDia = new AlertDialog.Builder(getActivity())
                .setTitle("删除设备")
                .setMessage("确认删除" + bleDevConfig.getAlias() + "并清空所有配置信息(包括昵称，定位记录等)？")
                .setNegativeButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDevList.remove(bleDevConfig);
                        adapter.setDevList(mDevList);
                        if (bleDevConfig.getConnectState().equals("已连接")) {
                            xfBluetooth.disconnect();
                        }
                        bleDevConfig.delete();
                    }
                })
                .setPositiveButton("取消", null)
                .setCancelable(false);
        delDia.create().show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        handler.removeCallbacks(scanTimeOut);
        handler.removeCallbacks(rssiRunnable);
        Log.e("jerry", "removeCallbacks 信号解除 ");
    }
/*

 private void stopScan() {
        btScan.setText("开始扫描");

        vScan.clearAnimation();

//        vReScan.setVisibility(View.GONE);
        xfBluetooth.stop();
        handler.removeCallbacks(scanTimeOut);
    }


    public void scan() {

        if (xfBluetooth.isScaning) {       //停止扫描
            stopScan();
        } else {                            //开始扫描
            startScan();
        }
    }

    private void startScan() {
        for (int i = 0; i < mDevList.size(); i++) {
            DeviceListBean deviceListBean = mDevList.get(i);
            if (deviceListBean == null || !"已连接".equals(deviceListBean.getConnectState())) {
                mDevList.remove(i);
            }
        }
        adapter.setDevList(mDevList);
        btScan.setText("停止扫描");
        if (vReScan.getVisibility() == View.VISIBLE) {
            RotateAnimation animation = new RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setInterpolator(new LinearInterpolator());
            animation.setDuration(2000);
            animation.setRepeatCount(-1);
            animation.setFillAfter(true);
            vScan.startAnimation(animation);
        }
        xfBluetooth.scan();

        handler.postDelayed(scanTimeOut, 20000);
    }
*/

}
