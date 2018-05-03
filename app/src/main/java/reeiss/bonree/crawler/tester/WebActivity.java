package reeiss.bonree.crawler.tester;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

import reeiss.bonree.crawler.R;
import reeiss.bonree.crawler.utils.Utils;

/**
 * Created by GodRui on 2018/3/19.
 */

public class WebActivity extends Activity {

    private Document document;
    private File file;
    private WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setFlags(flag, flag);

        setContentView(R.layout.webview);
        final String url = getIntent().getStringExtra("url");
        initData(url);
    }

    private void initData(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connection connect = Jsoup.connect(url);
                    connect.header("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Mobile Safari/537.36");
                    document = connect.get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                StringBuffer buffer = new StringBuffer();
                Document parse = Jsoup.parse(document.toString());
                Elements select = parse.select("[class=panel-body markdown markdown-toc]");
                Elements img = select.select("img");
                for (int i = 0; i < img.size(); i++) {
                    Element element = img.get(i);
                    String src = element.attr("src");
                    if (!src.startsWith("http")) {
                        element.attr("src", "https://testerhome.com" + src);
                        element.attr("width", "100%").attr("height", "auto");
                    }
                    if (element.attr("class").equals("twemoji")) {
                        element.attr("width", "4%").attr("height", "auto");
                    }
                }

                //  Elements head = parse.select("[class=panel-heading media clearfix]");
                Elements head = parse.select("[class=media-body]");
                head.select("a.node").first().html("");
                head.first().select("[class=info]").html("");

                Elements label = parse.select("[class=label-awesome]");
                buffer.append(head);
                buffer.append(label);
                buffer.append(select);
                file = Utils.printStringToFile(buffer, "tester.html", false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initView();
                    }
                });
            }
        }).start();
    }

    private void initView() {
        webView = findViewById(R.id.web);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);// 设置允许访问文件数据
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
      /*  settings.setSupportZoom(true);//支持放大网页功能
        settings.setBuiltInZoomControls(true);//支持缩小网页功能*/
        webView.setWebViewClient(new MyWebViewClient());
        webView.addJavascriptInterface(new JavaScriptInterface(), "imagelistner");
        webView.loadUrl("file://" + file.toString());
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // imgReset();
            addImageClickListner();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private void addImageClickListner() {
        webView.loadUrl("javascript:(function(){" +
            "var objs = document.getElementsByTagName(\"img\"); " +
            "for(var i=0;i<objs.length;i++)  " +
            "{"
            + "    objs[i].onclick=function()  " +
            "    {  "
            + "        window.imagelistner.openImage(this.src);  " +
            "    }  " +
            "}" +
            "})()");
    }

    private void imgReset() {
        webView.loadUrl("javascript:(function(){" +
            "var objs = document.getElementsByTagName('img'); " +
            "for(var i=0;i<objs.length;i++)  " +
            "{"
            + "var img = objs[i];   " +
            "    img.style.maxWidth = '100%'; img.style.height = 'auto';  " +
            "}" +
            "})()");
    }

    public class JavaScriptInterface {


        @JavascriptInterface
        public void openImage(String img) {
            Intent intent = new Intent();
            intent.putExtra("image", img);
            intent.setClass(WebActivity.this, BigImageActivity.class);
            startActivity(intent);
        }
    }
}
