package org.anvei.novel.download;

import org.anvei.novel.NovelSource;
import org.anvei.novel.api.HbookerAPI;
import org.anvei.novel.api.hbooker.BookInfoJson;
import org.anvei.novel.api.hbooker.ChapListInfoJson;
import org.anvei.novel.api.hbooker.DivisionInfoJson;
import org.anvei.novel.api.hbooker.SearchResultJson;
import org.anvei.novel.download.beans.Chapter;
import org.anvei.novel.download.beans.Novel;
import org.anvei.novel.download.beans.Volume;

import java.io.IOException;
import java.util.List;

public class HbookerTask extends DownloadTask {

    private volatile HbookerAPI api;

    private synchronized HbookerAPI getApi() throws IOException {
        if (api == null) {
            synchronized (HbookerAPI.class) {
                if (api == null) {
                    if (downloadParams.api != null) {
                        api = (HbookerAPI) downloadParams.api;
                    } else {
                        api = new HbookerAPI(downloadParams.account, downloadParams.certificate);
                    }
                }
            }
        }
        return api;
    }

    @Override
    protected Novel getNovel(long novelId) {
        Novel novel = new Novel(true, NovelSource.HbookerAPP);
        novel.novelId = novelId;
        HbookerAPI api;
        try {
            api = getApi();
            BookInfoJson bookInfoJson = api.getBookInfoJson(novelId);
            novel.title = bookInfoJson.data.bookInfo.bookName;
            novel.author = bookInfoJson.data.bookInfo.authorName;
            getVolumeList(novel.volumeList, novelId);
            return novel;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 请求章节列表信息
    private void getVolumeList(List<Volume> volumeList, long novelId) {
        try {
            List<DivisionInfoJson.Division> divisionList = api.getDivisionInfoJson(novelId).data.divisionList;
            int index = 1;
            for (DivisionInfoJson.Division division : divisionList) {
                Volume volume = new Volume();
                volume.volumeOrder = index++;
                ChapListInfoJson chapterListInfoJson = api.getChapterListInfoJson(division.divisionId);
                for (ChapListInfoJson.Chapter c : chapterListInfoJson.data.chapterList) {
                    Chapter chapter = new Chapter();
                    chapter.chapId = c.chapterId;
                    chapter.charCount = c.wordCount;
                    chapter.chapOrder = c.chapterIndex;
                    chapter.title = c.chapterTitle;
                    volume.chapterList.add(chapter);
                }
                volumeList.add(volume);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected String getChapterContent(long novelId, long chapId) {
        try {
            return getApi().getChapterInfoJson(chapId).data.chapterInfo.txtContent;
        } catch (Exception e) {
            e.printStackTrace();;
            return "";
        }
    }

    @Override
    protected Novel select(String keyword) {
        try {
            SearchResultJson searchResultJson = getApi().search(keyword);
            List<SearchResultJson.Book> bookList = searchResultJson.data.bookList;
            if (bookList.size() != 0) {
                Novel novel = new Novel(true, NovelSource.HbookerAPP);
                SearchResultJson.Book book = bookList.get(0);
                novel.title = book.bookName;
                novel.author = book.authorName;
                novel.novelId = book.bookId;
                getVolumeList(novel.volumeList, novel.novelId);
                return novel;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
