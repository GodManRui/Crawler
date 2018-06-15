package reeiss.bonree.ble_test;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


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
                Log.e("JerryZhu", "停止扫描！！");
                xfBluetooth.stop();
                for (int i = 0; i < mDevList.size(); i++) {
                    if (mDevList.get(i).getBluetoothDevice().getAddress().equals(device.getAddress())) {
                        Log.e("JerryZhu", "onScanResult: TRUE !!!!!!");
                        return;
                    }
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        vReScan.setVisibility(View.GONE);
                        vDevLv.setVisibility(View.VISIBLE);
                        mDevList.add(new DeviceListBean(device, BluetoothGatt.STATE_DISCONNECTED));
                        adapter.setDevList(mDevList);
                    }
                }, 2000);
            }
        }

        //链接状态发生改变
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, final int status, final int newState) {
            Log.e("JerryZhu", "链接状态: " + status + "   ==  " + newState);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog != null)
                        progressDialog.dismiss();

                    mDevList.get(position).setConnectState(newState);
                    adapter.setDevList(mDevList);
                    vDevLv.setItemsCanFocus(true);

                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.e("jerryzhu", "启动服务发现:" + xfBluetooth.getXFBluetoothGatt().discoverServices());
                    }
                }
            });
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        scanBle();
    }

    private void initView() {
        vDevLv = findViewById(R.id.ble_dev_lv);
        vScan = findViewById(R.id.iv_scan);
        vReScan = findViewById(R.id.rl_scan);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("正在连接..");
        progressDialog.setCancelable(false);

        mDevList = new ArrayList<DeviceListBean>();
        adapter = new DevListAdapter(mDevList, MainActivity.this);
        vDevLv.setAdapter(adapter);
        vDevLv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DeviceListBean deviceListBean = mDevList.get(position);
                if (deviceListBean.getConnectState().equals("已连接")) {
                    T.show(MainActivity.this, "设备已连接！");
                    Intent intent = new Intent(MainActivity.this, BlueControlActivity.class);
                    startActivity(intent);
                    return;
                }
                vDevLv.setItemsCanFocus(false);
                MainActivity.this.position = position;/*
                deviceListBean.setConnectState(BluetoothGatt.STATE_CONNECTING);
                adapter.setDevList(mDevList);*/
                progressDialog.show();
                xfBluetooth.connect(deviceListBean.getBluetoothDevice());
            }
        });
    }

    private void scanBle() {
        xfBluetooth = XFBluetooth.getInstance(this);
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
