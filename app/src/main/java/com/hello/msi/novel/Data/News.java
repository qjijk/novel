package com.hello.msi.novel.Data;

/**
 * Created by msi on 2018/3/15.
 * 小说信息
 */

public class News {
    private String newsTitle;//小说英文名
    private String newsUrl;//小说对应链接
    private String newsCname;//中文名
    private String newId;//小说ID

    public News(String newsTitle, String newsUrl, String newsTime, String newId) {
        this.newsTitle = newsTitle;
        this.newsUrl = newsUrl;
        this.newsCname = newsTime;
        this.newId = newId;
    }



    public String getNewId() {
        return newId;
    }

    public String getNewsTime() {
        return newsCname;
    }

    public void setNewsTime(String newsTime) {
        this.newsCname = newsTime;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }
}
