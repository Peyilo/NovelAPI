package org.anvei.novel.api.sfacg;

public class SearchParams {

    public static final int DEFAULT_SEARCH_SIZE = 12;
    public static final int DEFAULT_SEARCH_PAGE = 0;

    public final String   key;

    public int      page = DEFAULT_SEARCH_PAGE;
    public int      size = DEFAULT_SEARCH_SIZE;
    public Sort     sort = Sort.hot;
    public long     timeout = -1;

    public enum Sort {
        hot             // 按照热度排序
    }

    public SearchParams(String key) {
        this.key = key;
    }

}
