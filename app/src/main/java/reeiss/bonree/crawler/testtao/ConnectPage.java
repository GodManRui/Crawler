package reeiss.bonree.crawler.testtao;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import reeiss.bonree.crawler.bean.CatalogueBean;

import static reeiss.bonree.crawler.utils.Utils.printException;

/**
 * Created by GodRui on 2018/3/23.
 */

public class ConnectPage extends Thread {

    private Document parse;
    private Context context;

    public ConnectPage(MainTestTao mainTestTao) {
        context = mainTestTao;
    }

    @Override
    public void run() {
        SAXReader reader = new SAXReader();
        try {
           /* DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new File("books.xml"));*/
            URL url = new URL("http://www.testtao.cn/?p=6487");
            org.dom4j.Document document1 = reader.read(url);
            Log.e("JerryZhu", "我已经执行了了了了le: " + document1);
            org.dom4j.Node node = document1.selectSingleNode("//div[@class='entry-content clearfix']/h1[1]/text()");
            Log.e("JerryZhu", "我已经执行了了了了le: " + node);
        } catch (Exception e) {
            printException(e);
        }


        StringBuffer baseDir = new StringBuffer(Environment.getExternalStorageDirectory().getPath() + "/111AAA/");
        for (int i = 6487; i < 6488; i++) {
            String baseUrl = "http://www.testtao.cn/?p=" + i;
            Connection connect = Jsoup.connect(baseUrl);
            connect.header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");
            try {
                Document document = connect.get();
                parse = Jsoup.parse(document.toString(), "http://www.testtao.cn");
                Elements breadcrumb = parse.getElementsByClass("breadcrumb").select("li");
                String fileName = "";
                for (int j = 0; j < breadcrumb.size(); j++) {
                    if (j != breadcrumb.size() - 1)
                        baseDir.append(breadcrumb.get(j).text() + "/");
                    else
                        fileName = breadcrumb.get(j).text() + ".txt";
                }

                File file = new File(baseDir.toString(), fileName);
                if (!file.getParentFile().exists())
                    file.getParentFile().mkdirs();
                if (!file.exists())
                    file.createNewFile();
                String title = parse.getElementsByClass("entry-title").text();
                Element contents = parse.getElementsByClass(
                    "entry-content clearfix").first();

                Elements elements = contents.select("*");
                StringBuffer sb = new StringBuffer();
                sb.append(title);
                sb.append("\r\n");
                for (int j = 1; j < elements.size(); j++) {
                    Node unwrap = elements.get(j).unwrap();
                    if (unwrap == null || unwrap.toString().equals("&nbsp;"))
                        sb.append("\r\n");
                    else if (unwrap.toString().isEmpty()) {
                        continue;
                    } else {
                        String replaceAll = unwrap.toString()
                            // .replaceAll("&.{0,4};", " ")
                            .replaceAll("<strong>", "")
                            .replaceAll("</strong>", "");
                        Log.e("jerryzhu", "原始数据     " + unwrap.toString());
                        Log.e("jerryzhu", "转换数据     " + replaceAll + "\r\n");
                        sb.append(replaceAll);
                        sb.append("\r\n");
                    }
                }
                Log.e("jerryzhu", String.valueOf(sb));
                // Util.printStringToFile(title, content, file, append)

            } catch (IOException e) {
                printException(e);
            }
        }
    }
}
