package org.anvei.novel.api;

import org.anvei.novel.api.hbooker.*;
import org.anvei.novel.utils.NetUtils;
import org.jsoup.Connection;

import java.io.IOException;

import static org.anvei.novel.api.hbooker.HbookerSecurity.decrypt;
import static org.anvei.novel.utils.TextUtils.getGson;

public class HbookerAPI implements API {

    public static final String API = "https://app.hbooker.com";

    private static final int SUCCESS_CODE = 100000;

    private static final int DEFAULT_SEARCH_COUNT = 10;

    private static final String DEFAULT_APP_VERSION = "3.0.303";
    private static final String DEFAULT_DEVICE_TOKEN = "iPad-F726E4CC-4FA9-432B-8338-E461F93AC7D8";

    // APP版本，该值关系到获取章节列表信息是否成功
    private String appVersion = DEFAULT_APP_VERSION;
    private String deviceToken = DEFAULT_DEVICE_TOKEN;

    private String account;
    private String loginToken;

    private int timeout = -1;

    public HbookerAPI(){
    }

    public HbookerAPI(String account, String loginToken) {
        this.account = account;
        this.loginToken = loginToken;
    }

    public HbookerAPI(String account, String loginToken, String deviceToken, String appVersion) {
        this.appVersion = appVersion;
        this.deviceToken = deviceToken;
        this.account = account;
        this.loginToken = loginToken;
    }

    private Connection getConnection(String suffix) {
        return NetUtils.getConnection(API + suffix)
                .data("account", account)
                .data("app_version", appVersion)
                .data("device_token", deviceToken)
                .data("login_token", loginToken);
    }

    public SearchResultJson search(String key) throws IOException {
        return search(key, 0);
    }

    public SearchResultJson search(String key, int page) throws IOException {
        return search(key, page, DEFAULT_SEARCH_COUNT);
    }

    public SearchResultJson search(String key, int page, int count) throws IOException {
        Connection connection = getConnection("/bookcity/get_filter_search_book_list")
                .data("key", key)
                .data("count", count + "")
                .data("page", page + "");
        if (timeout > 0) {
            connection.timeout(timeout);
        }
        String json = decrypt(connection.post().body().text());
        return getGson().fromJson(json, SearchResultJson.class);
    }


    public String getAccount() {
        return account;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public BookInfoJson getBookInfoJson(long bookId) throws IOException {
        Connection connection = getConnection("/book/get_info_by_id")
                .data("book_id", bookId + "");
        if (timeout > 0) {
            connection.timeout(timeout);
        }
        String json = decrypt(connection.post().body().text());
        return getGson().fromJson(json, BookInfoJson.class);
    }

    public DivisionInfoJson getDivisionInfoJson(long bookId) throws IOException {
        Connection connection = getConnection("/book/get_division_list")
                .data("book_id", bookId + "");
        if (timeout > 0) {
            connection.timeout(timeout);
        }
        String json = decrypt(connection.post().body().text());
        return getGson().fromJson(json, DivisionInfoJson.class);
    }

    public ChapListInfoJson getChapterListInfoJson(long divisionId) throws IOException {
        Connection connection = getConnection("/chapter/get_updated_chapter_by_division_id")
                .data("division_id", divisionId + "");
        if (timeout > 0) {
            connection.timeout(timeout);
        }
        String json = decrypt(connection.post().body().text());
        return getGson().fromJson(json, ChapListInfoJson.class);
    }

    private ChapterCommandJson getChapterCmd(long chapterId) throws IOException {
        Connection connection = getConnection("/chapter/get_chapter_cmd")
                .data("chapter_id", chapterId + "")
                .header("User-Agent", "Android  com.kuangxiangciweimao.novel  2.9.293,HONOR, TNNH-AN00, 29, 10");
        if (timeout > 0) {
            connection.timeout(timeout);
        }
        String json = decrypt(connection.post().body().text());
        return getGson().fromJson(json, ChapterCommandJson.class);
    }

    public ChapterInfoJson getChapterInfoJson(long chapterId) throws IOException {
        ChapterCommandJson chapterCommand = getChapterCmd(chapterId);
        if (chapterCommand.code != SUCCESS_CODE)
            return null;
        String command = chapterCommand.data.command;
        Connection connection = getConnection("/chapter/get_cpt_ifm")
                .data("chapter_id", chapterId + "")
                .data("chapter_command", command);
        if (timeout > 0) {
            connection.timeout(timeout);
        }
        String json = decrypt(connection.post().body().text());
        ChapterInfoJson chapterInfoJson = getGson().fromJson(json, ChapterInfoJson.class);
        if (chapterInfoJson.data != null && chapterInfoJson.data.chapterInfo != null
                && chapterInfoJson.data.chapterInfo.txtContent != null) {
            // 对章节内容再次解码
            chapterInfoJson.data.chapterInfo.txtContent = decrypt(chapterInfoJson.data.chapterInfo.txtContent.getBytes(), command);
        }
        return chapterInfoJson;
    }
}
