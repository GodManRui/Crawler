package reeiss.bonree.androidtojs;

import android.content.DialogInterface;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebView.PictureListener;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    private WebView wb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wb = findViewById(R.id.wb);
        WebSettings wbSetting = wb.getSettings();
        wbSetting.setJavaScriptEnabled(true);
     //   wb.addJavascriptInterface(new MainActivity(),"callAndroid");
        // 设置允许JS弹窗
        //wbSetting.setJavaScriptCanOpenWindowsAutomatically(true);

      /*  WebViewClient client = new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e("JerryZhu", "onPageFinished: ");
            }
        };
        wb.setWebViewClient(client);
        wb.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
              //  Log.e("JerryZhu", "onJsAlert: " + view + "   url=" + url + "  message=" + message + "  result=" + result);
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setTitle("Alert");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }

        });*/
        //wb.loadUrl("file:///android_asset/Demo2_page跳转.html");
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
    @JavascriptInterface
    public boolean callAndroid(String title, final String msg) {
        Log.e("JerryZhu", " title = "+title+"   msg="+msg+"   =callAndroid+= "+Thread.currentThread().getName() );
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("JerryZhu", "run: = "+Thread.currentThread().getName() );
                AlertDialog.Builder b = new AlertDialog.Builder(getApplicationContext());
                b.setTitle("Alert");
                b.setMessage(msg);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                b.setCancelable(false) .create().show();
            }
        });

        return true;
    }
}
