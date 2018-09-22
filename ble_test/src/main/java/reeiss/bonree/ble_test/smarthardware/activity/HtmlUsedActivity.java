package reeiss.bonree.ble_test.smarthardware.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;

public class HtmlUsedActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView wb = new WebView(this);
        setContentView(wb);
        setTitle("防丢器使用说明");
        wb.loadUrl("file:///android_asset/used.htm");
    }
}
