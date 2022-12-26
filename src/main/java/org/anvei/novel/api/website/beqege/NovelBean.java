package org.anvei.novel.api.website.beqege;

import java.util.ArrayList;
import java.util.List;

public class NovelBean {

    public String novelName;
    public String author;

    public String coverUrl;         // 封面地址
    public String intro;
    public String lastUpdateTime;
    public ChapterBean lastChapter;

    public final List<ChapterBean> chapterList = new ArrayList<>();
}
