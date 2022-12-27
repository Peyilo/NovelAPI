package org.anvei.novel.website;

import org.anvei.novel.api.website.FanqieWebsiteAPI;
import org.anvei.novel.api.website.common.ChapterBean;
import org.anvei.novel.api.website.fanqie.NovelBean;
import org.anvei.novel.api.website.fanqie.SearchResultJson;
import org.anvei.novel.utils.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class TestFanqie {

    @Test
    public void test() throws IOException {
        FanqieWebsiteAPI api = new FanqieWebsiteAPI();
        SearchResultJson searchResultJson = api.search("萝莉");
        SearchResultJson.BookData bookData = searchResultJson.data.bookDataList.get(1);
        File file = new File("D:\\Programming\\Temp\\1.jpg");
        if (!file.exists()) {
            file.createNewFile();
        }
        boolean res = FileUtils.writeFile(file, api.requestImage(bookData.coverUrl));
        if (res) {
            System.out.println("保存图片成功!");
            System.out.println("文件位置: " + file.getAbsolutePath());
        }
    }

    @Test
    public void test1() throws IOException {
        FanqieWebsiteAPI api = new FanqieWebsiteAPI();
        NovelBean novel = api.getNovel("7090069368691756064");
        ChapterBean chapContent = api.getChapContent(novel.volumeList.get(1).chapterBeanList.get(0));
        System.out.println(chapContent.content);
    }
}
