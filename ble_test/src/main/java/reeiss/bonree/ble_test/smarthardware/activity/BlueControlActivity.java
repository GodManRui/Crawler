package reeiss.bonree.ble_test.smarthardware.activity;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.UUID;

import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.bean.PreventLosingCommon;
import reeiss.bonree.ble_test.utils.T;
import reeiss.bonree.ble_test.blehelp.XFBluetooth;
import reeiss.bonree.ble_test.blehelp.XFBluetoothCallBack;


public class BlueControlActivity extends AppCompatActivity implements OnClickListener {

    private TextView tvDevName;
    private ImageView imRssi;
    private Button btnCall;
    private ImageView imSetting;
    private boolean isAlert;
    private BluetoothGattService alertService;
    private BluetoothGattCharacteristic alertCharacteristic;
    private Handler mHandler = new Handler();
    private XFBluetooth mXFBluetooth;
    private BluetoothGatt mXFBluetoothGatt;
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mXFBluetoothGatt.readRemoteRssi();
            mHandler.postDelayed(this, 2500);
        }
    };
    private XFBluetoothCallBack mXFBluetoothControl = new XFBluetoothCallBack() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState != BluetoothProfile.STATE_CONNECTED) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, final int rssi, int status) {
            if (imRssi != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (rssi > -45) {
                            imRssi.setImageResource(R.mipmap.ic_rssi_5);
                        } else if (rssi > -65) {
                            imRssi.setImageResource(R.mipmap.ic_rssi_4);
                        } else if (rssi > -85) {
                            imRssi.setImageResource(R.mipmap.ic_rssi_3);
                        } else if (rssi > -100) {
                            imRssi.setImageResource(R.mipmap.ic_rssi_2);
                        } else if (rssi > -110) {
                            imRssi.setImageResource(R.mipmap.ic_rssi_1);
                        } else if (rssi > -120) {
                            imRssi.setImageResource(R.mipmap.ic_rssi_0);
                        }
                    }
                });
                //  Log.e("JerryZhu", "onReadRemoteRssi: " + rssi);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blue_contrlo);
        initBle();
        initView();
    }

    private void initBle() {
        mXFBluetooth = XFBluetooth.getInstance(this);
        mXFBluetooth.addBleCallBack(mXFBluetoothControl);
        mXFBluetoothGatt = mXFBluetooth.getXFBluetoothGatt();
        // alertService = mXFBluetoothGatt.getService(UUID.fromString(PreventLosingCommon.Server_Immediate_Alert_ShuiDi));
        alertService = mXFBluetoothGatt.getService(PreventLosingCommon.getServerImmediateAlert());
        Log.e("jerryzhu", "服务扫描结果2   : " + mXFBluetoothGatt.getServices().size());
        for (int i = 0; i < mXFBluetoothGatt.getServices().size(); i++) {
            Log.e("jerryzhu", "服务扫描结果2   : " + mXFBluetoothGatt.getServices().get(i).getUuid());
        }
        if (alertService != null)
            // alertCharacteristic = alertService.getCharacteristic(UUID.fromString(PreventLosingCommon.CH_Immediate_Alert_ShuiDi));
            alertCharacteristic = alertService.getCharacteristic(PreventLosingCommon.getCHImmediateAlert());
        else
            T.show(this, "服务未找到！");
    }

    private void initView() {
        mHandler.postDelayed(runnable, 1000);
        tvDevName = findViewById(R.id.tv_dev_name);
        imRssi = findViewById(R.id.im_rssi);
        btnCall = findViewById(R.id.btn_call);
        imSetting = findViewById(R.id.im_setting);

        imSetting.setOnClickListener(this);
        btnCall.setOnClickListener(this);

        tvDevName.setText(mXFBluetoothGatt.getDevice().getName().trim());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
        if (mXFBluetoothControl != null) {
            mXFBluetooth.removeBleCallBack(mXFBluetoothControl);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.im_setting:
                startActivity(new Intent(this, BluetoothSettingActivity.class));
                break;
            case R.id.btn_call:
                if (!isAlert) {
                    alertCharacteristic.setValue(new byte[]{PreventLosingCommon.Common_High_immediate_Alert});
                    mXFBluetoothGatt.writeCharacteristic(alertCharacteristic);
                    btnCall.setText("正在呼叫");
                    isAlert = true;
                } else {
                    alertCharacteristic.setValue(new byte[]{PreventLosingCommon.Common_No_immediate_Alert});
                    mXFBluetoothGatt.writeCharacteristic(alertCharacteristic);
                    btnCall.setText("呼叫");
                    isAlert = false;
                }
                break;
        }
    }

}
