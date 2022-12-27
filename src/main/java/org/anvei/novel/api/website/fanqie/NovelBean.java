package org.anvei.novel.api.website.fanqie;

import org.anvei.novel.api.website.common.ChapterBean;

import java.util.ArrayList;
import java.util.List;

public class NovelBean {

    public String novelName;

    public String novelId;

    public String author;

    public String intro;

    public ChapterBean lastChapter;

    public final List<Volume> volumeList = new ArrayList<>();           // 小说分卷

    public static class Volume {
        public String volumeName;
        public final List<ChapterBean> chapterBeanList = new ArrayList<>();
    }

    public String coverUrl;

    public String lastUpdateTime;
}
