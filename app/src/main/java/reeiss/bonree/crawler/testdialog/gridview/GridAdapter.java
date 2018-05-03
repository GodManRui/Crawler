package reeiss.bonree.crawler.testdialog.gridview;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;

import reeiss.bonree.crawler.R;

/**
 * Created by GodRui on 2018/4/20.
 */

public class GridAdapter extends BaseAdapter {
    private String[] mNameList;
    private LayoutInflater mInflater;

    public GridAdapter(Context context, String[] nameList) {
        this.mNameList = nameList;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return mNameList.length;
    }

    public Object getItem(int position) {
        return mNameList[position];
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.net_task_infodata_radiobutton, null);
        ((RadioButton) convertView).setText(mNameList[position]);
        ((RadioButton) convertView).setId(position + 1);
        AutoUtils.autoSize(convertView);
        return convertView;
    }
}
