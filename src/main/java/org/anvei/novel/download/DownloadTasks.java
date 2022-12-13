package org.anvei.novel.download;

import org.anvei.novel.SourceIdentifier;

public class DownloadTasks {

    public static DownloadTask getDownloadTask(SourceIdentifier identifier) {
        switch (identifier) {
            case Sfacg:
                return new SfacgDownloadTask();
            case Ciweimao:
                return new CiweimaoDownloadTask();
            default:
                throw new IllegalArgumentException("暂时不支持该种类型的下载任务!");
        }
    }

}
