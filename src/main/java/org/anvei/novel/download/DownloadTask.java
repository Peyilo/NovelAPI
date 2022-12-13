package org.anvei.novel.download;

import org.anvei.novel.beans.Chapter;
import org.anvei.novel.beans.Novel;
import org.anvei.novel.beans.Volume;
import org.anvei.novel.utils.FileUtils;
import org.anvei.novel.utils.Security;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class DownloadTask {

    public static final int DOWNLOAD_UNINITIALIZED = 0x00;              // 未初始化

    public static final int DOWNLOAD_FAILED = 0x01;                     // 下载失败

    public static final int DOWNLOAD_SUCCESS = 0x02;                    // 下载成功

    public static final int DOWNLOAD_PAUSE = 0x03;                      // 下载暂停

    public static final int DOWNLOAD_STOP = 0x04;                       // 下载停止

    public static final int DOWNLOADING = 0x05;                         // 下载中

    protected volatile int downloadStatus = DOWNLOAD_UNINITIALIZED;

    /**
     * 在内部开启一个子线程开始下载
     */
    private Thread task;

    public void startDownload(DownloadParams params) {
        if (task != null) {
            throw new IllegalStateException("正常情况下，该task应该为null。" +
                    "只有当上个任务还未执行完成或者未调用stop()就再次就调用startDownload()，才会抛出该异常.");
        }
        downloadStatus = DOWNLOADING;
        task = new Thread(() -> {
            try {
                Novel novel = getNovel(params.novelId);
                if (params.fileName == null) {
                    params.fileName = Security.getMD5Str(params.novelId + "");
                }
                File file = FileUtils.createFile(params.parent, params.fileName);
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                if (isFinish()) {
                    task = null;
                    return;
                }
                while (downloadStatus == DOWNLOAD_PAUSE) {
                }
                if (params.threadOn) {
                    // 多线程请求章节内容
                    for (Volume volume : novel.volumeList) {
                        for (Chapter chapter : volume.chapterList) {
                            new Thread(() -> {
                                chapter.content = getChapterContent(params.novelId, chapter.chapId);
                            }).start();
                        }
                    }
                }
                for (Volume volume : novel.volumeList) {
                    String volumeTitle = volume.title;
                    for (Chapter chapter : volume.chapterList) {
                        if (!params.threadOn) {
                            chapter.content = getChapterContent(params.novelId, chapter.chapId);
                        }
                        if (isFinish()) {
                            task = null;
                            return;
                        }
                        while (downloadStatus == DOWNLOAD_PAUSE || (params.threadOn
                                && chapter.content == null)) {
                        }
                        // 在这里完成数据的写入
                        writeChapTitle(writer, volumeTitle, chapter.title);
                        writeChapContent(writer, chapter.content);
                    }
                }

                downloadStatus = DOWNLOAD_SUCCESS;
            } catch (IOException e) {
                downloadStatus = DOWNLOAD_FAILED;
                e.printStackTrace();
            }
            task = null;
        });
        // 计时部分
        if (params.outTime > 0) {
            long start = System.currentTimeMillis();
            new Thread(() -> {
                long time;
                long outTime = params.outTime;
                while (true) {
                    time = System.currentTimeMillis() - start;
                    if (time > outTime) {
                        downloadStatus = DOWNLOAD_FAILED;
                        break;
                    }
                    if (!isFinish()) {
                        break;
                    }
                }

            }).start();
        }
        // 执行任务
        task.start();
    }

    public abstract Novel getNovel(long novelId);

    public abstract String getChapterContent(long novelId, long chapId);

    public void pause() {
        if (downloadStatus == DOWNLOADING) {
            downloadStatus = DOWNLOAD_PAUSE;
        }
    }

    public void restart() {
        if (downloadStatus == DOWNLOAD_PAUSE) {
            downloadStatus = DOWNLOADING;
        }
    }

    public void stop() {
        if (downloadStatus != DOWNLOAD_SUCCESS && downloadStatus != DOWNLOAD_UNINITIALIZED) {
            downloadStatus = DOWNLOAD_STOP;
        }
    }

    public boolean isFinish() {
        return downloadStatus == DOWNLOAD_FAILED ||
                downloadStatus == DOWNLOAD_SUCCESS ||
                downloadStatus == DOWNLOAD_STOP;
    }

    public int getStatusCode() {
        return downloadStatus;
    }

    protected void writeChapTitle(BufferedWriter writer, String volumeTitle, String chapTitle) throws IOException {
        writer.write("\n" + volumeTitle + " " + chapTitle + "\n");
    }

    protected void writeChapContent(BufferedWriter writer, String chapContent) throws IOException {
        writer.write("\t" + chapContent.strip() + "\n");
    }
}
