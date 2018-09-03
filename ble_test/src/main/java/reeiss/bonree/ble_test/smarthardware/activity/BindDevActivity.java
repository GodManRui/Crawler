package reeiss.bonree.ble_test.smarthardware.activity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ListView;

import org.litepal.LitePal;

import java.lang.reflect.Field;
import java.util.ArrayList;

import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.bean.BleDevConfig;
import reeiss.bonree.ble_test.bean.DeviceListBean;
import reeiss.bonree.ble_test.blehelp.XFBluetooth;
import reeiss.bonree.ble_test.blehelp.XFBluetoothCallBack;
import reeiss.bonree.ble_test.smarthardware.adapter.DevListAdapter;
import reeiss.bonree.ble_test.utils.T;

public class BindDevActivity extends AppCompatActivity {

    private View vReScan;
    private View imScan;
    private ListView vDevLv;
    private ArrayList<DeviceListBean> mDevList;
    private XFBluetooth xfBluetooth;
    private DevListAdapter adapter;
    private XFBluetoothCallBack gattCallback = new XFBluetoothCallBack() {
        @Override
        public void onScanResult(final BluetoothDevice device) {
            Log.e("jerryzhu", "扫描结果: " + device.getName());
            if (TextUtils.isEmpty(device.getName()) || !device.getName().contains("iTAG")) return;
            // xfBluetooth.stop();
            for (int i = 0; i < mDevList.size(); i++) {
                if (mDevList.get(i).getBluetoothDevice().getAddress().equals(device.getAddress())) {
                    Log.e("JerryZhu", "onScanResult: 列表已存在!!!!!!");
                    return;
                }
            }
            if (vReScan.getVisibility() == View.VISIBLE) {
                imScan.clearAnimation();
                vReScan.setVisibility(View.GONE);
            }
            if (vDevLv.getVisibility() == View.GONE) {
                vDevLv.setVisibility(View.VISIBLE);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BleDevConfig bleDevConfig = LitePal.where("mac=?", device.getAddress()).findFirst(BleDevConfig.class);
                    if (bleDevConfig != null && !TextUtils.isEmpty(bleDevConfig.getAlias()))
                        mDevList.add(new DeviceListBean(device, BluetoothGatt.STATE_DISCONNECTED, bleDevConfig.getAlias()));
                    else
                        mDevList.add(new DeviceListBean(device, BluetoothGatt.STATE_DISCONNECTED));

                    adapter.setDevList(mDevList);
                }
            });
        }
    };
    private boolean addSuccess;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bind_activity);
        initView();
        initBle();
    }

    private void initView() {
        setTitle("绑定设备");
        imScan = findViewById(R.id.iv_scan);
        vReScan = findViewById(R.id.rl_bd_scan);
        vDevLv = (ListView) findViewById(R.id.lv_bind_dev);
        mDevList = new ArrayList<DeviceListBean>();
        adapter = new DevListAdapter(mDevList, this);
        vDevLv.setAdapter(adapter);
        vDevLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                xfBluetooth.stop();
                //添加设备，只是添加到本地数据库中，这里不做连接，过滤已添加的设备
                DeviceListBean deviceListBean = mDevList.get(position);
                String address = deviceListBean.getBluetoothDevice().getAddress();
                if (TextUtils.isEmpty(address)) return;
                BleDevConfig devConfig = LitePal.where("mac=?", address).findFirst(BleDevConfig.class);
                if (devConfig != null) {
                    T.show(BindDevActivity.this, "此设备已经添加过");
                } else {
                    //没有添加过 ， 要往数据库写入设备
                    Field[] fields = R.raw.class.getDeclaredFields();
                    BleDevConfig currentDevConfig = null;
                    try {
                        BluetoothDevice bluetoothDevice = mDevList.get(position).getBluetoothDevice();
                        currentDevConfig = new BleDevConfig
                            (bluetoothDevice.getAddress(), bluetoothDevice.getName(), fields[1].getName(), 0, fields[1].getInt(R.raw.class), 3);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    boolean save = currentDevConfig.save();
                    if (save) {
                        addSuccess = true;
                        T.show(BindDevActivity.this, "添加成功！");
                    }
                }
            }
        });
    }

    private void initBle() {
        xfBluetooth = XFBluetooth.getInstance(this);
        xfBluetooth.addBleCallBack(gattCallback);
        xfBluetooth.scan();
        startScan();
    }

    private void startScan() {
        if (vReScan.getVisibility() == View.VISIBLE) {
            RotateAnimation animation = new RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setInterpolator(new LinearInterpolator());
            animation.setDuration(2000);
            animation.setRepeatCount(-1);
            animation.setFillAfter(true);
            imScan.startAnimation(animation);
        }
    }

    @Override
    public void onBackPressed() {
        if (addSuccess) {
            setResult(200);
        } else {
            super.onBackPressed();
        }
        finish();
    }

    public void btStopScan(View view) {
        xfBluetooth.stop();
        finish();
    }

}
