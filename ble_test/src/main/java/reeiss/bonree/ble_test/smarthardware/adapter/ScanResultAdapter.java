package reeiss.bonree.ble_test.smarthardware.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.bean.DeviceAndRssi;
import reeiss.bonree.ble_test.utils.Utils;

public class ScanResultAdapter extends BaseAdapter {
    private List<DeviceAndRssi> devList;
    private Context context;

    public ScanResultAdapter(List<DeviceAndRssi> devList, Context context) {
        this.devList = devList;
        this.context = context;
    }

    public void setDevList(List<DeviceAndRssi> devList) {
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
    public View getView(int position, View v, ViewGroup parent) {
        v = LayoutInflater.from(context).inflate(R.layout.item_scan_dev_list, null);
        TextView tvDevName = v.findViewById(R.id.tv_scan_dev_name);
        ImageView imRssi = v.findViewById(R.id.im_scan_rssi);
        tvDevName.setText(devList.get(position).getDevice().getName());
        imRssi.setImageResource(Utils.getRssiDrawable(devList.get(position).getRssi()));
        return v;
    }
}