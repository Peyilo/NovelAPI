package org.anvei.novel.website;

import org.anvei.novel.api.website.BiqumuAPI;
import org.anvei.novel.api.website.beqege.ChapterBean;
import org.anvei.novel.api.website.beqege.NovelBean;
import org.anvei.novel.api.website.beqege.SearchResultBean;
import org.anvei.novel.utils.TextUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TestBiqumu {

    @Test
    public void test1() throws IOException {
        BiqumuAPI api = new BiqumuAPI();
        NovelBean novelBean = api.getNovel(152281);
        System.out.println(novelBean.author);
        System.out.println(novelBean.novelName);
        System.out.println(novelBean.intro);
        System.out.println(novelBean.lastUpdateTime);
        System.out.println(TextUtils.toPrettyFormat(novelBean.lastChapter));
        System.out.println(novelBean.coverUrl);
        System.out.println(novelBean.chapterList.size());

        ChapterBean chapContent = api.getChapContent(novelBean.lastChapter);
        System.out.println(chapContent.content);
    }

    @Test
    public void testSearch() throws IOException {
        BiqumuAPI api = new BiqumuAPI();
        List<SearchResultBean> searchResultBeans = api.search("萝莉");
        System.out.println(TextUtils.toPrettyFormat(searchResultBeans));
    }
}
