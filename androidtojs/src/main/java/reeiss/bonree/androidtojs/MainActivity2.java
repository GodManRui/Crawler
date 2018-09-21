package reeiss.bonree.androidtojs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

public class MainActivity2 extends AppCompatActivity {

    private WebView wb;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        wb = findViewById(R.id.wb);
        WebSettings wbSetting = wb.getSettings();
        wbSetting.setJavaScriptEnabled(true);

        // wb.loadUrl("file:///android_asset/Demo2_page跳转.html");
        wb.loadUrl("file:///android_asset/alert/alertDetails.html");
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
