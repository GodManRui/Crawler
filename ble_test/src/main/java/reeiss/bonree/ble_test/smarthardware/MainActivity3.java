package reeiss.bonree.ble_test.smarthardware;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import reeiss.bonree.ble_test.R;

public class MainActivity3 extends AppCompatActivity {

    private RecyclerView Rv;
    private DoorAdapter myAdapter;
    private List<DataBean> listItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recy);// 初始化显示的数据
        initData();
        initView();
    }

    // 初始化显示的数据
    public void initData() {
        listItem = new ArrayList<>();/*在数组中存放数据*/
        for (int i = 0; i < 100; i++) {
            DataBean dataBean = new DataBean();
            if (i % 2 == 0) {
                dataBean.setStatus("门窗打开");
                dataBean.setAlert(true);
                dataBean.setOpen(1);
                dataBean.setTime("十点半");
            } else {
                dataBean.setStatus("门窗关闭");
                dataBean.setAlert(false);
                dataBean.setOpen(0);
                dataBean.setTime("十一点半");
            }
            listItem.add(dataBean);
        }
    }

    // 绑定数据到RecyclerView
    public void initView() {
        Rv = (RecyclerView) findViewById(R.id.my_recycler_view);
        //使用线性布局
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        Rv.setLayoutManager(layoutManager);
        Rv.setHasFixedSize(true);

//        //用自定义分割线类设置分割线
//        Rv.addItemDecoration(new DividerItemDecoration(this));

        //为ListView绑定适配器
        myAdapter = new DoorAdapter(this, listItem);
        Rv.setAdapter(myAdapter);
    }

}
