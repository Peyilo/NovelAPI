package org.anvei.novel.download.beans;

import org.anvei.novel.NovelSource;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示小说基本信息，考虑到某些小说网站的小说具有分卷
 */
public class Novel {

    public String author;                                       // 作者名

    public String title;                                        // 小说名

    public long novelId;

    public String url;

    public List<Volume> volumeList = new ArrayList<>();         // 卷列表

    public final boolean hasMultiVolume;                   // 该网站小说是否具有分卷

    public final NovelSource novelSource;     // 获取小说网站表示

    public Novel(boolean hasMultiVolume, NovelSource novelSource) {
        this.hasMultiVolume = hasMultiVolume;
        this.novelSource = novelSource;
    }
}
