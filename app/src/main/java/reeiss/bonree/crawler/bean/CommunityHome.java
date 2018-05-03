package reeiss.bonree.crawler.bean;

/**
 * Created by GodRui on 2018/3/19.
 */
/* Log.e("JerryZhu", i + "名字  ：" + avatarLeft.attr("title"));
                            Log.e("JerryZhu", "点击名字跳转 2 ： https://testerhome.com" + avatarLeft.attr("href"));
                            Log.e("JerryZhu", "头像地址: https://testerhome.com" + avatarLeft.select("img").first().attr("src"));

                            Elements titleHeading = element.select("[class=title media-heading]");
                            Log.w("JerryZhu", "run: 标题 ： " + titleHeading.select("a").attr("title"));
                            Log.w("JerryZhu", "run: 正文连接 ： https://testerhome.com" + titleHeading.select("a").first().attr("href"));
                            Log.w("JerryZhu", "run: Node主题 ： " + titleHeading.select("a").first().select("span").text());
                            //  if (titleHeading.select("i") != null && titleHeading.select("i").first().select("fa fa-thumb-tack").attr("title").equals("置顶"))
                            Element i1 = titleHeading.select("i").first();
                            if (i1 != null)
                                Log.w("JerryZhu", "run: 是否置顶 ： " + i1.attr("title"));*/
public class CommunityHome {
    String userName;    //用户名
    String userInfoUrl; //用户详情 链接
    String userImageUrl;  //用户头像 链接

    String title;   //帖子标题
    public String node;    //帖子节点类型
    public String contentUrl;//帖子地址


    public CommunityHome(String userName, String userInfoUrl, String userImageUrl, String title, String contentUrl, String node) {
        this.userName = userName;
        this.userInfoUrl = "https://testerhome.com" + userInfoUrl;
        this.userImageUrl = userImageUrl.startsWith("http") ? userImageUrl : "https://testerhome.com" + userImageUrl;
        this.title = title;
        this.node = node;
        this.contentUrl = "https://testerhome.com" + contentUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserInfoUrl() {
        return userInfoUrl;
    }

    public void setUserInfoUrl(String userInfoUrl) {
        this.userInfoUrl = userInfoUrl;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }
}
