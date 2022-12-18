package org.anvei.novel;

import org.anvei.novel.api.SfacgAPI;
import org.anvei.novel.api.sfacg.AccountJson;
import org.anvei.novel.api.sfacg.ChapContentJson;
import org.anvei.novel.api.sfacg.ChapListJson;
import org.anvei.novel.api.sfacg.SearchResultJson;
import org.anvei.novel.download.DownloadParams;
import org.anvei.novel.download.DownloadTask;
import org.anvei.novel.download.DownloadTasks;
import org.anvei.novel.utils.FileUtils;
import org.anvei.novel.utils.TextUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.anvei.novel.api.sfacg.ChapListJson.Chapter;
import static org.anvei.novel.api.sfacg.ChapListJson.Volume;

public class TestSfacgAPI {

    private String username;
    private String password;

    @Before
    public void init() throws IOException {
        Config.initAppConfig();
        try {
            username = Config.getSfacgConfig(Config.KEY_SFACG_USERNAME);
            password = Config.getSfacgConfig(Config.KEY_SFACG_PASSWORD);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void test1() throws IOException {
        ChapListJson novelDirs = new SfacgAPI().getChapListJson(591785);
        List<Volume> volumeList = novelDirs.getVolumeList();
        for (Volume volume : volumeList) {
            List<Chapter> chapterList = volume.chapterList;
            System.out.println(volume.title);
            for (Chapter chapter : chapterList) {
                System.out.println("\t" + chapter.title);
            }
        }
    }


    @Test
    public void test2() throws IOException {
        ChapContentJson chapContentJson = new SfacgAPI().getChapContentJson(6742076);
        System.out.println(chapContentJson.getContent());
        System.out.println("httpCode = " + chapContentJson.getHttpCode());
        System.out.println("charCount = " + chapContentJson.getCharCount());
        System.out.println("chapOrder = " + chapContentJson.getChapOrder());
    }

    @Test
    public void test3() {
        DownloadTask downloadTask = DownloadTasks.getDownloadTask(NovelSource.Sfacg);
        DownloadParams params = new DownloadParams();
        params.parent = new File("E:\\Text File\\Novel");
        params.novelId = 233718;
        params.fileName = "咸鱼少女拒绝翻身.txt";
        long start = System.currentTimeMillis();
        downloadTask.startDownload(params);
        boolean res = downloadTask.waitFinished();
        System.out.println("download success: " + res);
        System.out.println((System.currentTimeMillis() - start) / 1000f + "s");
    }

    @Test
    public void test4() {
        DownloadTask downloadTask = DownloadTasks.getDownloadTask(NovelSource.Sfacg);
        DownloadParams params = new DownloadParams();
        params.parent = new File("E:\\Text File\\Novel");
        params.novelId = 233718;
        params.fileName = "咸鱼少女拒绝翻身.txt";
        params.multiThreadOn = true;
        long start = System.currentTimeMillis();
        downloadTask.startDownload(params);
        boolean res = downloadTask.waitFinished();
        System.out.println("download success: " + res);
        System.out.println("status message: " + downloadTask.getStatusMsg());
        System.out.println((System.currentTimeMillis() - start) / 1000f + "s");
    }

    @Test
    public void test5() throws IOException {
        SfacgAPI api = new SfacgAPI(username, password);
        boolean loginRes = api.loginMini();
        System.out.println("loginRes: " + loginRes);
        System.out.println(api.getCookies());
        AccountJson info = api.getAccountJson();
        System.out.println(TextUtils.toPrettyFormat(info));
    }

    @Test
    public void test6() throws IOException {
        SfacgAPI api = new SfacgAPI(username, password);
        boolean loginRes = api.loginMini();
        if (loginRes) {
            ChapListJson chapListJson = api.getChapListJson(591785);
            List<Volume> volumeList = chapListJson.getVolumeList();
            List<Chapter> chapterList = volumeList.get(volumeList.size() - 1).chapterList;
            Chapter chapter = chapterList.get(chapterList.size() - 1);
            System.out.println("isVip: " + chapter.isVip);
            File parent = new File("E:\\Text File\\Novel\\pic");
            File file = FileUtils.createFile(parent, "content.jpg");
            boolean saveRes = api.saveVipChapPic(chapter.novelId,
                    chapter.chapId,
                    file);
            System.out.println("saveRes: " + saveRes);
        } else {
            System.out.println("Login failed!");
        }
    }

    @Test
    public void test7() throws IOException {
        SfacgAPI api = new SfacgAPI();
        SearchResultJson searchResultJson = api.search("来自深渊", 0, 20);
        System.out.println(TextUtils.toPrettyFormat(searchResultJson));
        System.out.println("Search result count: " + searchResultJson.data.novels.size());
    }

    @Test
    public void test8() {
        boolean res = new SfacgAPI(username, password).loginMini();
        System.out.println(res);
    }
}
