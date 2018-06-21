package reeiss.bonree.ble_test.smarthardware;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import reeiss.bonree.ble_test.BlueControlActivity;
import reeiss.bonree.ble_test.DevListAdapter;
import reeiss.bonree.ble_test.DeviceListBean;
import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.ShuiDiCommon;
import reeiss.bonree.ble_test.T;
import reeiss.bonree.ble_test.XFBluetooth;
import reeiss.bonree.ble_test.XFBluetoothCallBack;

public class FirstFragment extends Fragment {


    private XFBluetooth xfBluetooth;
    private ListView vDevLv;
    private ImageView vScan;
    private View vReScan;
    private DevListAdapter adapter;
    private int position;
    private ArrayList<DeviceListBean> mDevList;
    private ProgressDialog progressDialog;
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
                xfBluetooth.stop();
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
                        Log.e("jerry", "连接成功，服务扫描 : ");
                    }
                }
            });
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                for (int i = 0; i < xfBluetooth.getXFBluetoothGatt().getServices().size(); i++) {
                    Log.e("jerryzhu", "服务扫描结果 : " + xfBluetooth.getXFBluetoothGatt().getServices().get(i).getUuid());
                }
                Log.e("JerryZhu", "onServicesDiscovered: 服务扫描成功，开启按键通知！");
                BluetoothGattService click = xfBluetooth.getXFBluetoothGatt().getService(UUID.fromString(ShuiDiCommon.Server_Private));
                if (click == null) return;
                BluetoothGattCharacteristic chKey = click.getCharacteristic(UUID.fromString(ShuiDiCommon.CH_Key_Press));
                xfBluetooth.getXFBluetoothGatt().setCharacteristicNotification(chKey, true);
            }
        }

        //通知操作的回调（此处接收BLE设备返回数据） 点击返回1
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.e("jerryzhu", "点击了  " + Arrays.toString(characteristic.getValue()));
        }

    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("JerryZhu", "onCreate: ");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, null);
        Log.e("JerryZhu", "onCreateView: ");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("JerryZhu", "onViewCreated: ");
        initView();
        scanBle();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("JerryZhu", "onActivityCreated: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("JerryZhu", "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("JerryZhu", "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("JerryZhu", "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("JerryZhu", "onPause: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("JerryZhu", "onDestroy: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("JerryZhu", "onDestroy: ");
    }

    private void initView() {
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
