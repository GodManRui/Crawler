package reeiss.bonree.ble_test.smarthardware.activity;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.bean.PreventLosingCommon;
import reeiss.bonree.ble_test.blehelp.XFBluetooth;
import reeiss.bonree.ble_test.blehelp.XFBluetoothCallBack;
import reeiss.bonree.ble_test.utils.T;
import reeiss.bonree.ble_test.utils.Utils;

import static reeiss.bonree.ble_test.bean.CommonHelp.getImmediateAlert;


public class BlueControlActivity extends AppCompatActivity implements OnClickListener {

    private TextView tvDevName;
    private ImageView imRssi;
    private Button btnCall;
    private ImageView imSetting;
    private boolean isAlert;
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
                        imRssi.setImageResource(Utils.getRssiDrawable(rssi));
                    }
                });
                //  Log.e("JerryZhu", "onReadRemoteRssi: " + rssi);
            }
        }
    };
    private String mNewName;

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

        for (int i = 0; i < mXFBluetoothGatt.getServices().size(); i++) {
            Log.e("jerryzhu", "服务扫描结果   : " + mXFBluetoothGatt.getServices().get(i).getUuid());
        }

        alertCharacteristic = getImmediateAlert(mXFBluetoothGatt);
        if (alertCharacteristic == null)
            T.show(this, "报警服务未找到！");
    }

    private void initView() {
        mHandler.postDelayed(runnable, 1000);
        tvDevName = findViewById(R.id.tv_dev_name);
        imRssi = findViewById(R.id.im_rssi);
        btnCall = findViewById(R.id.btn_call);
        imSetting = findViewById(R.id.im_setting);

        imSetting.setOnClickListener(this);
        btnCall.setOnClickListener(this);

        tvDevName.setText(Objects.requireNonNull(XFBluetooth.getCurrentDevConfig()).getAlias());
        ActionBar mActionBar = getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("设备控制");
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
    public boolean onSupportNavigateUp() {
        MyFinish();
        return super.onSupportNavigateUp();
    }

    private void MyFinish() {
        if (!TextUtils.isEmpty(mNewName)) {
            Intent intent = new Intent();
            intent.putExtra("newName", mNewName);
            setResult(100, intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.im_setting:
                Intent intent = new Intent(this, BluetoothSettingActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.btn_call:
                if (!isAlert) {
                    if (alertCharacteristic == null) {
                        T.show(this, "暂未获得服务,请稍后再试");
                        return;
                    }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mNewName = data.getStringExtra("name");
                    if (!TextUtils.isEmpty(mNewName)) {
                        tvDevName.setText(mNewName);
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        MyFinish();
    }
}
