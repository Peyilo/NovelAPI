package org.anvei.novel.api.website;

import org.anvei.novel.NovelSource;
import org.anvei.novel.api.RetryableAPI;
import org.anvei.novel.api.exceptions.ConnectionException;
import org.anvei.novel.api.website._147xs.SearchResultBean;
import org.anvei.novel.api.website.common.ChapterBean;
import org.anvei.novel.api.website.common.NovelBean;
import org.anvei.novel.api.website.common.NovelStatus;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: 和{@link BiqegeWebsiteAPI}相比，有大量重复代码，待建立一个统一的模型
 */
public class _147xsWebsiteAPI extends RetryableAPI {

    private static final String API = "https://www.147xs.org/";

    private static final String DEFAULT_COOKIE = "__gads=ID=3591258f3d6a8a2a-221423120bd90007:T=1672117118:RT=1672117118:S=ALNI_MY-8BiGIVvZrTgbNlapW4iO1Ejyug; cf_clearance=S0wcNagkdGmUk.sp55maFr3Lez_MNswXmVAOXnpG1aw-1676798340-0-250; __gpi=UID=00000b987a2bf523:T=1672117118:RT=1676818067:S=ALNI_MZaglp5i7kqUZbffVDMnMMUi0IrVQ";
    private static final String DEFAULT_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36";

    private String cookie = DEFAULT_COOKIE;
    private String UA = DEFAULT_UA;

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

    @Override
    public NovelSource getNovelSource() {
        return NovelSource._143xsWebsite;
    }

    /**
     * 搜索功能
     * @param keyword 关键字
     */
    public List<SearchResultBean> search(String keyword) throws IOException, ConnectionException {
        Connection connection = Jsoup.connect(API + "/search.php")
                .header("cookie", getCookie())
                .header("User-Agent", getUA())
                .data("keyword", keyword);
        Document document = connect(connection, Connection.Method.POST);
        List<SearchResultBean> searchResultBeanList = new ArrayList<>();
        Elements results = document.select("#bookcase_list > tr");
        for (Element result : results) {
            SearchResultBean resultBean = new SearchResultBean();
            Elements tds = result.select("td");
            resultBean.tag = tds.get(0).text();
            resultBean.novelName = tds.get(1).text();
            resultBean.url = tds.get(1).select("a").attr("href");
            resultBean.author = tds.get(3).text();
            resultBean.lastChapter = new ChapterBean();
            resultBean.lastChapter.chapterName = tds.get(2).text();
            resultBean.lastChapter.url = tds.get(2).select("a").attr("href");
            resultBean.lastUpdateTime = tds.get(4).text();
            if (tds.get(5).text().contains("连载")) {
                resultBean.status = NovelStatus.Updating;
            } else {
                resultBean.status = NovelStatus.Finished;
            }
            searchResultBeanList.add(resultBean);
        }
        return searchResultBeanList;
    }

    /**
     * 该方法会获取小说基本信息，以及小说章节列表信息
     */
    public NovelBean getNovel(String url) throws IOException, ConnectionException {
        NovelBean novelBean = new NovelBean(getNovelSource());
        novelBean.url = url;
        if (!url.contains(API)) {
            url = API + url;
        }
        Connection connection = Jsoup.connect(url)
                .header("cookie", getCookie())
                .header("User-Agent", getUA());
        Document document = connect(connection, Connection.Method.GET);
        Elements mainInfo = document.select("#maininfo");
        novelBean.novelName = mainInfo.select("#info > h1").text();
        novelBean.author = mainInfo.select("#info > p:nth-child(2)").text().split("：", 2)[1];
        novelBean.lastUpdateTime = mainInfo.select("#info > p:nth-child(4)").text().split("：", 2)[1];
        novelBean.intro = mainInfo.select("#intro").text();
        novelBean.lastChapter = new ChapterBean();
        novelBean.lastChapter.chapterName = mainInfo.select("#info > p:nth-child(5) > a").text();
        novelBean.lastChapter.url = mainInfo.select("#info > p:nth-child(5) > a").attr("href");
        novelBean.coverUrl = document.select("#fmimg > img").attr("src");
        // 接下来解析章节目录信息
        Elements elements = document.select("#list > dl > dd > a");
        for (int i = 0; i < elements.size() - 1; i++) {
            Element chapListEle = elements.get(i);
            ChapterBean chap = new ChapterBean();
            chap.chapterName = chapListEle.text();
            chap.url = chapListEle.attr("href");
            novelBean.chapterList.add(chap);
        }
        // 将最新章节添加进入列表中
        novelBean.chapterList.add(novelBean.lastChapter);
        return novelBean;
    }

    public NovelBean getNovel(SearchResultBean searchResultBean) throws IOException, ConnectionException {
        return getNovel(searchResultBean.url);
    }

    public NovelBean getNovel(long novelId) throws IOException, ConnectionException {
        return getNovel("/book/" + novelId + "/");
    }

    public String getChapContent(ChapterBean chapter) throws ConnectionException {
        chapter.content = getChapContent(chapter.url);
        return chapter.content;
    }

    public String getChapContent(String url) throws ConnectionException {
        if (!url.contains(API)) {
            url = API + url;
        }
        Connection connection = Jsoup.connect(url)
                .header("cookie", getCookie())
                .header("User-Agent", getUA());
        Document document = connect(connection, Connection.Method.GET);
        Elements paras = document.select("#content > p");
        StringBuilder builder = new StringBuilder();
        for (Element para : paras) {
            builder.append("\t").append(para.text()).append("\n");
        }
        return builder.toString();
    }

}
