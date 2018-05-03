package reeiss.bonree.crawler.testtao;

import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import reeiss.bonree.crawler.bean.CatalogueBean;
import reeiss.bonree.crawler.utils.Utils;

import static reeiss.bonree.crawler.utils.Utils.printException;

/**
 * Created by GodRui on 2018/3/21.
 */

public class ConnectManager
    extends Thread {
    ArrayList<CatalogueBean> listCatalogue;
    private Document parse;
    private String lessTitle;


    public ConnectManager(ArrayList<CatalogueBean> listCatalogue) {
        this.listCatalogue = listCatalogue;
    }

    @Override
    public void run() {

        for (int j = 1; j < 7; j++) {
            CatalogueBean bean = listCatalogue.get(j);
            for (int i = 0; i < bean.getInfo().size(); i++) {
                // for (int i = 0; i < 1; i++) {
                Connection connect = Jsoup.connect(bean.getInfo().get(i).getLessTitleUrl());
                lessTitle = bean.getInfo().get(i).getLessTitle();
                connect.header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");
                try {
                    Document document = connect.get();
                    parse = Jsoup.parse(document.toString(), "http://www.testtao.cn");
                    Elements select = parse.getElementsByClass("entry-content clearfix").select("a[href]");
                    for (int i1 = 0; i1 < select.size(); i1++) {
                        //for (int i1 = 0; i1 < 1; i1++) {
                        String title = select.get(i1).text();
                        String url = select.get(i1).attr("href");
                        openMainBody(title, url, bean.getTitle());
                    }

                } catch (Exception e) {
                    printException(e);
                }
                Log.e("JerryZhu", "run: 下载完成" + lessTitle);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.e("JerryZhu", "run: 执行结束~  == " + j);
        }
    }

    //文章内容
    private void openMainBody(String title, String url, String dirName) {
        Connection connect = Jsoup.connect(url);
        connect.header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");
        try {
            Document document = connect.get();
            parse = Jsoup.parse(document.toString(), "http://www.testtao.cn");
            Elements select = parse.select("div.entry");

            Element content = select.select("div.entry-content.clearfix").first();
            dirName = dirName.replaceAll("/", "&&");
            if (content != null)
                Utils.printStringToFile(title, content.text(), dirName, lessTitle + ".txt", true);

        } catch (Exception e) {
            printException(e);
        }
    }
}
