package reeiss.bonree.ble_test.smarthardware.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.litepal.LitePal;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.bean.BleDevConfig;
import reeiss.bonree.ble_test.bean.Location;
import reeiss.bonree.ble_test.utils.T;

public class LostHistory extends AppCompatActivity {

    private ListView lv;
    private List<Location> lostHistories;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lost_history);
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
        setTitle("丢失记录");
        lostHistories = LitePal.findAll(Location.class);
        if (lostHistories.size() <= 0) {
            findViewById(R.id.tv_nodata).setVisibility(View.VISIBLE);
            T.show(this, "没有数据!");
            return;
        }

        lv = findViewById(R.id.lv_lost);
        MyAdapter myAdapter = new MyAdapter();
        lv.setAdapter(myAdapter);
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return lostHistories.size();
        }

        @Override
        public Object getItem(int position) {
            return lostHistories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(LostHistory.this).inflate(R.layout.lv_lost, null);
            TextView tvName = convertView.findViewById(R.id.tv_name);
            TextView tvTime = convertView.findViewById(R.id.tv_time);
//            TextView jingweidu = convertView.findViewById(R.id.tv_jingweidu);
            TextView weizhi = convertView.findViewById(R.id.tv_weizhistr);
//            TextView weizhimiaoshu = convertView.findViewById(R.id.tv_weizhimiaoashu);
            Location lostHistory = lostHistories.get(position);
            BleDevConfig ble = LitePal.where("mac=?", lostHistory.getMac()).findFirst(BleDevConfig.class);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//这个是你要转成后的时间的格式
            String sd = sdf.format(new Date(lostHistory.getTime()));   // 时间戳转换成时间
            tvTime.setText(sd);
            tvName.setText(ble.getAlias());
//            jingweidu.setText("经度:" + lostHistory.getLongitude() + "  维度:" + lostHistory.getLatitude());
            weizhi.setText(lostHistory.getAddStr());
//            weizhimiaoshu.setText(lostHistory.getLocationDescribe());
            return convertView;
        }
    }
}
