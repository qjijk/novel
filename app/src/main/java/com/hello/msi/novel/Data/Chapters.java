package com.hello.msi.novel.Data;

/**
 * Created by msi on 2018/3/15.
 * 章节信息
 */

public class Chapters {
    private String chapterNum;//章节号
    private String chapterName;//章节名
    private String chapterHref;//章节对应连接


    public Chapters (String chapterNum, String chapterName, String chapterHref)
    {
        this.chapterNum = chapterNum;
        this.chapterName = chapterName;
        this.chapterHref = chapterHref;
    }

    public String getChapterNum()
    {
        return chapterNum;
    }
    public String getChapterName()
    {
        return chapterName;
    }
    public String getChapterHref()
    {
        return chapterHref;
    }


}
