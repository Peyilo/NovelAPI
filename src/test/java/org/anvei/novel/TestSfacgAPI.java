package org.anvei.novel;

import org.anvei.novel.download.DownloadParams;
import org.anvei.novel.download.DownloadTask;
import org.anvei.novel.download.DownloadTasks;
import org.anvei.novel.sfacg.SfacgAPI;
import org.anvei.novel.sfacg.gson.ChapContent;
import org.anvei.novel.sfacg.gson.ChapList;
import static org.anvei.novel.sfacg.gson.ChapList.*;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.anvei.novel.sfacg.SfacgAPI.getChapList;

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
        DownloadTask downloadTask = DownloadTasks.getDownloadTask(SourceIdentifier.Sfacg);
        DownloadParams params = new DownloadParams();
        params.parent = new File("E:\\Text File\\Novel");
        params.novelId = 233718;
        params.fileName = "咸鱼少女拒绝翻身.txt";
        long start = System.currentTimeMillis();
        downloadTask.startDownload(params);
        while (!downloadTask.isFinish()) {
        }
        System.out.println("status = " + downloadTask.getStatusCode());
        System.out.println((System.currentTimeMillis() - start) / 1000f + "s");
    }

    @Test
    public void test4() {
        DownloadTask downloadTask = DownloadTasks.getDownloadTask(SourceIdentifier.Sfacg);
        DownloadParams params = new DownloadParams();
        params.parent = new File("E:\\Text File\\Novel");
        params.novelId = 233718;
        params.fileName = "咸鱼少女拒绝翻身.txt";
        params.threadOn = true;
        long start = System.currentTimeMillis();
        downloadTask.startDownload(params);
        while (!downloadTask.isFinish()) {
        }
        System.out.println("status = " + downloadTask.getStatusCode());
        System.out.println((System.currentTimeMillis() - start) / 1000f + "s");
    }
}
