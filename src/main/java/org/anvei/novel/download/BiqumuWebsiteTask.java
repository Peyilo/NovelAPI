package org.anvei.novel.download;

import org.anvei.novel.download.beans.Novel;

@Deprecated
public class BiqumuWebsiteTask extends DownloadTask {

    @Override
    public Novel getNovel(long novelId) {
        return null;
    }

    @Override
    public String getChapterContent(long novelId, long chapId) {
        return null;
    }

    @Override
    public Novel select(String keyword) {
        return null;
    }
}
