package org.anvei.novel.api.website;

import org.anvei.novel.NovelSource;
import org.anvei.novel.api.API;
import org.anvei.novel.api.website.common.ChapterBean;
import org.anvei.novel.api.website.common.NovelBean;
import org.anvei.novel.api.website.biqumu.SearchResultBean;
import org.anvei.novel.utils.NetUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BiqumuAPI implements API {

    private static final String API = "http://www.biqumu.com/";

    private volatile boolean pageReqIsFinished;
    private volatile int pageReqFinished;

    private int needReq;

    private synchronized void addOneToPageReqFinished() {
        pageReqFinished++;
    }

    public static final long DEFAULT_TIMEOUT = 15000;
    private long timeout = DEFAULT_TIMEOUT;

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * 获取小说基本信息、及其目录信息
     */
    public NovelBean getNovel(long novelId) throws IOException {
        return getNovel("/book/" + novelId + "/");
    }

    public NovelBean getNovel(SearchResultBean searchResultBean) throws IOException {
        return getNovel(searchResultBean.url);
    }

    public NovelBean getNovel(String url) throws IOException {
        long startTime = System.currentTimeMillis();
        NovelBean novelBean = new NovelBean(getNovelSource());
        novelBean.url = url;
        if (!url.contains(API)) {
            url = API + url;
        }
        Document[] pageList = new Document[50];       // 最多支持50 + 1页，即5100章节
        pageReqFinished = 0;
        pageReqIsFinished = false;
        needReq = 0;
        Document home = Jsoup.connect(url).header("User-Agent", NetUtils.USER_AGENT_VALUE).get();
        Element body = home.body();
        // 考虑到网络请求速度过于耗时，开启多线程请求其他章节分页
        final String tempUrl = url;
        new Thread(() -> {
            try {
                Document document = Jsoup.connect(tempUrl + "1/").header("User-Agent", NetUtils.USER_AGENT_VALUE).get();
                Elements options = document.select("body > div.container > div:nth-child(2) > div > div:nth-child(4) > select > option");
                // 只有一页
                switch (options.size()) {
                    case 1: pageReqIsFinished = true;
                        break;
                    case 2:
                        pageList[0] = document;
                        pageReqIsFinished = true;
                        break;
                    default: {
                        pageList[0] = document;
                        needReq = options.size() - 2;
                        for (int i = 2; i < options.size(); i++) {
                            final int index = i;
                            new Thread(() -> {
                                String value = options.get(index).attr("value");
                                Document temp = null;
                                try {
                                    temp = Jsoup.connect(API + value).header("User-Agent", NetUtils.USER_AGENT_VALUE).get();
                                    pageList[index - 1] = temp;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                addOneToPageReqFinished();
                            }).start();
                        }
                        // 等待完成请求
                        while ((System.currentTimeMillis() - startTime) < timeout && pageReqFinished < needReq);
                        pageReqIsFinished = true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        novelBean.novelName = body.select("body > div.container > div.row.row-detail > div:nth-child(1) > div > div.info > div.top > h1").text();
        novelBean.author = body.select("body > div.container > div.row.row-detail > div:nth-child(1) > div > div.info > div.top > div > p:nth-child(1)").text().split("：", 2)[1];
        novelBean.intro = body.select("body > div.container > div.row.row-detail > div:nth-child(2) > div > p").text();
        novelBean.lastUpdateTime = body.select("body > div.container > div.row.row-detail > div:nth-child(1) > div > div.info > div.top > div > p:nth-child(5)").text().split("：", 2)[1];
        novelBean.lastChapter = new ChapterBean();
        Elements e = body.select("body > div.container > div.row.row-detail > div:nth-child(1) > div > div.info > div.top > div > p:nth-child(6) > a");
        novelBean.lastChapter.chapterName = e.text();
        novelBean.lastChapter.url = e.attr("href");
        novelBean.coverUrl = body.select("body > div.container > div.row.row-detail > div:nth-child(1) > div > div.imgbox > img").attr("src");
        Elements firstPageChapter = body.select("body > div.container > div:nth-child(2) > div > ul:nth-child(4) > li > a");
        for (Element element : firstPageChapter) {
            ChapterBean chapterBean = new ChapterBean();
            chapterBean.chapterName = element.text();
            chapterBean.url = element.attr("href");
            novelBean.chapterList.add(chapterBean);
        }

        // 等待其他分页完成请求
        while((System.currentTimeMillis() - startTime) < timeout && !pageReqIsFinished);
        for (int i = 0; i < needReq + 1; i++) {
            if (pageList[i] == null)
                break;
            Element element = pageList[i].body();
            Elements elements = element.select("body > div.container > div:nth-child(2) > div > ul > li > a");
            // 处理网站的反爬虫设计
            String style = pageList[i].select("head style").toString();
            int start = 0;
            int end = 0;
            Pattern pattern1 = Pattern.compile("li:nth-child\\(\\d+\\)");
            Matcher matcher1 = pattern1.matcher(style);
            while (matcher1.find()) {
                start++;
            }
            Pattern pattern2 = Pattern.compile("li:nth-last-child\\(\\d+\\)");
            Matcher matcher2 = pattern2.matcher(style);
            while (matcher2.find()) {
                end++;
            }
            for (int j = start; j < elements.size() - end; j++) {
                Element temp = elements.get(j);
                ChapterBean chapterBean = new ChapterBean();
                chapterBean.chapterName = temp.text();
                chapterBean.url = temp.attr("href");
                novelBean.chapterList.add(chapterBean);
            }
        }
        return novelBean;
    }

    @Override
    public NovelSource getNovelSource() {
        return NovelSource.BiqumuWebsite;
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
     * 根据给定的ChapterBean，获取其章节具体内容
     */
    public ChapterBean getChapContent(ChapterBean chapterBean) throws IOException {
        String url = API + chapterBean.url;
        StringBuilder builder = new StringBuilder();
        Document document = Jsoup.connect(url).header("User-Agent", NetUtils.USER_AGENT_VALUE).get();
        Elements paras = document.select("body > div.container > div.row.row-detail > div > div > p");
        for (Element para : paras) {
            builder.append(paraPrefix).append(para.text()).append(paraSuffix);
        }
        String href = document.select("body > div.container > div.row.row-detail > div > div > div:nth-child(31) > a:nth-child(4)").attr("href");
        while (href.contains("_")) {
            document = Jsoup.connect(API + href).header("User-Agent", NetUtils.USER_AGENT_VALUE).get();
            paras = document.select("body > div.container > div.row.row-detail > div > div > p");
            boolean isFirst = true;
            for (Element para : paras) {
                if (isFirst) {
                    isFirst = false;
                    continue;           // 跳过第一段: 重复的
                }
                builder.append(paraPrefix).append(para.text()).append(paraSuffix);
            }
            href = document.select("body > div.container > div.row.row-detail > div > div > div:nth-child(31) > a:nth-child(4)").attr("href");
        }
        chapterBean.content = builder.toString();
        return chapterBean;
    }

    /**
     * 根据给定的关键字进行搜索小说
     */
    public List<SearchResultBean> search(String keyWord) throws IOException {
        Document document = Jsoup.connect(API + "/search.html").header("User-Agent", NetUtils.USER_AGENT_VALUE)
                .data("s", keyWord)
                .post();
        ArrayList<SearchResultBean> searchResultBeans = new ArrayList<>();
        Elements results = document.select("body > div.container > div:nth-child(1) > div > ul > li");
        for (Element result : results) {
            SearchResultBean searchResultBean = new SearchResultBean();
            searchResultBean.tag = result.select(".n1").text().replace("[", "").replace("]", "");
            searchResultBean.author = result.select(".n4").text();
            searchResultBean.url = result.select(".n2 > a").attr("href");
            searchResultBean.novelName = result.select(".n2 > a").text();
            searchResultBean.lastChapter = new ChapterBean();
            searchResultBean.lastChapter.chapterName = result.select(".n3 > a").text();
            searchResultBean.lastChapter.url = result.select(".n3 > a").attr("href");
            searchResultBeans.add(searchResultBean);
        }
        return searchResultBeans;
    }

}
