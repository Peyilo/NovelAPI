package org.anvei.novel.download;

import org.anvei.novel.NovelSource;
import org.anvei.novel.beans.Chapter;
import org.anvei.novel.beans.Novel;
import org.anvei.novel.beans.Volume;
import org.anvei.novel.api.SfacgAPI;
import org.anvei.novel.api.sfacg.ChapListJson;

import java.io.IOException;

public class SfacgDownloadTask extends DownloadTask {

    @Override
    public Novel getNovel(long novelId) {
        Novel novel = new Novel() {
            @Override
            public boolean hasMultiVolume() {
                return true;
            }

            @Override
            public NovelSource getNovelSource() {
                return NovelSource.SfacgAPP;
            }
        };
        try {
            ChapListJson chapListJson = new SfacgAPI().getChapListJson(novelId);
            for (int i = 0; i < chapListJson.getVolumeList().size(); i++) {
                Volume v1 = new Volume();
                ChapListJson.Volume v2 = chapListJson.getVolumeList().get(i);
                v1.volumeOrder = i + 1;
                v1.title = v2.title;
                for (ChapListJson.Chapter chapter : v2.chapterList) {
                    Chapter c = new Chapter();
                    c.title = chapter.title;
                    c.chapOrder = chapter.chapOrder;
                    c.charCount = chapter.charCount;
                    c.chapId = chapter.chapId;
                    v1.chapterList.add(c);
                }
                novel.volumeList.add(v1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return novel;
    }

    @Override
    public String getChapterContent(long novelId, long chapId) {
        String s = null;
        try {
            s = new SfacgAPI().getChapContentJson(chapId).getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }
}
