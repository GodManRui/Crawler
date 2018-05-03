package reeiss.bonree.crawler.bean;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by GodRui on 2018/3/21.
 */

public class CatalogueBean extends DataSupport {

    private String title;
    private List<InfoBean> info;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<InfoBean> getInfo() {
        return info;
    }

    public void setInfo(List<InfoBean> info) {
        this.info = info;
    }

    @Override
    public String toString() {
        StringBuffer infoBuffer = new StringBuffer();
        for (int i = 0; i < info.size(); i++) {
            infoBuffer.append(info.get(i).lessTitle + "   ");
            infoBuffer.append(info.get(i).lessTitleUrl + "\n");
        }
        return title + "   " + infoBuffer;
    }

    public static class InfoBean {

        private String lessTitle;
        private String lessTitleUrl;

        public InfoBean(String lessTitle, String lessTitleUrl) {
            this.lessTitle = lessTitle;
            this.lessTitleUrl = lessTitleUrl;
        }

        public String getLessTitle() {
            return lessTitle;
        }

        public void setLessTitle(String lessTitle) {
            this.lessTitle = lessTitle;
        }

        public String getLessTitleUrl() {
            return lessTitleUrl;
        }

        public void setLessTitleUrl(String lessTitleUrl) {
            this.lessTitleUrl = lessTitleUrl;
        }

        @Override
        public String toString() {
            return lessTitle + "   " + lessTitleUrl;
        }
    }
}
