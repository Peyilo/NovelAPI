package org.anvei.novel.api;

import org.anvei.novel.api.hbooker.*;
import org.anvei.novel.utils.NetUtils;
import org.jsoup.Connection;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import static org.anvei.novel.utils.TextUtils.getGson;

public class HbookerAPI {

    public static final String API = "https://app.hbooker.com";

    public static final String decryptKey = "zG2nSeEfSHfvTCHy5LCcqtBbQehKNLXn";

    private static final int SUCCESS_CODE = 100000;

    private static final int DEFAULT_SEARCH_COUNT = 10;

    // 默认APP版本
    private static final String DEFAULT_APP_VERSION = "3.0.303";
    // APP版本，该值关系到获取章节列表信息是否成功
    private String appVersion = DEFAULT_APP_VERSION;

    private String username;
    private String password;

    private int timeout = -1;

    public HbookerAPI() {
    }

    public HbookerAPI(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * 解密
     */
    public static String decrypt(String src) {
        return decrypt(src.getBytes());
    }

    public static String decrypt(byte[] src) {
        return decrypt(src, decryptKey);
    }

    private static String decrypt(byte[] src, String key) {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            sha256.update(key.getBytes(StandardCharsets.UTF_8));
            byte[] keyBytes = sha256.digest();
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] res = cipher.doFinal(Base64.getDecoder().decode(src));

            return new String(res).trim().replace("\\\\u", "\\u");
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private Connection getConnection(String suffix) {
        return NetUtils.getConnection(API + suffix)
                .header("Host", "app.hbooker.com")
                .header("User-Agent", "HappyBook/3.0.3 (iPad; iOS 16.1; Scale/2.00)")
                .header("Connection", "keep-alive")
                .data("account", "%E4%B9%A6%E5%AE%A2831585069584")
                .data("device_token", "iPad-F726E4CC-4FA9-432B-8338-E461F93AC7D8")
                .data("login_token", "84550c4165e022cafa1bb91bef6d9382")
                .data("app_version", appVersion);
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


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
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

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public ChapterCommandJson getChapterCmdS(long chapterId) throws IOException {
        Connection connection = getConnection("/chapter/get_chapter_cmd_s")
                .data("chapter_id", chapterId + "")
                .data("token", "9edc47755b8160e5a071b4fa717f22fa32");
        if (timeout > 0) {
            connection.timeout(timeout);
        }
        String json = decrypt(connection.post().body().text());
        return getGson().fromJson(json, ChapterCommandJson.class);
    }

    public ChapterInfoJson getChapterInfoJson(long chapterId) throws IOException {
        ChapterCommandJson chapterCmdS = getChapterCmdS(chapterId);
        if (chapterCmdS.code != SUCCESS_CODE)
            return null;
        String command = chapterCmdS.data.command;
        Connection connection = getConnection("/chapter/get_cpt_ifm")
                .data("chapter_command", command)
                .data("chapter_id", chapterId + "");
        if (timeout > 0) {
            connection.timeout(timeout);
        }
        String json = decrypt(connection.post().body().text());
        ChapterInfoJson chapterInfoJson = getGson().fromJson(json, ChapterInfoJson.class);
        if (chapterInfoJson.data != null && chapterInfoJson.data.chapterInfo != null
                && chapterInfoJson.data.chapterInfo.txtContent != null) {
            chapterInfoJson.data.chapterInfo.txtContent = decryptTxtContent(chapterInfoJson.data.chapterInfo.txtContent, command);
        }
        return chapterInfoJson;
    }

    // 对章节内容进行解码
    private String decryptTxtContent(String content, String command) {
        return decrypt(content);
    }

}
