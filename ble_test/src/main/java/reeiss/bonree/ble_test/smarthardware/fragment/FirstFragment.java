package reeiss.bonree.ble_test.smarthardware.fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.bean.BleDevConfig;
import reeiss.bonree.ble_test.bean.EventAddDev;
import reeiss.bonree.ble_test.bean.EventEditName;
import reeiss.bonree.ble_test.blehelp.XFBluetooth;
import reeiss.bonree.ble_test.blehelp.XFBluetoothCallBack;
import reeiss.bonree.ble_test.smarthardware.MainActivity;
import reeiss.bonree.ble_test.smarthardware.activity.BindDevActivity;
import reeiss.bonree.ble_test.smarthardware.activity.BlueControlActivity;
import reeiss.bonree.ble_test.smarthardware.adapter.DevListAdapter;
import reeiss.bonree.ble_test.utils.T;

import static reeiss.bonree.ble_test.blehelp.XFBluetooth.CURRENT_DEV_MAC;

public class FirstFragment extends Fragment {

    private XFBluetooth xfBluetooth;
    private ListView vDevLv;
    private DevListAdapter adapter;
    private int position;
    private List<BleDevConfig> mDevList;
    private ProgressDialog progressDialog;

    private XFBluetoothCallBack gattCallback = new XFBluetoothCallBack() {

        //链接状态发生改变
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {

         /*   BleDevConfig currentDevConfig = getMacDevConfig();
            if (currentDevConfig == null) {
                currentDevConfig = getMacDevConfig(gatt.getDevice().getAddress());
            }*/
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    StatusChange(status, newState);
                }
            });
        }
    };
    private MainActivity mainActivity;

    /**
     * 链接状态发生改变
     *
     * @param
     * @param status
     * @param newState
     */
    private void StatusChange(int status, final int newState) {
        if (progressDialog != null)
            progressDialog.dismiss();

        Log.e("jerry", "原来的状态: " + mDevList.get(position).getAlias() + "  " + mDevList.get(position).getConnectState() + "    " + status + "   " + newState);
        mDevList.get(position).setConnectState(newState);
        adapter.setDevList(mDevList);
        vDevLv.setItemsCanFocus(true);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e("JerryZhu", "onHiddenChanged: First 设备管理");
        if (!hidden) {
            Objects.requireNonNull(getActivity()).setTitle("设备管理");
        }
    }

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String newName = data.getStringExtra("newName");
                    mDevList.get(position).setAlias(newName);
                    adapter.setDevList(mDevList);
                }
            });
        } else if (resultCode == 200) {
//            ArrayList<BleDevConfig> addBindDev = (ArrayList<BleDevConfig>) data.getSerializableExtra("addBindDev");
            boolean addBindDev = data.getBooleanExtra("addBindDev", true);
            if (addBindDev) {
                mDevList = LitePal.findAll(BleDevConfig.class);
            }
            adapter.setDevList(mDevList);
            Log.e("jerry", "onActivityResult: Fragment notifyDataSetChanged");
//            adapter.notifyDataSetChanged();
        }
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.e("jerry", "onCreate: Fragment的bundle  " + savedInstanceState);
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("jerry", "onCreateView: Fragment的bundle  " + savedInstanceState);
        return inflater.inflate(R.layout.activity_main, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        handler = new Handler();
        mainActivity = (MainActivity) getActivity();
        initView();
        xfBluetooth = XFBluetooth.getInstance(getActivity());
        xfBluetooth.addBleCallBack(gattCallback);
        instanceListData(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        instanceListData(savedInstanceState);
        super.onViewStateRestored(savedInstanceState);
        Log.e("jerry", "Fragment onViewStateRestored: " + savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("devListData", (ArrayList) mDevList);
        super.onSaveInstanceState(outState);
        Log.e("JerryZhuMM", " Fragment onSaveInstanceState(Bundle outState保存状态)" + outState);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        xfBluetooth.removeBleCallBack(gattCallback);
        super.onDestroy();
        Log.e("JerryZhuMM", "Fragment onDestroy: ");
    }

    private void initView() {
//        getActivity().setTitle("设备管理");
        vDevLv = getView().findViewById(R.id.ble_dev_lv);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("正在连接设备");
        progressDialog.setMessage("请确保设备开机并在您周围...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                xfBluetooth.disconnect();
                xfBluetooth.reset();
            }
        });

        mDevList = LitePal.findAll(BleDevConfig.class);
        adapter = new DevListAdapter(this.mDevList, getActivity());
        vDevLv.setAdapter(adapter);
        vDevLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!XFBluetooth.getInstance(getActivity()).getAdapter().isEnabled()) {
                    T.show(getActivity(), "请先打开蓝牙再链接防丢器");
                    return;
                }
                //点击的是未连接的设备，但此时有其他设备已连接
                BleDevConfig deviceListBean = FirstFragment.this.mDevList.get(position);
                if (!TextUtils.isEmpty(CURRENT_DEV_MAC) && !CURRENT_DEV_MAC.equals(deviceListBean.getMac())) {
                    T.show(getActivity(), "如需连接本防丢器，请先断开当前连接防丢器");
                    return;
                }
                //点击的是已连接的设备
                if (XFBluetooth.getCurrentDevConfig() != null && deviceListBean.getConnectState().equals("已连接")) {
                    Intent intent = new Intent(getActivity(), BlueControlActivity.class);
                    startActivityForResult(intent, 100);
                    return;
                }
                //点的是可以连接的设备，开始连接
                vDevLv.setItemsCanFocus(false);
                FirstFragment.this.position = position;
                progressDialog.show();

                mainActivity.iService.connect(deviceListBean.getMac());
                //       xfBluetooth.connect(deviceListBean.getMac());

            }
        });

        vDevLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                final BleDevConfig deviceListBean = FirstFragment.this.mDevList.get(position);
                if (deviceListBean == null) return true;
                final String address = deviceListBean.getMac();

                if (deviceListBean.getConnectState().equals("已连接") && address.equals(CURRENT_DEV_MAC)) {
                    AlertDialog.Builder seleDia = new AlertDialog.Builder(getActivity())
                        .setItems(new String[]{"断开连接", "删除设备"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        mainActivity.iService.setDontAlert(true);
                                        xfBluetooth.disconnect();
                                        break;
                                    case 1:
                                        DelDev(deviceListBean, address);
                                        break;
                                }
                            }
                        });
                    seleDia.create().show();
                } else
                    DelDev(deviceListBean, address);
                return true;
            }
        });
    }

    private void instanceListData(@Nullable Bundle savedInstanceState) {
        if (XFBluetooth.getCurrentDevConfig() == null) return;
        if (savedInstanceState != null) {
            ArrayList devListData = (ArrayList) savedInstanceState.getSerializable("devListData");
            if (devListData != null) {
                mDevList = devListData;
                adapter.setDevList(mDevList);
            }
        }
    }

    private void DelDev(final BleDevConfig bleDevConfig, String address) {

        AlertDialog.Builder delDia = new AlertDialog.Builder(getActivity())
            .setTitle("删除设备")
            .setMessage("确认删除" + bleDevConfig.getAlias() + "并清空所有配置信息(包括昵称，定位记录等)？")
            .setNegativeButton("删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mDevList.remove(bleDevConfig);
                    adapter.setDevList(mDevList);
                    if (bleDevConfig.getConnectState().equals("已连接")) {
                        xfBluetooth.disconnect();
                    }
                    bleDevConfig.delete();
                }
            })
            .setPositiveButton("取消", null)
            .setCancelable(false);
        delDia.create().show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addDevResultCode200(EventAddDev event) {
        mDevList = LitePal.findAll(BleDevConfig.class);
        adapter.setDevList(mDevList);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void editNameResultCode100(EventEditName event) {
        String newName = event.newName;
        if (newName == null) return;
        mDevList.get(position).setAlias(newName);
        adapter.setDevList(mDevList);
    }

    //跳转到添加设备界面
    public void addDev() {
        if (!XFBluetooth.getInstance(getActivity()).getAdapter().isEnabled()) {
            T.show(getActivity(), "请先打开蓝牙再扫描");
            return;
        }
        Intent intent = new Intent(getActivity(), BindDevActivity.class);
        startActivityForResult(intent, 200);
    }
}
