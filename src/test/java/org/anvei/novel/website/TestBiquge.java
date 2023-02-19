package org.anvei.novel.website;

import org.anvei.novel.api.website.BiqegeWebsiteAPI;
import org.anvei.novel.api.website._147xsWebsiteAPI;
import org.anvei.novel.api.website.biqege.SearchResultBean;
import org.anvei.novel.api.website.common.ChapterBean;
import org.anvei.novel.api.website.common.NovelBean;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TestBiquge {

    @Test
    public void test1() throws IOException {
        Document document = Jsoup.connect("https://www.beqege.com/search.php")
                .header("cookie", "__cf_bm=GwqHZy3RVG7l6MPTzYOnmXSMHRqAipzDalSK49Uiqhs-1676812726-0-AesDK2dBUPFrI1Rvv+vw9NMbueFCOICH5BP4jQD2XszfE+JgeAlNFGG41LJfKIscwS+H5epm7SwEKVa8O5Ipulw25cXpHvV/8ceiuAmXpwbXkzp/V4J14KCy2GYTHGxrTSE3UMVKk6dP472WkOfVlnw=; cf_clearance=5kq0MTWNz8vP.SzpB_rHz65u6BJ7CwxaWHcHfCiuQS8-1676812913-0-250")
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                .data("keyword", "斗破")
                .post();
        System.out.println(document.html());
    }

    @Test
    public void test2() throws IOException {
        BiqegeWebsiteAPI api = new BiqegeWebsiteAPI();
        List<SearchResultBean> searchResultBeans = api.search("斗破");
        if (searchResultBeans.size() != 0) {
            SearchResultBean searchResultBean = searchResultBeans.get(0);
            NovelBean novel = api.getNovel(searchResultBean.url);
            int size = novel.chapterList.size();
            System.out.println("一共" + size + "章节");
            if (size != 0) {
                ChapterBean chapterBean = novel.lastChapter;
                System.out.println(api.getChapContent(chapterBean));
            }
        }
    }

    @Test
    public void test3() throws IOException {
        _147xsWebsiteAPI api = new _147xsWebsiteAPI();
        List<org.anvei.novel.api.website._147xs.SearchResultBean> resultBeans = api.search("斗破");
        if (resultBeans.size() != 0) {
            org.anvei.novel.api.website._147xs.SearchResultBean resultBean = resultBeans.get(0);
            NovelBean novel = api.getNovel(resultBean.url);
            int size = novel.chapterList.size();
            System.out.println("一共" + size + "章节");
            if (size != 0) {
                System.out.println(api.getChapContent(novel.lastChapter));
            }
        }
    }

}
