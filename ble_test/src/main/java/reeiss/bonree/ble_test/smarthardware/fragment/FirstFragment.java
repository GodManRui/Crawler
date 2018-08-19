package reeiss.bonree.ble_test.smarthardware.fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import org.litepal.LitePal;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import reeiss.bonree.ble_test.LocationApplication;
import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.bean.BleDevConfig;
import reeiss.bonree.ble_test.bean.DeviceListBean;
import reeiss.bonree.ble_test.bean.Location;
import reeiss.bonree.ble_test.bean.PreventLosingCommon;
import reeiss.bonree.ble_test.blehelp.XFBluetooth;
import reeiss.bonree.ble_test.blehelp.XFBluetoothCallBack;
import reeiss.bonree.ble_test.smarthardware.activity.BlueControlActivity;
import reeiss.bonree.ble_test.smarthardware.adapter.DevListAdapter;
import reeiss.bonree.ble_test.utils.T;

import static reeiss.bonree.ble_test.bean.CommonHelp.getOnClick;
import static reeiss.bonree.ble_test.blehelp.XFBluetooth.CURRENT_DEV_MAC;

public class FirstFragment extends Fragment {


    private XFBluetooth xfBluetooth;
    private ListView vDevLv;
    private ImageView vScan;
    private View vReScan;
    private DevListAdapter adapter;
    private int position;
    private ArrayList<DeviceListBean> mDevList;
    private ProgressDialog progressDialog;
    private MediaPlayer mPlayer;
    private XFBluetoothCallBack gattCallback = new XFBluetoothCallBack() {

        /**
         * device.getBondState()
         * BOND_BONDED     指明远程设备已经匹配。   和远程设备的匹配并不意味着设备间已经成功连接。它只意味着匹配过程已经在稍早之前完成，
         * 并且连接键已经存储在本地，准备在下次连接的时候使用。
         * <p>
         * BOND_BONDING  指明和远程设备的匹配正在进行中
         * BOND_NONE      指明远程设备未被匹配。
         */
        //扫描获取设备的回调
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
        }

        //链接状态发生改变
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, final int status, final int newState) {
            StatusChange(status, newState);
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
            }
        }

        //通知操作的回调（此处接收BLE设备返回数据） 点击返回1
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic) {
            FoundPhone(characteristic);
        }

    };

    private void FoundPhone(BluetoothGattCharacteristic characteristic) {
        String value = Arrays.toString(characteristic.getValue());
        if (value.equals("[2]")) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BleDevConfig currentDevConfig = XFBluetooth.getCurrentDevConfig();
                    T.show(getActivity(), "寻找手机！！");
                    if (mPlayer != null && mPlayer.isPlaying()) {
                        Log.e("jerry", "run: 正在播放");
                        return;
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

    /**
     * 链接状态发生改变
     *
     * @param status
     * @param newState
     */
    private void StatusChange(int status, final int newState) {
        Log.e("JerryZhu", "链接状态: " + status + "   ==  " + newState);
//            final BleDevConfig currentDev = LitePal.findFirst(BleDevConfig.class);

        if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            PreventLosingCommon.Dev_Type = -1;
            if (locationApplication != null) {
                if (locationApplication.locationService.isStart()) {
                    Log.e("jerryzhu", "first 停止定位: ");
                    locationApplication.locationService.stop();
                }
                boolean save = locationApplication.mLocation.save();
                if (save)
                    locationApplication.mLocation = new Location();
            }

            final BleDevConfig currentDevConfig = XFBluetooth.getCurrentDevConfig();
            if (currentDevConfig != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mPlayer != null && mPlayer.isPlaying()) {
                            Log.e("jerry", "run: 正在播放断开");
                            return;
                        }
                        try {
                            mPlayer = new MediaPlayer();
                            Uri setDataSourceuri = Uri.parse("android.resource://reeiss.bonree.ble_test/" + currentDevConfig.getRingResId());
                            mPlayer.setDataSource(getActivity(), setDataSourceuri);
                            mPlayer.prepare();
                            mPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                            mPlayer.setVolume(2f, 2f);
                       /* final MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), currentDevConfig.getRingResId());//重新设置要播放的音频
                        mediaPlayer.start();*/
                        T.show(getActivity(), "开始报警：" + currentDevConfig.getRingResId());
                        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                        b.setTitle("丢失报警");
                        b.setMessage("防丢器已断开连接！");
                        b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mPlayer != null) {
                                    mPlayer.stop();
                                    mPlayer.release();
                                    mPlayer = null;
                                }
                                T.show(getActivity(), "取消报警");
                            }
                        });
                        b.setCancelable(false).create().show();
                    }
                });
            }
        }
        Log.e("jerry", "run: 走了");

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null)
                    progressDialog.dismiss();

                BleDevConfig currentDevConfig = XFBluetooth.getCurrentDevConfig();
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    xfBluetooth.getXFBluetoothGatt().discoverServices();
                    if (locationApplication != null && !locationApplication.locationService.isStart()) {
                        Log.e("jerryzhu", " 定位开启: ");
                        locationApplication.locationService.start();
                    }

                    if (currentDevConfig == null) {
                        Field[] fields = R.raw.class.getDeclaredFields();
                        try {
                            currentDevConfig = new BleDevConfig(CURRENT_DEV_MAC, XFBluetooth.getInstance(getActivity()).getXFBluetoothGatt().getDevice().getName(), "true", "false", fields[1].getName(), 0, fields[1].getInt(R.raw.class));
                            currentDevConfig.save();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
                mDevList.get(position).setConnectState(newState);
                //如果当前设备以前设置过别名，那么应该先显示别名
                if (currentDevConfig != null && !TextUtils.isEmpty(currentDevConfig.getAlias()))
                    mDevList.get(position).setDevNick(currentDevConfig.getAlias());

                adapter.setDevList(mDevList);
                vDevLv.setItemsCanFocus(true);

                Log.e("jerry", "连接成功，服务扫描 : ");
            }
        });
    }

    private LocationApplication locationApplication;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getActivity().setTitle("设备管理");
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
        initView();
        scanBle();
    }

    private void initView() {
        getActivity().setTitle("设备管理");
        vDevLv = getView().findViewById(R.id.ble_dev_lv);
        vScan = getView().findViewById(R.id.iv_scan);
        vReScan = getView().findViewById(R.id.rl_scan);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("正在连接..");
        progressDialog.setCancelable(false);
        locationApplication = ((LocationApplication) getActivity().getApplication());

        mDevList = new ArrayList<DeviceListBean>();
        adapter = new DevListAdapter(mDevList, getActivity());
        vDevLv.setAdapter(adapter);
        vDevLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("jerry", "onItem " + xfBluetooth.getAdapter().isDiscovering());
                //todo 判断仍在扫描，此方法暂时无效

                if (xfBluetooth.isStopCall) {
                    Log.e("jerry", "点击的时候: 正在发现");
                    xfBluetooth.stop();
                }

                DeviceListBean deviceListBean = mDevList.get(position);
                if (XFBluetooth.getCurrentDevConfig() != null && deviceListBean.getConnectState().equals("已连接")) {
                    T.show(getActivity(), "设备已连接！");
                    Intent intent = new Intent(getActivity(), BlueControlActivity.class);
                    startActivityForResult(intent, 100);
                    return;
                }

                vDevLv.setItemsCanFocus(false);
                FirstFragment.this.position = position;/*
                deviceListBean.setConnectState(BluetoothGatt.STATE_CONNECTING);
                adapter.setDevList(mDevList);*/
                deviceListBean.setConnectState(BluetoothGatt.STATE_DISCONNECTED);
                adapter.notifyDataSetChanged();
                progressDialog.show();
                xfBluetooth.connect(deviceListBean.getBluetoothDevice());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BleDevConfig currentDevConfig = XFBluetooth.getCurrentDevConfig();
                    if (currentDevConfig != null && !TextUtils.isEmpty(currentDevConfig.getAlias())) {
                        mDevList.get(position).setDevNick(currentDevConfig.getAlias());
                    }
                    adapter.setDevList(mDevList);
                }
            });
        }
    }

    private void scanBle() {
        xfBluetooth = XFBluetooth.getInstance(getActivity());
        xfBluetooth.addBleCallBack(gattCallback);
        RotateAnimation animation = new RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(2000);
        animation.setRepeatCount(-1);
        animation.setFillAfter(true);
        vScan.startAnimation(animation);
        xfBluetooth.scan();
    }
}
