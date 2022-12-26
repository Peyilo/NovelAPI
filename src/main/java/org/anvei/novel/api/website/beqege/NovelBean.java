package org.anvei.novel.api.website.beqege;

import java.util.ArrayList;
import java.util.List;

public class NovelBean {

    public String novelName;
    public String author;

    public String coverUrl;         // 封面地址
    public String intro;            // 简介
    public String lastUpdateTime;   // 最后更新时间
    public ChapterBean lastChapter; // 最新章节

    public final List<ChapterBean> chapterList = new ArrayList<>();
}
