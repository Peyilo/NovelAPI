package org.anvei.novel.api.website;

import org.anvei.novel.NovelSource;
import org.anvei.novel.api.API;
import org.anvei.novel.api.website.common.ChapterBean;
import org.anvei.novel.api.website.fanqie.NovelBean;
import org.anvei.novel.api.website.fanqie.SearchResultJson;
import org.anvei.novel.utils.NetUtils;
import org.anvei.novel.utils.TextUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FanqieWebsiteAPI implements API {

    private static final String baseUrl = "https://fanqienovel.com";
    private static final String searchAPI = baseUrl + "/api/author/search/search_book/v1";
    private static final String imgAPI = "https://p1-tt.byteimg.com/img/";

    @Override
    public NovelSource getNovelSource() {
        return NovelSource.FanqieWebsite;
    }

    public SearchResultJson search(String keyword, int pageIndex, int pageCount) throws IOException {
        Document document = Jsoup.connect(searchAPI).headers(getHeaders())
                .ignoreContentType(true)
                .data("filter", "127,127,127")
                .data("page_count", pageCount + "")
                .data("page_index", pageIndex + "")
                .data("query_word", keyword)
                .get();
        return TextUtils.getGson().fromJson(document.body().text(), SearchResultJson.class);
    }

    public SearchResultJson search(String keyword, int pageIndex) throws IOException {
        return search(keyword, pageIndex, 10);
    }

    public SearchResultJson search(String keyword) throws IOException {
        return search(keyword, 0);
    }

    protected Map<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", NetUtils.getRandomUA());
        return headers;
    }

    // 向服务器请求图片
    public InputStream requestImage(String url) throws IOException {
        String tempUrl;
        if (url.contains(imgAPI)) {
            tempUrl = url;
        } else {
            tempUrl = imgAPI + url + "~240x312.jpg";
        }
        Connection connection = Jsoup.connect(tempUrl).headers(getHeaders())
                .method(Connection.Method.GET)
                .ignoreContentType(true);
        Connection.Response response = connection.execute();
        return response.bodyStream();
    }

    // 获取小说基本信息、目录信息
    public NovelBean getNovel(String novelId) throws IOException {
        String url = baseUrl + "/page/" + novelId;
        Document document = Jsoup.connect(url).headers(getHeaders()).get();
        NovelBean novelBean = new NovelBean();
        novelBean.novelName = document.body().select("#app > div > div.muye > div > div.page-wrap > div > div.page-header-info > div.info > div.info-name > h1").text();
        novelBean.author = document.body().select("#app > div > div.muye > div > div.page-wrap > div > div.page-header-info > div.author > div.author-info > a > div > span.author-name-text").text();
        novelBean.intro = document.body().select("#app > div > div.muye > div > div.page-body-wrap > div > div.page-abstract-content > p").text();
        novelBean.novelId = novelId;
        novelBean.coverUrl = "https:" + document.body().select("#app > div > div.muye > div > div.page-wrap > div > div.page-header-info > div.muye-book-cover.img.is-book > div > div.book-cover.loaded > img").attr("src");
        novelBean.lastUpdateTime = document.body().select("#app > div > div.muye > div > div.page-wrap > div > div.page-header-info > div.info > div.info-last > span").text();
        novelBean.lastChapter = new ChapterBean();
        Elements lastChapter = document.body().select("#app > div > div.muye > div > div.page-wrap > div > div.page-header-info > div.info > div.info-last > a");
        novelBean.lastChapter.chapterName = lastChapter.text();
        novelBean.lastChapter.url = lastChapter.attr("href");

        // 处理章节目录信息
        Elements volumeEles = document.body().select("#app > div > div.muye > div > div.page-body-wrap > div > div.page-directory-content > div");
        for (Element volumeEle : volumeEles) {
            NovelBean.Volume volume = new NovelBean.Volume();
            volume.volumeName = volumeEle.select("div.volume").text();
            Elements chapterEles = volumeEle.select("div.chapter > div.chapter-item > a");
            for (Element chapterEle : chapterEles) {
                ChapterBean chapterBean = new ChapterBean();
                chapterBean.chapterName = chapterEle.text();
                chapterBean.url = chapterEle.attr("href");
                volume.chapterBeanList.add(chapterBean);
            }
            novelBean.volumeList.add(volume);
        }
        return novelBean;
    }

    /**
     * 请求章节具体内容
     */
    public ChapterBean getChapContent(ChapterBean chapterBean) throws IOException {
        String tempUrl;
        if (chapterBean.url.contains(baseUrl)) {
            tempUrl = chapterBean.url;
        } else {
            tempUrl = baseUrl + chapterBean.url;
        }
        Document document = Jsoup.connect(tempUrl).headers(getHeaders()).get();
        Elements paras = document.body().select("#app > div > div > div > div.muye-reader-box > div.muye-reader-content.noselect > div > p");
        StringBuilder builder = new StringBuilder();
        // 获取不到信息
        boolean addition = false;       // 是否添加提示信息：下载【番茄小说】免费阅读，更多免费好书尽在番茄小说APP，还可以和作者互动哦！
        if (paras.size() == 0) {
            paras = document.body().select("#app > div > div > div > div.muye-reader-box > div.muye-reader-content.noselect > p");
            if (paras.size() == 0) {        // 如果还是没有数据，就表示url有问题
                return chapterBean;
            }
            addition = true;
        }
        for (Element para : paras) {
            builder.append("\t").append(para.text()).append("\n");
        }
        if (addition) {
            builder.append("\t").append("下载【番茄小说】免费阅读，更多免费好书尽在番茄小说APP，还可以和作者互动哦！").append("\n");
        }
        chapterBean.content = builder.toString();
        return chapterBean;
    }

}
