package reeiss.bonree.ble_test.smarthardware.activity;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.bean.PreventLosingCommon;
import reeiss.bonree.ble_test.utils.T;
import reeiss.bonree.ble_test.blehelp.XFBluetooth;
import reeiss.bonree.ble_test.blehelp.XFBluetoothCallBack;

public class BluetoothSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private Switch vAlert;
    private XFBluetooth mXFBlue;
    private boolean isAlert;
    private BluetoothGatt xfBluetoothGatt;
    private TextView tvBattery;
    private XFBluetoothCallBack mXFBluetoothCallBack = new XFBluetoothCallBack() {
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (tvBattery != null)
                            tvBattery.setText(Arrays.toString(characteristic.getValue()) + " %");
                    }
                });
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
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
    private ProgressDialog mProgressDialog;
    private BluetoothGattService mLinkLostServer;
    private RelativeLayout rlRing;
    private MediaPlayer mp;
    private TextView tvRing;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        initView();
        initBlue();
    }

    private void initBlue() {
        mXFBlue = XFBluetooth.getInstance(BluetoothSettingActivity.this);
        xfBluetoothGatt = mXFBlue.getXFBluetoothGatt();
        mXFBlue.addBleCallBack(mXFBluetoothCallBack);
        for (int i = 0; i < xfBluetoothGatt.getServices().size(); i++) {
            Log.e("jerryzhu", "initBlue: " + xfBluetoothGatt.getServices().get(i).getUuid());
        }
        mLinkLostServer = xfBluetoothGatt.getService(UUID.fromString(PreventLosingCommon.Server_LinkLost_Alert));
        BluetoothGattService mBatteryServer = xfBluetoothGatt.getService(UUID.fromString(PreventLosingCommon.Server_Battery_Level));
        if (mBatteryServer != null) {
            BluetoothGattCharacteristic mChBattery = mBatteryServer.getCharacteristic(UUID.fromString(PreventLosingCommon.CH_Battery_Level));
            xfBluetoothGatt.readCharacteristic(mChBattery);
        }
    }

    private void initView() {
        vAlert = (Switch) findViewById(R.id.sw_is_alert);
        tvBattery = (TextView) findViewById(R.id.tv_battery);
        rlRing = (RelativeLayout) findViewById(R.id.rl_ring);
        tvRing = (TextView) findViewById(R.id.tv_ring);
        //todo 读取数据库，回显当前设置的铃声
        rlRing.setOnClickListener(this);
        vAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //todo 小芳支持此功能
                isAlert = b;
            }
        });
        mProgressDialog = new ProgressDialog(this);
    }

    public void saveConfig(View view) {
        finish();
       /* BluetoothGattCharacteristic mLinkLostCharacteristic = mLinkLostServer.getCharacteristic(UUID.fromString(PreventLosingCommon.CH_LinkLost_Alert));
        if (mLinkLostCharacteristic != null) {
            Log.e("jerryzhu", "saveConfig: " + mLinkLostCharacteristic.getProperties() + "   ==  " + mLinkLostCharacteristic.getUuid());
            if (isAlert) {
                mLinkLostCharacteristic.setValue(new byte[]{PreventLosingCommon.Common_LinkLost_100Alert});
            } else {
                mLinkLostCharacteristic.setValue(new byte[]{(byte) PreventLosingCommon.Common_LinkLost_No_Alert});
            }
            xfBluetoothGatt.writeCharacteristic(mLinkLostCharacteristic);
        } else {
            T.show(this, "未搜索到该服务！");
            return;
        }
        mProgressDialog.setMessage("正在设置..");
        mProgressDialog.show();*/
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
        //final String items[] = {"男", "女", "其他"};
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
        builder.setSingleChoiceItems(itemName, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (0 <= which && which <= itemName.length - 1) {
                            if (itemName[which] != null) {
                                // T.show(BluetoothSettingActivity.this, itemName[which]);
                                Integer resID = nameMap.get(itemName[which]);
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
                if (mp != null) {
                    mp.reset();
                    mp.release();
                    mp = null;
                }
            }
        });

    }
}
