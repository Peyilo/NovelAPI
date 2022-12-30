package org.anvei.novel.download;

import org.anvei.novel.beans.Chapter;
import org.anvei.novel.beans.Novel;
import org.anvei.novel.beans.Volume;
import org.anvei.novel.utils.FileUtils;
import org.anvei.novel.utils.SecurityUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class DownloadTask {

    public enum Status {
        UNINITIALIZED,
        FAILED,
        SUCCESS,
        PAUSE,
        STOP,
        DOWNLOADING
    }

    protected volatile Status downloadStatus = Status.UNINITIALIZED;

    private int charCount = 0;

    private int chapterCount = 0;

    private File targetFile;                // 最终下载目标文件，内容保存在该文件下

    protected DownloadParams downloadParams;

    private static final long DEFAULT_TIMEOUT = 30000;

    public DownloadTask() {
    }

    public DownloadTask(DownloadParams downloadParams) {
        this.downloadParams = downloadParams;
    }

    public void startDownload(DownloadParams downloadParams) {
        this.downloadParams = downloadParams;
        startDownload();
    }

    protected ExecutorService mainPool = Executors.newSingleThreadExecutor();
    protected ExecutorService subPool;

    private volatile AtomicInteger taskFinishedCount;

    /**
     * 开始下载
     */
    public void startDownload() {
        downloadStatus = Status.DOWNLOADING;
        charCount = 0;
        chapterCount = 0;
        targetFile = null;
        taskFinishedCount = new AtomicInteger(0);
        // 根据参数创建线程池
        if (downloadParams.maxThreadCount > 1) {
            subPool = Executors.newFixedThreadPool(downloadParams.maxThreadCount);
        } else {
            subPool = Executors.newSingleThreadExecutor();
        }
        Future<?> mainTask = mainPool.submit(() -> {
            try {
                boolean needRename = false;
                Novel novel = getNovel(downloadParams.novelId);
                if (downloadParams.fileName == null) {                      // 如果没配置文件名，就以novelId的md5值作为文件名
                    downloadParams.fileName = SecurityUtils.getMD5Str(downloadParams.novelId + ".txt");
                    needRename = true;
                } else if (!downloadParams.fileName.contains(".")) {        // 如果没加后缀，就添加上后缀.txt
                    downloadParams.fileName += ".txt";
                }
                File file = FileUtils.createFile(downloadParams.parent, downloadParams.fileName);
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                // 多线程请求章节内容
                int allTaskCount = 0;
                for (Volume volume : novel.volumeList) {
                    for (Chapter chapter : volume.chapterList) {
                        allTaskCount++;
                        subPool.submit(() -> {
                            chapter.content = getChapterContent(downloadParams.novelId, chapter.chapId);
                            taskFinishedCount.addAndGet(1);
                        });
                    }
                }
                // 等待线程池任务全部执行完成
                while (taskFinishedCount.get() < allTaskCount - 1) {
                    if (downloadStatus == Status.STOP) {
                        subPool.shutdown();
                        subPool = null;
                        return;
                    }
                }
                // 文件流写入
                for (Volume volume : novel.volumeList) {
                    String volumeTitle = volume.title;
                    for (Chapter chapter : volume.chapterList) {
                        writeChapTitle(writer, volumeTitle, chapter.title);
                        writeChapContent(writer, chapter.content);
                        if (volumeTitle != null) {
                            charCount += volumeTitle.length();
                        }
                        if (chapter.title != null) {
                            charCount += chapter.title.length();
                        }
                        if (chapter.content != null) {
                            charCount += chapter.content.length();
                        }
                        chapterCount++;
                    }
                }
                writer.close();
                if (needRename && (novel.author != null || novel.title != null)) {
                    downloadParams.fileName = novel.title + " " + novel.author;
                    File rename;
                    if (downloadParams.parent != null) {
                        rename = new File(downloadParams.parent, downloadParams.fileName + ".txt");
                    } else {
                        rename = new File(downloadParams.fileName + ".txt");
                    }
                    targetFile = FileUtils.getFile(rename);
                    file.renameTo(targetFile);
                } else {
                    targetFile = file;
                }
                downloadStatus = Status.SUCCESS;
            } catch (IOException e) {
                downloadStatus = Status.FAILED;
                e.printStackTrace();
            }
            subPool = null;
        });
        long timeout;
        if (downloadParams.timeout > 0) {
            timeout = downloadParams.timeout;
        } else {
            timeout = DEFAULT_TIMEOUT;
        }

    }

    // 在该函数内保存章节、分卷信息，别在该函数内请求章节内容
    public abstract Novel getNovel(long novelId);

    // 请求章节内容
    public abstract String getChapterContent(long novelId, long chapId);

    // 停止下载任务
    public void stop() {
        if (downloadStatus != Status.SUCCESS && downloadStatus != Status.UNINITIALIZED) {
            downloadStatus = Status.STOP;
        }
    }

    public boolean isFinish() {
        return downloadStatus == Status.FAILED ||
                downloadStatus == Status.SUCCESS ||
                downloadStatus == Status.STOP;
    }

    public Status getStatusMsg() {
        return downloadStatus;
    }

    protected void writeChapTitle(BufferedWriter writer, String volumeTitle, String chapTitle) throws IOException {
        if (volumeTitle == null) {
            volumeTitle = "";
        }
        volumeTitle += " ";
        writer.write(volumeTitle + chapTitle + "\n");
    }

    protected void writeChapContent(BufferedWriter writer, String chapContent) throws IOException {
        if (chapContent == null) {
            chapContent = "";
        }
        writer.newLine();
        writer.write(chapContent);
        writer.newLine();
    }

    /**
     * 等待下载任务结束，下载成功返回true，否则返回false
     */
    public boolean waitFinished() {
        while (!isFinish());
        return downloadStatus == Status.SUCCESS;
    }

    public int getCharCount() {
        return charCount;
    }

    public int getChapterCount() {
        return chapterCount;
    }

    /**
     * 获取最终下载位置
     */
    public File getTargetFile() {
        return targetFile;
    }
}
