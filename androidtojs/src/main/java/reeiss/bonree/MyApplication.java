package reeiss.bonree;

import android.app.Application;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

         android.webkit.WebView.setWebContentsDebuggingEnabled(true);

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onCoreInitFinished() {
            }

            @Override
            public void onViewInitFinished(boolean arg0) {
                Log.e("JerryZhu", "onViewInitFinished: " + arg0);
            }
        };

        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
            }

            @Override
            public void onInstallFinish(int i) {
            }

            @Override
            public void onDownloadProgress(int i) {
            }
        });

        QbSdk.initX5Environment(getApplicationContext(), cb);
    }
}
