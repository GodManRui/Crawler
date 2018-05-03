package reeiss.bonree.crawler.testdialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import reeiss.bonree.crawler.R;
import reeiss.bonree.crawler.testdialog.gridview.GridDialog;

/**
 * Created by GodRui on 2018/4/19.
 */

public class TestActivity extends Activity {

    private GridDialog smartDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPop();
    }

    private void initPop() {
        ArrayList<SmartPopupItem> list = new ArrayList<SmartPopupItem>();
        list.add(new SmartPopupItem(R.mipmap.sx1, new String[]{"全部日志", "报警日志", "解除日志"}));
        list.add(new SmartPopupItem(R.mipmap.sx2, new String[]{"全部报警", "严重报警", "普通报警"}));
        list.add(new SmartPopupItem(R.mipmap.sx3, new String[]{"启用", "禁用"}));
        smartDialog = new GridDialog(this);
        smartDialog.setContentView(list);

    }

    public void start(View v) {
        smartDialog.show();
    }
}
