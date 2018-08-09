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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.bean.BleDevConfig;
import reeiss.bonree.ble_test.bean.DeviceListBean;
import reeiss.bonree.ble_test.bean.PreventLosingCommon;
import reeiss.bonree.ble_test.blehelp.XFBluetooth;
import reeiss.bonree.ble_test.blehelp.XFBluetoothCallBack;
import reeiss.bonree.ble_test.smarthardware.activity.BlueControlActivity;
import reeiss.bonree.ble_test.smarthardware.adapter.DevListAdapter;
import reeiss.bonree.ble_test.utils.T;

import static reeiss.bonree.ble_test.bean.CommonHelp.getOnClick;

public class FirstFragment extends Fragment {


    private XFBluetooth xfBluetooth;
    private ListView vDevLv;
    private ImageView vScan;
    private View vReScan;
    private DevListAdapter adapter;
    private int position;
    private ArrayList<DeviceListBean> mDevList;
    private ProgressDialog progressDialog;
    private String address;
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
                        vReScan.setVisibility(View.GONE);
                        vDevLv.setVisibility(View.VISIBLE);
                        mDevList.add(new DeviceListBean(device, BluetoothGatt.STATE_DISCONNECTED));
                        adapter.setDevList(mDevList);
                    }
                });
            }
        }

        //链接状态发生改变
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, final int status, final int newState) {
            Log.e("JerryZhu", "链接状态: " + status + "   ==  " + newState);
            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                PreventLosingCommon.Dev_Type = -1;

                final BleDevConfig currentDev = LitePal.where("mac=?", address).findFirst(BleDevConfig.class);
                if (currentDev != null && currentDev.isAlert()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), currentDev.getRingResId());//重新设置要播放的音频
                            mediaPlayer.start();
                            T.show(getActivity(), "开始报警：" + currentDev.getRingResId());
                            AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                            b.setTitle("丢失报警");
                            b.setMessage("防丢器已断开连接！");
                            b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mediaPlayer.reset();
                                    mediaPlayer.release();
                                    T.show(getActivity(), "取消报警");
                                }
                            });
                            b.setCancelable(false).create().show();
                        }
                    });
                }
                address = "";
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog != null)
                        progressDialog.dismiss();

                    mDevList.get(position).setConnectState(newState);
                    adapter.setDevList(mDevList);
                    vDevLv.setItemsCanFocus(true);

                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        xfBluetooth.getXFBluetoothGatt().discoverServices();
                        BluetoothDevice device = xfBluetooth.getXFBluetoothGatt().getDevice();
                        if (device == null) return;
                        address = device.getAddress();
                        BleDevConfig bleDevConfig = LitePal.where("mac=?", address).findFirst(BleDevConfig.class);
                        if (bleDevConfig == null) {
                            Field[] fields = R.raw.class.getDeclaredFields();
                            BleDevConfig bleDevConfi = null;
                            try {
                                bleDevConfi = new BleDevConfig(address, device.getName(), true, 0, fields[0].getInt(R.raw.class));
                                bleDevConfi.save();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.e("jerry", "连接成功，服务扫描 : ");
                    }
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
            }
        }

        //通知操作的回调（此处接收BLE设备返回数据） 点击返回1
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic
            characteristic) {
            String value = Arrays.toString(characteristic.getValue());
            if (value.equals("[2]")) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        T.show(getActivity(), "警报！！");
                    }
                });
            }
            Log.e("jerryzhu", "点击了  " + value);
        }

    };

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getActivity().setTitle("设备管理");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("JerryZhu", "onCreate: ");
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

        mDevList = new ArrayList<DeviceListBean>();
        adapter = new DevListAdapter(mDevList, getActivity());
        vDevLv.setAdapter(adapter);
        vDevLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("jerry", "onItem " + xfBluetooth.getAdapter().isDiscovering());
                //todo 判断仍在扫描，此方法暂时无效
                if (xfBluetooth.getAdapter().isDiscovering()) {
                    Log.e("jerry", "onItemClick: 正在发现");
                    xfBluetooth.stop();
                }
                DeviceListBean deviceListBean = mDevList.get(position);
                if (deviceListBean.getConnectState().equals("已连接")) {
                    T.show(getActivity(), "设备已连接！");
                    Intent intent = new Intent(getActivity(), BlueControlActivity.class);
                    startActivity(intent);
                    return;
                }
                vDevLv.setItemsCanFocus(false);
                FirstFragment.this.position = position;/*
                deviceListBean.setConnectState(BluetoothGatt.STATE_CONNECTING);
                adapter.setDevList(mDevList);*/
                progressDialog.show();
                xfBluetooth.connect(deviceListBean.getBluetoothDevice());
            }
        });
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
