package reeiss.bonree.androidtojs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity2 extends AppCompatActivity {

    private WebView wb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        wb = findViewById(R.id.wb);
        WebSettings wbSetting = wb.getSettings();
        wbSetting.setJavaScriptEnabled(true);

        // wb.loadUrl("file:///android_asset/Demo2_page跳转.html");
        wb.loadUrl("file:///android_asset/ceshi2.html");
        //wb.loadUrl("file:///android_asset/Demo6_事件.html");
    }
    public void callJS(View view) {
        wb.evaluateJavascript("javascript:callJS()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.e("JerryZhu", "onReceiveValue: " + value);
            }
        });
    }

}
