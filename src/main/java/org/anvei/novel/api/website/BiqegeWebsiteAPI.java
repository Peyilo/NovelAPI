package org.anvei.novel.api.website;

import org.anvei.novel.NovelSource;
import org.anvei.novel.api.CrawlerAPI;
import org.anvei.novel.api.website.biqege.SearchResultBean;
import org.anvei.novel.api.website.common.ChapterBean;
import org.anvei.novel.api.website.common.NovelBean;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: 最近很多小说网站都添加了安全验证机制，待破解 <br/>
 *
 * 推测是先进行安全验证，之后会获取到一个cookie值，该cookie值使得之后的请求过程中无需再进行安全验证，
 * 在本版本的{@link BiqegeWebsiteAPI}中，直接提供了一个默认的cookie值
 */
public class BiqegeWebsiteAPI implements CrawlerAPI {

    private static final String API = "https://www.beqege.com";

    private static final String DEFAULT_COOKIE = "__cf_bm=GwqHZy3RVG7l6MPTzYOnmXSMHRqAipzDalSK49Uiqhs-1676812726-0-AesDK2dBUPFrI1Rvv+vw9NMbueFCOICH5BP4jQD2XszfE+JgeAlNFGG41LJfKIscwS+H5epm7SwEKVa8O5Ipulw25cXpHvV/8ceiuAmXpwbXkzp/V4J14KCy2GYTHGxrTSE3UMVKk6dP472WkOfVlnw=; cf_clearance=5kq0MTWNz8vP.SzpB_rHz65u6BJ7CwxaWHcHfCiuQS8-1676812913-0-250";
    private static final String DEFAULT_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36";

    private String cookie = DEFAULT_COOKIE;
    private String UA = DEFAULT_UA;

    /**
     * 获取小说基本信息、及其目录信息
     */
    public NovelBean getNovel(long novelId) throws IOException {
        return getNovel("/" + novelId);
    }

    public NovelBean getNovel(SearchResultBean searchResultBean) throws IOException {
        return getNovel(searchResultBean.url);
    }

    /**
     * 请求小说基本信息以及目录信息，但是不会请求小说章节内容
     */
    public NovelBean getNovel(String url) throws IOException {
        if (!url.contains(API)) {
            url = API + url;
        }
        NovelBean novelBean = new NovelBean(NovelSource.BiqegeWebsite);
        Document document = Jsoup.connect(url)
                .header("user-agent", getUA())
                .header("cookie", getCookie())
                .get();
        // 开始解析novelBean的基本信息
        Elements headerEles = document.select("#info");
        novelBean.url = url;
        novelBean.novelName = headerEles.select("h1").text();
        novelBean.author = headerEles.select("p:nth-child(2)").text();
        novelBean.lastUpdateTime = headerEles.select("#info > p:nth-child(4)").text();
        ChapterBean lastChapter = new ChapterBean();
        Elements lastChapterEles = headerEles.select("p:nth-child(5) > a");
        lastChapter.chapterName = lastChapterEles.text();
        lastChapter.url = lastChapterEles.attr("href");
        novelBean.lastChapter = lastChapter;
        novelBean.intro = headerEles.select("#intro").text();
        novelBean.coverUrl = document.select("#fmimg > img").attr("src");
        // 解析novelBean的章节列表信息
        Elements chapListEles = document.select("#list > dl > dd > a");
        for (int i = 0; i < chapListEles.size() - 1; i++) {
            Element chapListEle = chapListEles.get(i);
            ChapterBean chap = new ChapterBean();
            chap.chapterName = chapListEle.text();
            chap.url = chapListEle.attr("href");
            novelBean.chapterList.add(chap);
        }
        // 将最新章节添加进入列表中
        novelBean.chapterList.add(novelBean.lastChapter);
        return novelBean;
    }

    @Override
    public NovelSource getNovelSource() {
        return NovelSource.BiqegeWebsite;
    }

    private String paraPrefix = "\t";
    private String paraSuffix = "\n";

    public void setParaPrefix(String paraPrefix) {
        this.paraPrefix = paraPrefix;
    }

    public void setParaSuffix(String paraSuffix) {
        this.paraSuffix = paraSuffix;
    }

    /**
     * 根据给定的ChapterBean，获取其章节具体内容 <br/>
     * 该方法返回章节内容的同时，也会将章节内容赋给输入的ChapterBean的content属性 <br/>
     */
    public String getChapContent(ChapterBean chapterBean) throws IOException {
        String url;
        if (chapterBean.url.contains(API)) {
            url = chapterBean.url;
        } else {
            url = API + chapterBean.url;
        }
        StringBuilder builder = new StringBuilder();
        Document document = Jsoup.connect(url)
                .header("user-agent", getUA())
                .header("cookie", getCookie())
                .get();
        Elements paras = document.select("#content > p");
        for (Element para : paras) {
            builder.append(paraPrefix).append(para.text()).append(paraSuffix);
        }
        chapterBean.content = builder.toString();
        return chapterBean.content;
    }

    /**
     * 根据给定的关键字进行搜索小说
     */
    public List<SearchResultBean> search(String keyword) throws IOException {
        Document document = Jsoup.connect(API + "/search.php")
                .header("user-agent", getUA())
                .header("cookie", getCookie())
                .data("keyword", keyword)
                .post();
        ArrayList<SearchResultBean> searchResultBeans = new ArrayList<>();
        Elements results = document.select("#main > div > div.panel-body > ul > li");
        for (Element result : results) {
            SearchResultBean searchResultBean = new SearchResultBean();
            searchResultBean.tag = result.select(".s1").text().replace("[", "").replace("]", "");
            searchResultBean.author = result.select(".s4").text();
            searchResultBean.url = result.select(".s2 > a").attr("href");
            searchResultBean.novelName = result.select(".s2 > a").text();
            searchResultBean.lastChapter = new ChapterBean();
            searchResultBean.lastChapter.chapterName = result.select(".s3 > a").text();
            searchResultBean.lastChapter.url = result.select(".s3 > a").attr("href");
            searchResultBeans.add(searchResultBean);
        }
        return searchResultBeans;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getUA() {
        return UA;
    }

    public void setUA(String UA) {
        this.UA = UA;
    }
}
