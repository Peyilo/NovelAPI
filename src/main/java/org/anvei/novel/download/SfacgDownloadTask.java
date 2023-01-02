package org.anvei.novel.download;

import org.anvei.novel.NovelSource;
import org.anvei.novel.api.sfacg.NovelHomeJson;
import org.anvei.novel.api.sfacg.SearchResultJson;
import org.anvei.novel.download.beans.Chapter;
import org.anvei.novel.download.beans.Novel;
import org.anvei.novel.download.beans.Volume;
import org.anvei.novel.api.SfacgAPI;
import org.anvei.novel.api.sfacg.ChapListJson;

import java.io.IOException;
import java.util.List;

public class SfacgDownloadTask extends DownloadTask {

    private volatile SfacgAPI api;

    private SfacgAPI getApi() {
        if (api == null) {
            synchronized (SfacgAPI.class) {
                if (api == null) {
                    if (downloadParams.api != null) {
                        api = (SfacgAPI) downloadParams.api;
                    } else if (downloadParams.account != null && downloadParams.certificate != null) {
                        api = new SfacgAPI(downloadParams.account, downloadParams.certificate);
                        api.login();
                    } else {
                        api = new SfacgAPI();
                    }
                }
            }
        }
        return api;
    }

    @Override
    protected Novel getNovel(long novelId) {
        Novel novel = new Novel(true, NovelSource.SfacgAPP);
        novel.novelId = novelId;
        try {
            NovelHomeJson novelHomeJson = getApi().getNovelHomeJson(novelId);
            novel.title = novelHomeJson.data.novelName;
            novel.author = novelHomeJson.data.authorName;
            getVolumeList(novel.volumeList, novel.novelId);
            return novel;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void getVolumeList(List<Volume> volumeList, long novelId) {
        try {
            ChapListJson chapListJson = getApi().getChapListJson(novelId);
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
                volumeList.add(v1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getChapterContent(long novelId, long chapId) {
        String s = null;
        try {
            s = getApi().getChapContentJson(chapId).getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    @Override
    protected Novel select(String keyword) {
        try {
            SearchResultJson searchResultJson = getApi().search(keyword);
            List<SearchResultJson.Novel> novels = searchResultJson.data.novels;
            if (novels.size() != 0) {
                SearchResultJson.Novel target = novels.get(0);
                Novel novel = new Novel(true, NovelSource.SfacgAPP);
                novel.novelId = target.novelId;
                novel.author = target.authorName;
                novel.title = target.novelName;
                getVolumeList(novel.volumeList, novel.novelId);
                return novel;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
