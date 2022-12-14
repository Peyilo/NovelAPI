package org.anvei.novel.download;

import org.anvei.novel.NovelSource;

public class DownloadTasks {

    public static DownloadTask getDownloadTask(NovelSource novelSource) {
        switch (novelSource) {
            case Sfacg:
                return new SfacgDownloadTask();
            case Hbooker:
                return new HbookerDownloadTask();
            default:
                throw new IllegalArgumentException("暂时不支持该种类型的下载任务!");
        }
    }

}
