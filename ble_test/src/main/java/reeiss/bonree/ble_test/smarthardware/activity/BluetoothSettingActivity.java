package reeiss.bonree.ble_test.smarthardware.activity;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.bean.BleDevConfig;
import reeiss.bonree.ble_test.bean.PreventLosingCommon;
import reeiss.bonree.ble_test.blehelp.XFBluetooth;
import reeiss.bonree.ble_test.blehelp.XFBluetoothCallBack;
import reeiss.bonree.ble_test.utils.T;

import static reeiss.bonree.ble_test.bean.CommonHelp.getLinkLostAlert;

public class BluetoothSettingActivity extends AppCompatActivity implements View.OnClickListener {


    private XFBluetooth mXFBlue;
    private BluetoothGatt xfBluetoothGatt;
    private TextView tvBattery;
    private ProgressDialog mProgressDialog;
    private XFBluetoothCallBack mXFBluetoothCallBack = new XFBluetoothCallBack() {
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
            final String value = Arrays.toString(characteristic.getValue());
            Log.e("jerry", "读取数据  : " + value);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (linkLostAlert != null && characteristic == linkLostAlert) {
                            vAlert.setChecked(value.equals("[1]") ? true : false);
                        } else {
                            if (tvBattery != null)
                                tvBattery.setText(Arrays.toString(characteristic.getValue()) + " %");
                        }
                    }
                });
            }
        }

        @Override
        public void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressDialog.dismiss();
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        T.show(BluetoothSettingActivity.this, "设置成功！");
                    } else {
                        T.show(BluetoothSettingActivity.this, "设置失败！");
                    }
                }
            });
        }
    };
    private RelativeLayout rlRing;
    private MediaPlayer mp;
    private TextView tvRing;
    private EditText edDevName;
    private boolean isChecked;
    private Switch vAlert;
    private BluetoothGattCharacteristic linkLostAlert;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        initView();
        initBlue();
    }

    private void initView() {
        vAlert = (Switch) findViewById(R.id.sw_is_alert);
        tvBattery = (TextView) findViewById(R.id.tv_battery);
        rlRing = (RelativeLayout) findViewById(R.id.rl_ring);
        tvRing = (TextView) findViewById(R.id.tv_ring);
        edDevName = (EditText) findViewById(R.id.ed_devName);
        BleDevConfig currentDevConfig = XFBluetooth.getCurrentDevConfig();
        if (currentDevConfig != null) {
            tvRing.setText(currentDevConfig.getRingName());
            edDevName.setText(currentDevConfig.getAlias());
        }
        rlRing.setOnClickListener(this);
        vAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BluetoothSettingActivity.this.isChecked = isChecked;
            }
        });
        mProgressDialog = new ProgressDialog(this);
    }

    private void initBlue() {
        mXFBlue = XFBluetooth.getInstance(BluetoothSettingActivity.this);
        xfBluetoothGatt = mXFBlue.getXFBluetoothGatt();
        mXFBlue.addBleCallBack(mXFBluetoothCallBack);
        for (int i = 0; i < xfBluetoothGatt.getServices().size(); i++) {
            Log.e("jerryzhu", "initBlue: " + xfBluetoothGatt.getServices().get(i).getUuid());
        }

        //获取
        linkLostAlert = getLinkLostAlert(xfBluetoothGatt);
        if (linkLostAlert != null)
            xfBluetoothGatt.readCharacteristic(linkLostAlert);
        //读取当前电量
        BluetoothGattService mBatteryServer = xfBluetoothGatt.getService(UUID.fromString(PreventLosingCommon.Server_Battery_Level));
        if (mBatteryServer != null) {
            BluetoothGattCharacteristic mChBattery = mBatteryServer.getCharacteristic(UUID.fromString(PreventLosingCommon.CH_Battery_Level));
            xfBluetoothGatt.readCharacteristic(mChBattery);
        }
    }

    public void saveConfig(View view) {

        if (edDevName.getText().toString().isEmpty()) {
            T.show(this, "别名不能为空");
            return;
        }


      /*  Bit0:
        Bit0 = 1 时 Tagelf 被设置成断开连接后报警
                Bit0 = 0 时 Tagelf 被设置成断开连接后不报警*/
        if (isChecked) {
            //需要报警  1000 0000  -128   80
            if (linkLostAlert != null) {
                linkLostAlert.setValue(new byte[]{1});
//                linkLostAlert.setValue(new byte[Common_LinkLost_No_Alert]);
                boolean b = xfBluetoothGatt.writeCharacteristic(linkLostAlert);
                Log.e("jerry", "写入了: 1  " + b);
            } else {
                T.show(BluetoothSettingActivity.this, "关闭报警不支持！");
            }
        } else {
            if (linkLostAlert != null) {
                linkLostAlert.setValue(new byte[]{0});
                boolean b = xfBluetoothGatt.writeCharacteristic(linkLostAlert);
                Log.e("jerry", "写入了: 0  " + b);
            } else {
                T.show(BluetoothSettingActivity.this, "断开报警不支持！");
            }
        }
        BleDevConfig currentDevConfig = XFBluetooth.getCurrentDevConfig();
        assert currentDevConfig != null;
        currentDevConfig.setAlias(edDevName.getText().toString());
        currentDevConfig.update(currentDevConfig.getId());

        Intent intent = new Intent();
        intent.putExtra("name", edDevName.getText().toString());
        setResult(100, intent);
        finish();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_ring:
//                View mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
//                mDialogView.findViewById(R.id.rl_scan).setVisibility(View.GONE);
//                ListView lvRing = (ListView) mDialogView.findViewById(R.id.ble_dev_lv);
//                lvRing.setAdapter(new RingAdapter(this));
                dialogChoice();
                break;
        }
    }

    /**
     * 单选
     */
    private void dialogChoice() {
        final BleDevConfig currentDev = XFBluetooth.getCurrentDevConfig();

        final HashMap<String, Integer> nameMap = new HashMap();
        Field[] fields = R.raw.class.getDeclaredFields();
        final ArrayList<String> items = new ArrayList();
        for (int i = 0; i < fields.length; i++) {
            String name = fields[i].getName();
            if (name != null && name.startsWith("ring")) {
                try {
                    nameMap.put(name, fields[i].getInt(R.raw.class));
                    items.add(name);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        final String[] itemName = (String[]) items.toArray(new String[items.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择铃声");
        builder.setIcon(R.mipmap.widget_bar_device_over);
        //todo 读取数据库，默认选中当前选择的铃声
        final int ringPosition = currentDev.getRingPosition();

        builder.setSingleChoiceItems(itemName, ringPosition,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (0 <= which && which <= itemName.length - 1) {
                            if (itemName[which] != null) {
                                // T.show(BluetoothSettingActivity.this, itemName[which]);
                                Integer resID = nameMap.get(itemName[which]);
                                currentDev.setRingResId(resID);

                                if (mp != null) {
                                    mp.reset();
                                    mp.release();
                                }
                                mp = MediaPlayer.create(BluetoothSettingActivity.this, resID);//重新设置要播放的音频
                                mp.start();//开始播放
                            }
                        }
                    }
                });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int checkedItemPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                if (0 <= checkedItemPosition && checkedItemPosition <= itemName.length - 1 && tvRing != null) {
                    // TODO: 2018/6/19 保存数据库
                    currentDev.setRingPosition(checkedItemPosition);
                    currentDev.setRingName(itemName[checkedItemPosition]);

                    tvRing.setText(itemName[checkedItemPosition]);
                }
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                currentDev.update(currentDev.getId());
                if (mp != null) {
                    mp.reset();
                    mp.release();
                    mp = null;
                }
            }
        });

    }
}
