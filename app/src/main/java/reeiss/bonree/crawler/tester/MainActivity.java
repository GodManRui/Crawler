package reeiss.bonree.crawler.tester;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import reeiss.bonree.crawler.R;
import reeiss.bonree.crawler.bean.CommunityHome;

public class MainActivity extends AppCompatActivity {

    private List<CommunityHome> communityBean;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //StatusBarColor.setWindowsTranslucent(this);
        setContentView(R.layout.activity_main);
    }

    public void start(View v) {
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在抓取数据...");
        dialog.setCancelable(false);
        dialog.show();
        communityBean = new ArrayList<>();
        initData();
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    for (int j = 1; j < 2; j++) {
                        Connection connect = Jsoup.connect("https://testerhome.com/topics/popular?page=" + j);
                        //  connect.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:59.0) Gecko/20100101 Firefox/59.0");
                        connect.header("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Mobile Safari/537.36");
                        doc = connect.get();
                        doc = Jsoup.parse(doc.toString());

                        /*    Elements elementsByClass = doc.getElementsByClass("avatar media-left"); */

                           /* Elements elementsA = doc.select("[class~=topic media topic-\\d{4}]");
                            Log.e("JerryZhu", "elementsA : " + elementsA.size() + "  ==  " + elementsA);*/
                        Elements elementss = doc.getElementsByClass("panel-body item-list");
                        Elements elements = elementss.select("[class~=topic media topic-\\d{4}]");
                        // Log.e("JerryZhu", "elements : " + elements.size() + "  ==  " + elements);
                        for (int i = 0; i < elements.size(); i++) {
                            Element element = elements.get(i);
                            //左边 名字 头像
                            Element avatarLeft = element.select("a").first();

                            String userName = avatarLeft.attr("title");
                            //  Log.e("JerryZhu", i + "名字  ：" + userName);
                            String userInfo = avatarLeft.attr("href");
                            //   Log.e("JerryZhu", "点击名字跳转 2 ：" + userInfo);

                            String imageUrl = avatarLeft.select("img").first().attr("src");
                            //  Log.e("JerryZhu", "头像地址: https://testerhome.com" + imageUrl);

                            Elements titleHeading = element.select("[class=title media-heading]");
                            String title = titleHeading.select("a").attr("title");
                            // Log.w("JerryZhu", "run: 标题 ： " + title);
                            String contentUrl = titleHeading.select("a").first().attr("href");

                            // Log.w("JerryZhu", "run: 正文连接 ：" + contentUrl);
                            String node = titleHeading.select("a").first().select("span").text();
                            //Log.w("JerryZhu", "run: Node主题 ： " + node);
                            Element i1 = titleHeading.select("i").first();
                           /* if (i1 != null)
                                Log.w("JerryZhu", "run: 是否置顶 ： " + i1.attr("title"));*/

                            communityBean.add(new CommunityHome(userName, userInfo, imageUrl, title, contentUrl, node));
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initLv();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void initLv() {
        ListView lv = findViewById(R.id.lv);
        lv.setAdapter(new MyAdapter());
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(MainActivity.this, WebActivity.class).putExtra("url", communityBean.get(position).contentUrl));
            }
        });
        dialog.cancel();
        Toast.makeText(this, "爬取完成！", Toast.LENGTH_SHORT).show();
    }

    private class MyAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return communityBean.size();
        }


        @Override
        public Object getItem(int position) {
            return communityBean.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (viewHolder == null) {
                convertView = View.inflate(MainActivity.this, R.layout.item, null);
                viewHolder = new ViewHolder();
                viewHolder.node = (TextView) convertView.findViewById(R.id.tv_node);
                viewHolder.title = (TextView) convertView.findViewById(R.id.tv_title);
                viewHolder.head = (ImageView) convertView.findViewById(R.id.im_user_head);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Picasso.get().load(communityBean.get(position).getUserImageUrl()).into(viewHolder.head);
            viewHolder.title.setText(communityBean.get(position).getTitle());
            viewHolder.node.setText(communityBean.get(position).node);
            return convertView;
        }

        class ViewHolder {
            private TextView node;
            private TextView title;
            private ImageView head;
        }
    }
}
