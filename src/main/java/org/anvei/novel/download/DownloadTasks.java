package org.anvei.novel.download;

import org.anvei.novel.NovelSource;

public class DownloadTasks {

    public static DownloadTask getDownloadTask(NovelSource novelSource) {
        switch (novelSource) {
            case SfacgAPP:
                return new SfacgTask();
            case HbookerAPP:
                return new HbookerTask();
            case FanqieWebsite:
                return new FanqieWebsiteTask();
            case BiqegeWebsite:
                return new BiqegeWebsiteTask();
            case _143xsWebsite:
                return new _147xsWebsiteTask();
            default:
                throw new IllegalArgumentException("暂时不支持该种类型的下载任务!");
        }
    }

}
