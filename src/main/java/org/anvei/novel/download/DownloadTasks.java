package org.anvei.novel.download;

import org.anvei.novel.NovelSource;

public class DownloadTasks {

    public static DownloadTask getDownloadTask(NovelSource novelSource) {
        switch (novelSource) {
            case SfacgAPP:
                return new SfacgDownloadTask();
            case HbookerAPP:
                return new HbookerDownloadTask();
            case FanqieWebsite:
                return new FanqieWebsiteDownloadTask();
            case _143xsWebsite:
                return new _147xsWebsiteDownloadTask();
            case BiqumuWebsite:
                return new BiqumuWebsiteDownloadTask();
            default:
                throw new IllegalArgumentException("暂时不支持该种类型的下载任务!");
        }
    }

}
