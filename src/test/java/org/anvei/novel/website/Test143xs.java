package org.anvei.novel.website;

import org.anvei.novel.api.website._147xs.SearchResultBean;
import org.anvei.novel.api.website._147xsAPI;
import org.anvei.novel.api.website.common.ChapterBean;
import org.anvei.novel.api.website.common.NovelBean;
import org.anvei.novel.utils.TextUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class Test143xs {

    @Test
    public void testSearch() throws IOException {
        _147xsAPI api = new _147xsAPI();
        List<SearchResultBean> resultBeanList = api.search("穿越");
        System.out.println(TextUtils.toPrettyFormat(resultBeanList));
    }

    @Test
    public void testGetNovel() throws IOException {
        _147xsAPI api = new _147xsAPI();
        NovelBean novel = api.getNovel(143516);
        ChapterBean chapContent = api.getChapContent(novel.lastChapter);
        System.out.println(chapContent.content);
    }
}
