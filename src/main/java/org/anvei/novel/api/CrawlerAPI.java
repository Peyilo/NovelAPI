package org.anvei.novel.api;

import org.anvei.novel.NovelSource;

/**
 * 爬虫基本接口，提供了唯一一个方法{@link #getNovelSource()},用来标识API的小说来源
 */
public interface CrawlerAPI {

    /**
     * @return 返回API的小说来源
     */
    NovelSource getNovelSource();

}
