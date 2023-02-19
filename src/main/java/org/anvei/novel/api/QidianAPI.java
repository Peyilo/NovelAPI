package org.anvei.novel.api;

import org.anvei.novel.NovelSource;

public class QidianAPI implements CrawlerAPI {

    @Override
    public NovelSource getNovelSource() {
        return NovelSource.Qidian;
    }

}
