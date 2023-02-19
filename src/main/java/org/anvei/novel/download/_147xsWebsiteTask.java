package org.anvei.novel.download;

import org.anvei.novel.NovelSource;
import org.anvei.novel.api.exceptions.ConnectionException;
import org.anvei.novel.api.website._147xs.SearchResultBean;
import org.anvei.novel.api.website._147xsWebsiteAPI;
import org.anvei.novel.api.website.common.ChapterBean;
import org.anvei.novel.api.website.common.NovelBean;
import org.anvei.novel.download.beans.Chapter;
import org.anvei.novel.download.beans.Novel;
import org.anvei.novel.download.beans.Volume;

import java.io.IOException;
import java.util.List;

@Deprecated
public class _147xsWebsiteTask extends DownloadTask {

    private static class Holder {
        public static _147xsWebsiteAPI api = new _147xsWebsiteAPI();
    }

    private _147xsWebsiteAPI getApi() {
        return Holder.api;
    }

    @Override
    protected Novel getNovel(long novelId) {
        try {
            NovelBean v1 = getApi().getNovel(novelId);
            Novel novel = new Novel(false, NovelSource._143xsWebsite);
            Volume volume = new Volume();
            novel.novelId = novelId;
            novel.author = v1.author;
            novel.title = v1.novelName;
            novel.url = v1.url;
            for (ChapterBean v2 : v1.chapterList) {
                Chapter chapter = new Chapter();
                chapter.title = v2.chapterName;
                chapter.url = v2.url;
                volume.chapterList.add(chapter);
            }
            novel.volumeList.add(volume);
            return novel;
        } catch (IOException | ConnectionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Novel select(String keyword) {
        try {
            List<SearchResultBean> searchResultBeans = getApi().search(keyword);
            if (searchResultBeans.size() != 0) {
                Novel novel = new Novel(false, NovelSource._143xsWebsite);
                // 没有多分卷，即只有一卷
                Volume volume = new Volume();
                // 将搜索结果的第一个作为下载目标
                SearchResultBean v1 = searchResultBeans.get(0);
                novel.title = v1.novelName;
                novel.author = v1.author;
                novel.url = v1.url;
                NovelBean v2 = getApi().getNovel(v1);
                for (ChapterBean v3 : v2.chapterList) {
                    Chapter chapter = new Chapter();
                    chapter.url = v3.url;
                    chapter.title = v3.chapterName;
                    volume.chapterList.add(chapter);
                }
                novel.volumeList.add(volume);
                return novel;
            }
        } catch (IOException | ConnectionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected String getChapterContent(String url) {
        try {
            return getApi().getChapContent(url);
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
