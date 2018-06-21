package reeiss.bonree.ble_test;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DevListAdapter extends BaseAdapter {
    private ArrayList<DeviceListBean> devList;
    private Context context;


    public DevListAdapter(ArrayList<DeviceListBean> devList, Context context) {
        this.devList = devList;
        this.context = context;
    }

    public void setDevList(ArrayList<DeviceListBean> devList) {
        this.devList = devList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return devList.size();
    }


    @Override
    public Object getItem(int position) {
        return position;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_dev_list, null);
        TextView tvDevName = convertView.findViewById(R.id.tv_dev_name);
        TextView tvConnectState = convertView.findViewById(R.id.tv_connect_state);
        DeviceListBean device = devList.get(position);
        tvDevName.setText(device.getBluetoothDevice().getName());
        tvConnectState.setText(device.getConnectState());
        return convertView;
    }
}
