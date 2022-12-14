package org.anvei.novel;

import org.anvei.novel.download.DownloadParams;
import org.anvei.novel.download.DownloadTask;
import org.anvei.novel.download.DownloadTasks;
import org.anvei.novel.api.SfacgAPI;
import org.anvei.novel.api.sfacg.ChapContent;
import org.anvei.novel.api.sfacg.ChapList;
import static org.anvei.novel.api.sfacg.ChapList.*;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.anvei.novel.api.SfacgAPI.getChapList;

public class TestSfacgAPI {

    @Test
    public void test1() throws IOException {
        ChapList novelDirs = getChapList(591785);
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
        ChapContent chapContent = SfacgAPI.getChapContent(6742076);
        System.out.println(chapContent.getContent());
        System.out.println("httpCode = " + chapContent.getHttpCode());
        System.out.println("charCount = " + chapContent.getCharCount());
        System.out.println("chapOrder = " + chapContent.getChapOrder());
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
}
