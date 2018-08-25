package reeiss.bonree.ble_test.smarthardware.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.bean.WuRaoWifiConfig;
import reeiss.bonree.ble_test.smarthardware.activity.WifiSpoceActivity;

public class MyAdapter extends BaseAdapter {

    private List<WuRaoWifiConfig> wifiList;
    private WifiSpoceActivity wifiSpoceActivity;

    public MyAdapter(WifiSpoceActivity wifiSpoceActivity, List<WuRaoWifiConfig> wifiList) {
        this.wifiSpoceActivity = wifiSpoceActivity;
        this.wifiList = wifiList;
    }

    public void setData(List<WuRaoWifiConfig> wifiList) {
        this.wifiList = wifiList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return wifiList.size();
    }

    @Override
    public Object getItem(int position) {
        return wifiList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(wifiSpoceActivity).inflate(R.layout.item_lv_wifi, null);
        TextView tvNick = convertView.findViewById(R.id.tv_nick);
        TextView tvWifiName = convertView.findViewById(R.id.tv_wifi_name);
        WuRaoWifiConfig wifiConfig = (WuRaoWifiConfig) getItem(position);
        tvNick.setText(wifiConfig.getWifiNick());
        tvWifiName.setText(wifiConfig.getWifiName());

        return convertView;
    }
}