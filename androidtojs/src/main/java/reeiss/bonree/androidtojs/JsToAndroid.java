package reeiss.bonree.androidtojs;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class JsToAndroid extends Object {
    @JavascriptInterface
    public boolean callAndroid(String title,String msg) {
       Log.e("JerryZhu", " title = "+title+"   msg="+msg+"   =callAndroid" );

        return false;
    }
}
