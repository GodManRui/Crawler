package reeiss.bonree.ble_test.smarthardware.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.litepal.LitePal;

import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.bean.BleDevConfig;
import reeiss.bonree.ble_test.utils.T;

public class WifiSpoceActivity extends Activity {

    private TextView tvWifiName;
    private TextView tvSpoce;
    private TextView tvCurrentWifi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi);
        initView();
    }

    private void initView() {
        tvWifiName = findViewById(R.id.tvWifiName);
        tvSpoce = findViewById(R.id.tvName);
        tvCurrentWifi = findViewById(R.id.tvCurrentWifi);// 获取系统wifi服务


    }

    public void addWifi(View view) {
        final EditText edit = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置当前区域名字");
        builder.setIcon(R.mipmap.widget_bar_device_over);
        builder.setView(edit);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = edit.getText().toString();
                if (name.isEmpty()) {
                    T.show(WifiSpoceActivity.this, "请输入此区域名称！");
                    return;
                }
                WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
// 获取当前所连接wifi的信息
                WifiInfo wi = wm.getConnectionInfo();
                dialog.dismiss();
                if (wi == null) return;
                String ssid = wi.getSSID();
                if (ssid != null) {
                    tvWifiName.setText(ssid);
                }
                tvSpoce.setText(name);
                BleDevConfig currentDev = LitePal.findFirst(BleDevConfig.class);
                if (currentDev == null) {
                    return;
                }
                BleDevConfig bleDevConfig = new BleDevConfig();
                bleDevConfig.setAlert("false");
                bleDevConfig.update(currentDev.id);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }
}
