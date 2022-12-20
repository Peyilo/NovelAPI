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

    private Thread task;

    private int charCount = 0;

    private int chapterCount = 0;

    private File targetFile;                // 最终下载目标文件，内容保存在该文件下

    protected DownloadParams downloadParams;

    public DownloadTask() {
    }

    public DownloadTask(DownloadParams downloadParams) {
        this.downloadParams = downloadParams;
    }

    public void startDownload(DownloadParams downloadParams) {
        this.downloadParams = downloadParams;
        startDownload();
    }

    /**
     * 在内部开启一个子线程开始下载
     * TODO: 该函数内部耦合度过高，逻辑过于复杂，待简化
     */
    public void startDownload() {
        if (task != null) {
            throw new IllegalStateException("正常情况下，该task应该为null。" +
                    "只有当上个任务还未执行完成或者未调用stop()就再次就调用startDownload()，才会抛出该异常.");
        }
        downloadStatus = Status.DOWNLOADING;
        charCount = 0;
        chapterCount = 0;
        targetFile = null;
        task = new Thread(() -> {
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
                if (isFinish()) {
                    task = null;
                    return;
                }
                while (downloadStatus == Status.PAUSE);
                if (downloadParams.multiThreadOn) {
                    // 多线程请求章节内容
                    for (Volume volume : novel.volumeList) {
                        for (Chapter chapter : volume.chapterList) {
                            new Thread(() -> {
                                chapter.content = getChapterContent(downloadParams.novelId, chapter.chapId);
                            }).start();
                        }
                    }
                }
                for (Volume volume : novel.volumeList) {
                    String volumeTitle = volume.title;
                    for (Chapter chapter : volume.chapterList) {
                        if (!downloadParams.multiThreadOn) {
                            chapter.content = getChapterContent(downloadParams.novelId, chapter.chapId);
                        }
                        if (isFinish()) {
                            task = null;
                            return;
                        }
                        while (downloadStatus == Status.PAUSE || (downloadParams.multiThreadOn
                                && chapter.content == null));
                        // 在这里完成数据的写入
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
                if (needRename) {
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
            task = null;
        });
        // 计时部分
        if (downloadParams.timeout > 0) {
            long start = System.currentTimeMillis();
            new Thread(() -> {
                long time;
                long outTime = downloadParams.timeout;
                while (true) {
                    time = System.currentTimeMillis() - start;
                    if (time > outTime) {
                        downloadStatus = Status.FAILED;
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

    // 在该函数内保存章节、分卷信息，别在该函数内请求章节内容
    public abstract Novel getNovel(long novelId);

    // 请求章节内容
    public abstract String getChapterContent(long novelId, long chapId);

    public void pause() {
        if (downloadStatus == Status.DOWNLOADING) {
            downloadStatus = Status.PAUSE;
        }
    }

    public void restart() {
        if (downloadStatus == Status.PAUSE) {
            downloadStatus = Status.DOWNLOADING;
        }
    }

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
        writer.write("\n" + volumeTitle + " " + chapTitle + "\n");
    }

    protected void writeChapContent(BufferedWriter writer, String chapContent) throws IOException {
        if (chapContent == null) {
            chapContent = "";
        }
        writer.write(chapContent + "\n");
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
