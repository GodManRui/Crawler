package reeiss.bonree.ble_test.smarthardware.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import org.litepal.LitePal;

import java.util.List;

import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.bean.WuRaoWifiConfig;
import reeiss.bonree.ble_test.smarthardware.adapter.WiFiAdapter;
import reeiss.bonree.ble_test.utils.T;

public class WifiSpoceActivity extends AppCompatActivity {


    private ListView lvWifiList;
    private List<WuRaoWifiConfig> wifiList;
    private WiFiAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi);
        initView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void initView() {
        ActionBar mActionBar = getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("勿扰区域设置");
        lvWifiList = findViewById(R.id.lv_wifi_list);
        wifiList = LitePal.findAll(WuRaoWifiConfig.class);
        adapter = new WiFiAdapter(this, wifiList);
        lvWifiList.setAdapter(adapter);
        lvWifiList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder del = new AlertDialog.Builder(WifiSpoceActivity.this)
                    .setTitle("删除此区域")
                    .setMessage("确认删除此勿扰区域？")
                    .setCancelable(false)
                    .setNegativeButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            long configId = wifiList.get(position).getId();
                            LitePal.delete(WuRaoWifiConfig.class, configId);
                            wifiList.remove(position);
                            adapter.setData(wifiList);
                        }
                    })
                    .setPositiveButton("取消", null);
                del.create().show();
                return true;
            }
        });
    }

    public void addWifi(View view) {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wm == null) return;
// 获取当前所连接wifi的信息
        final WifiInfo wi = wm.getConnectionInfo();
        if (wi == null) return;
        String macAddress = wi.getMacAddress();
        if ("02:00:00:00:00:00".equals(macAddress)) {
            macAddress = wi.getBSSID();
        }
        Log.e("jerry", "Wifi名字: " + wi.getSSID() + "   mac= " + wi.getMacAddress());
        WuRaoWifiConfig has = LitePal.where("wifiMac=?", macAddress).findFirst(WuRaoWifiConfig.class);
        if (has != null) {
            T.show(this, "当前区域已添加");
            return;
        }
        final EditText edit = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置当前区域名字");
        builder.setIcon(R.mipmap.widget_bar_device_over);
        builder.setView(edit);
        final String finalMacAddress = macAddress;
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = edit.getText().toString();
                if (name.isEmpty()) {
                    T.show(WifiSpoceActivity.this, "请输入此区域名称！");
                    return;
                }

                dialog.dismiss();
                WuRaoWifiConfig wuRaoWifiConfig = new WuRaoWifiConfig(wi.getSSID().replace("\"", ""), name, finalMacAddress);
                wuRaoWifiConfig.save();
                wifiList.add(wuRaoWifiConfig);
                adapter.setData(wifiList);

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
