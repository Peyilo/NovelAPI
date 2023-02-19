package org.anvei.novel.download;

import org.anvei.novel.download.beans.Chapter;
import org.anvei.novel.download.beans.Novel;
import org.anvei.novel.download.beans.Volume;
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
    private int allTaskCount;

    // 初始化操作
    private void initTask() {
        if (subPool != null) {
            subPool.shutdown();
            subPool = null;
        }
        downloadStatus = Status.DOWNLOADING;
        charCount = 0;
        chapterCount = 0;
        targetFile = null;
        taskFinishedCount = new AtomicInteger(0);
        allTaskCount = 0;
        // 根据参数创建线程池
        if (downloadParams.maxThreadCount > 1) {
            subPool = Executors.newFixedThreadPool(downloadParams.maxThreadCount);
        } else {
            subPool = Executors.newSingleThreadExecutor();
        }
    }

    /**
     * 开始下载
     */
    public void startDownload() {
        initTask();
        mainPool.submit(() -> {
            try {
                boolean needRename = false;
                Novel novel;
                if (downloadParams.novelId != -1) {
                    novel = getNovel(downloadParams.novelId);
                } else {
                    novel = select(downloadParams.keyword);
                }
                // 如果novel还是为null，就表示下载目标确认失败
                if (novel == null) {
                    subPool = null;
                    downloadStatus = Status.FAILED;
                    return;
                }
                if (downloadParams.fileName == null) {                      // 如果没配置文件名，就以novelId的md5值作为文件名
                    downloadParams.fileName = SecurityUtils.getMD5Str(downloadParams.novelId + ".txt");
                    needRename = true;
                } else if (!downloadParams.fileName.contains(".")) {        // 如果没加后缀，就添加上后缀.txt
                    downloadParams.fileName += ".txt";
                }
                File file = FileUtils.createFile(downloadParams.parent, downloadParams.fileName);
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                // 多线程请求章节内容
                for (Volume volume : novel.volumeList) {
                    for (Chapter chapter : volume.chapterList) {
                        allTaskCount++;
                        subPool.submit(() -> {
                            // 如果chapter的url属性不为null，就通过url获取章节内容，反之通过id获取
                            if (chapter.url != null) {
                                chapter.content = getChapterContent(chapter.url);
                            } else {
                                chapter.content = getChapterContent(downloadParams.novelId, chapter.chapId);
                            }
                            taskFinishedCount.incrementAndGet();
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
                // 处理文件重命名
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
        startTimeTask(timeout);
    }

    // 开启一个计时子线程
    private void startTimeTask(long timeout) {
        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            // 等待任务结束或者超时
            while (isUnFinish() && (System.currentTimeMillis() - startTime < timeout));
            // 如果超时后，任务还未完成，就结束任务，并做一些清理工作
            if (isUnFinish()) {
                subPool.shutdown();
                subPool = null;
                mainPool.shutdown();
                downloadStatus = Status.FAILED;
            }
        }).start();
    }

    // 在该函数内保存章节、分卷信息，别在该函数内请求章节内容
    protected abstract Novel getNovel(long novelId);

    // 根据一个关键词，获取Novel对象，并且将根据该Novel对象下载选中的小说
    protected abstract Novel select(String keyword);

    // 请求章节内容
    protected String getChapterContent(long novelId, long chapId) {
        throw new IllegalStateException("请重写该方法!");
    }

    protected String getChapterContent(String url) {
        throw new IllegalStateException("请重写该方法!");
    }

    // 停止下载任务
    public void stop() {
        if (downloadStatus != Status.SUCCESS && downloadStatus != Status.UNINITIALIZED) {
            downloadStatus = Status.STOP;
        }
    }

    // 任务是否已经结束，如果没有结束就返回true
    public boolean isUnFinish() {
        return downloadStatus != Status.FAILED &&
                downloadStatus != Status.SUCCESS &&
                downloadStatus != Status.STOP;
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
        while (isUnFinish());
        return downloadStatus == Status.SUCCESS;
    }

    public boolean waitFinished(Runnable runnable) {
        while (isUnFinish()) {
            runnable.run();
        }
        return downloadStatus == Status.SUCCESS;
    }

    // 获取任务已下载字数
    public int getCharCount() {
        return charCount;
    }

    // 获取下载任务的小说章节数
    public int getChapterCount() {
        return chapterCount;
    }

    /**
     * 获取最终下载位置
     */
    public File getTargetFile() {
        return targetFile;
    }

    // 获取当前下载进度
    public int getDownloadProcess() {
        if (allTaskCount == 0)
            return 0;
        return taskFinishedCount.intValue() * 100 / allTaskCount;
    }
}
