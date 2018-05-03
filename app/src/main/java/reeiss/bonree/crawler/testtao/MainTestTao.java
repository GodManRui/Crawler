package reeiss.bonree.crawler.testtao;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import reeiss.bonree.crawler.R;
import reeiss.bonree.crawler.bean.CatalogueBean;
import reeiss.bonree.crawler.bean.CatalogueBean.InfoBean;

/**
 * Created by GodRui on 2018/3/21.
 */

public class MainTestTao extends Activity {

    public MyHandler myHandler;
    private Document parse;
    private ProgressDialog dialog;
    private ArrayList<CatalogueBean> listCatalogue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_tao);
        myHandler = new MyHandler(MainTestTao.this);

    }

    public void start(View view) {
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在获取目录...");
        dialog.setCancelable(false);
        dialog.show();
        // initData();
         new ConnectPage(this).start();
    }

    private void initData() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Connection connect = Jsoup.connect("http://www.testtao.cn/?page_id=4993");
                connect.header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");
                try {
                    Document document = connect.get();
                    parse = Jsoup.parse(document.toString(), "http://www.testtao.cn");
                    Elements itemMenu = parse.getElementsByClass("menu-item dropdown");
                    listCatalogue = new ArrayList<>(itemMenu.size());
                    for (int i = 0; i < itemMenu.size(); i++) {
                        Element element = itemMenu.get(i);
                        String title = element.select("a").first().text();
                        CatalogueBean bean = new CatalogueBean();
                        bean.setTitle(title);

                        Elements selectItem = element.select("[class=menu-item]");
                        ArrayList<InfoBean> infoBean = new ArrayList<>();
                        for (int i1 = 0; i1 < selectItem.size(); i1++) {
                            Element element1 = selectItem.get(i1).select("a").first();
                            String url = element1.attr("abs:href");
                            String lessTitle = element1.text();
                            infoBean.add(new InfoBean(lessTitle, url));
                        }
                        bean.setInfo(infoBean);
                        listCatalogue.add(bean);
                    }
                    myHandler.sendEmptyMessage(100);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    static class MyHandler extends Handler {
        WeakReference<MainTestTao> mOuter;

        public MyHandler(MainTestTao activity) {
            mOuter = new WeakReference<MainTestTao>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainTestTao mainTestTao = mOuter.get();
            if (mainTestTao != null)
                switch (msg.what) {
                    case 100:
                        Toast.makeText(mainTestTao, "目录抓取完成", Toast.LENGTH_SHORT).show();
                        mainTestTao.dialog.setMessage("正在获取目录...");
                        DataSupport.saveAll(mainTestTao.listCatalogue);
                        ConnectManager manager = new ConnectManager(mainTestTao.listCatalogue);
                        manager.start();
                        break;
                }
        }
    }
}
