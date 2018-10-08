package reeiss.bonree.ble_test.smarthardware.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.lang.reflect.Field;
import java.util.ArrayList;

import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.bean.BleDevConfig;
import reeiss.bonree.ble_test.bean.DeviceAndRssi;
import reeiss.bonree.ble_test.blehelp.XFBluetooth;
import reeiss.bonree.ble_test.blehelp.XFBluetoothCallBack;
import reeiss.bonree.ble_test.smarthardware.adapter.ScanResultAdapter;
import reeiss.bonree.ble_test.utils.T;

public class BindDevActivity extends AppCompatActivity {

    private View vReScan;
    private View imScan;
    private ListView vDevLv;
    private ArrayList<DeviceAndRssi> mDevList;
    private XFBluetooth xfBluetooth;
    private ScanResultAdapter adapter;
    private boolean isHint = false;
    private XFBluetoothCallBack gattCallback = new XFBluetoothCallBack() {
        @Override
        public void onScanResult(final BluetoothDevice device, final int rssi) {
//            if (TextUtils.isEmpty(device.getName()) || !device.getName().contains("iTAG")) return;
            Log.e("jerryzhu", "扫描结果: " + device.getName());
            // xfBluetooth.stop();

            for (int i = 0; i < mDevList.size(); i++) { //已经在扫描列表里了
                if (mDevList.get(i).getDevice().getAddress().equals(device.getAddress())) {
                    Log.e("JerryZhu", "onScanResult: 列表已存在!!!!!!");
                    return;
                }
            }
            BleDevConfig devConfig = LitePal.where("mac=?", device.getAddress()).findFirst(BleDevConfig.class);
            if (devConfig != null) {           //之前数据库内已经有过配置了
                return;
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
                    if (!isHint) {
                        Toast.makeText(BindDevActivity.this, "点击设备可添加到设备列表内", Toast.LENGTH_LONG).show();
                        isHint = true;
                    }
                    mDevList.add(new DeviceAndRssi(device, rssi));
                    adapter.setDevList(mDevList);
                }
            });
        }
    };
    private boolean addSuccess;
//    private ArrayList<BleDevConfig> mBindList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bind_activity);
        initView();
        initBle();
    }

    private void initView() {
        setTitle("绑定设备");
        ActionBar mActionBar = getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp);
        imScan = findViewById(R.id.iv_scan);
        vReScan = findViewById(R.id.rl_bd_scan);
        vDevLv = (ListView) findViewById(R.id.lv_bind_dev);
        mDevList = new ArrayList<>();
        adapter = new ScanResultAdapter(mDevList, this);
        vDevLv.setAdapter(adapter);
        vDevLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                xfBluetooth.stop();
                //添加设备，只是添加到本地数据库中，这里不做连接，过滤已添加的设备
                DeviceAndRssi deviceListBean = mDevList.get(position);
                String address = deviceListBean.getDevice().getAddress();
                if (TextUtils.isEmpty(address)) return;
                BleDevConfig devConfig = LitePal.where("mac=?", address).findFirst(BleDevConfig.class);
                if (devConfig != null) {
                    T.show(BindDevActivity.this, "此设备已经添加过");
                } else {
                    //没有添加过 ， 要往数据库写入设备
                    Field[] fields = R.raw.class.getDeclaredFields();
                    BleDevConfig currentDevConfig = null;
                    try {
                        BluetoothDevice bluetoothDevice = mDevList.get(position).getDevice();
                        currentDevConfig = new BleDevConfig
                            (bluetoothDevice.getAddress(), bluetoothDevice.getName(), fields[1].getName(), 0, fields[1].getInt(R.raw.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    boolean save = currentDevConfig.save();
                    if (save) {
                        addSuccess = true;
//                        mBindList.add(currentDevConfig);
                        Toast.makeText(BindDevActivity.this, "添加成功,可返回上一页面进行链接！", Toast.LENGTH_LONG).show();
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
    protected void onDestroy() {
        xfBluetooth.stop();
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        MyFinish();
        return super.onSupportNavigateUp();
    }

    private void MyFinish() {
        if (addSuccess) {
            Intent intent = new Intent();
            intent.putExtra("addBindDev", addSuccess);
            setResult(200, intent);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        MyFinish();
    }

    public void btStopScan(View view) {
        xfBluetooth.stop();
        finish();
    }
}
