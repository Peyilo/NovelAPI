package org.anvei.novel.api;

import org.anvei.novel.api.sfacg.*;
import org.anvei.novel.utils.FileUtils;
import org.anvei.novel.utils.SecurityUtils;
import org.jsoup.Connection;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.anvei.novel.utils.NetUtils.getConnection;
import static org.anvei.novel.utils.TextUtils.getGson;

/**
 * 菠萝包轻小说  (官网 https://www.sfacg.com/) <br/>
 * 该类提供了菠萝包轻小说网的登录、搜索、账号、小说相关API    <br/>
 */
public class SfacgAPI {

    private static final String API = "https://api.sfacg.com";              // SFACG的iOS、Android查询API

    private static final String WeChatAPI = "https://minipapi.sfacg.com/pas/mpapi";   // 微信小程序API

    private static final String WeChatVipAPI = "https://m.sfacg.com";

    // cookie的相应的key值
    private static final String SFCommunity = ".SFCommunity";
    private static final String sessionMinip = "session_Minip1";
    private static final String sessionAPP = "session_APP";

    private static final int DEFAULT_SEARCH_SIZE = 12;          // 默认一页查询十二条数据

    private static final String salt = "xw3#a12-x";             // MD5加盐 "td9#Kn_p7vUw"

    private static final int HEADERS_APP = 0x01;
    private static final int HEADERS_MINI = 0x02;

    private final Map<String, String> cookies = new HashMap<>();

    public enum LoginStatus {
        UnLogin,            // 未登录
        APP,                // 应用登录
        Mini                // 微信小程序登录
    }

    private LoginStatus loginStatus = LoginStatus.UnLogin;          // 账号登录状态

    private int timeout = -1;

    private String username;
    private String password;

    public SfacgAPI(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public SfacgAPI() {
    }

    /**
     * 配置微信小程序或者iOS、Android APP所需的请求头信息
     * @param headerId 可选项: HEADERS_APP、HEADERS_MINI
     */
    private Map<String, String> getHeaders(int headerId) {
        Map<String, String> headers = new HashMap<>();
        if (headerId == HEADERS_APP) {
            headers.put("Authorization", "Basic YXBpdXNlcjozcyMxLXl0NmUqQWN2QHFlcg==");
        } else if (headerId == HEADERS_MINI) {
            headers.put("sf-minip-info", "minip_novel/1.0.72(iOS;16.1)/wxmp");
        } else {
            throw new IllegalArgumentException("headerId非法");
        }
        return headers;
    }

    /**
     * 生成SFSecurity值，具体计算算法暂时未知，该函数暂时作为调试函数使用 <br/>
     * TODO: 通过Android逆向，分析出其生成算法
     */
    public String getSFSecurity(String deviceToken) {
        String nonce = UUID.randomUUID().toString();
        String timestamp = System.currentTimeMillis() + "";
        String sign = SecurityUtils.getMD5Str(nonce, timestamp, deviceToken, salt);
        assert sign != null;
        return "nonce=" + nonce + "&timestamp=" + timestamp + "&devicetoken=" + deviceToken
                + "&sign=" + sign.toUpperCase();
    }

    /**
     * 获取小说章节列表信息
     */
    public ChapListJson getChapListJson(long novelId) throws IOException {
        Connection connection = getConnection(API + "/novels/" + novelId + "/dirs")
                .headers(getHeaders(HEADERS_APP));
        if (timeout > 0) {
            connection.timeout(timeout);
        }
        String json = connection.get().body().text();                   // JSON解析
        return getGson().fromJson(json, ChapListJson.class);
    }

    /**
     * 利用微信小程序API，获取章节内容信息（iOS、Android端获取章节内容的API被加密过了，暂时无法使用其获取信息）
     * 该方法无法获取vip章节内容
     */
    public ChapContentJson getChapContentJson(long chapId) throws IOException {
        Connection connection = getConnection(WeChatAPI + "/Chaps/" + chapId)
                .headers(getHeaders(HEADERS_MINI))
                .header("cookie", getCookieValue())
                .data("expand", "content,needFireMoney,originNeedFireMoney,tsukkomi")
                .data("autoOrder", "true");
        if (timeout > 0) {
            connection.timeout(timeout);
        }
        String json = connection.get().body().text();
        return getGson().fromJson(json, ChapContentJson.class);
    }

    /**
     * 该函数只能保存图片形式的vip章节，而且图片非常模糊（微信小程序API）
     */
    public boolean saveVipChapPic(long novelId, long chapId, File target) {
        if (loginStatus != LoginStatus.Mini)
            return false;
        Connection connection = getConnection(WeChatVipAPI + "/ajax/ashx/common.ashx")
                .header("cookie", getCookieValue())
                .data("op", "getChapPic")
                .data("cid", chapId + "")
                .data("nid", novelId + "")
                .data("w", "2180")          // 图片宽度
                .data("font", "12")         // 字体大小
                .method(Connection.Method.GET);
        if (timeout > 0) {
            connection.timeout(timeout);
        }
        try {
            connection.execute();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        };
        if (connection.response().statusCode() == 200) {
            return FileUtils.writeFile(target, connection.response().bodyStream());
        }
        return false;
    }

    /**
     * 模拟登录功能，登录之后，会保存相应的cookies
     */
    private boolean login(Connection connection, LoginStatus status) {
        System.out.println("[INFO] Attempting to sign in, current loginStatus: " + status.name());
        if (loginStatus != LoginStatus.UnLogin) {
            loginOut();
        }
        if (timeout > 0) {
            connection.timeout(timeout);
        }
        try {
            connection.execute();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        Connection.Response response = connection.response();
        String json = response.body();
        LoginJson loginJson = getGson().fromJson(json, LoginJson.class);
        System.out.println("[INFO] loginJson.status.httpCode: " + loginJson.status.httpCode);
        System.out.println("[INFO] loginJson.status.msg: " + loginJson.status.msg);
        if (loginJson.status.httpCode != 200) {
            return false;
        }
        cookies.put(SFCommunity, response.cookie(SFCommunity));
        if (status == LoginStatus.Mini) {
            cookies.put(sessionMinip, response.cookie(sessionMinip));
        } else if (status == LoginStatus.APP) {
            cookies.put(sessionAPP, response.cookie(sessionAPP));
        }
        loginStatus = status;
        for (String s : cookies.keySet()) {
            System.out.println("[INFO] cookies[" + s + "]: " + cookies.get(s));
        }
        return true;
    }

    /**
     * 登录Sfacg微信小程序
     */
    public boolean loginMini() {
        Connection connection = getConnection(WeChatAPI + "/sessions")
                .headers(getHeaders(HEADERS_MINI))
                .data("username", username)
                .data("password", password)
                .method(Connection.Method.POST);
        return login(connection, LoginStatus.Mini);
    }

    public boolean loginMini(String username, String password) {
        this.username = username;
        this.password = password;
        return loginMini();
    }

    /**
     * 手机应用模拟登录 <br/>
     * TODO: 未完工、作为调试使用
     */
    public boolean loginAPP() {
        String security = getSFSecurity("6F9A9878-637A-4A24-BB42-4B589E29C9F3");
        Connection connection = getConnection(API + "/sessions")
                .headers(getHeaders(HEADERS_APP))
                .header("User-Agent", "boluobao/4.9.16(iOS;16.1)/appStore/6F9A9878-637A-4A24-BB42-4B589E29C9F3")
                .header("SFSecurity", security)               // sign可以更改，只需要有就可以了
                .data("userName", username)
                .data("passWord", password)
                .method(Connection.Method.POST);
        return login(connection, LoginStatus.APP);
    }

    public boolean loginAPP(String username, String password) {
        this.username = username;
        this.password = password;
        return loginAPP();
    }

    /**
     * 注销账号
     */
    public void loginOut() {
        cookies.clear();
        loginStatus = LoginStatus.UnLogin;
    }

    private String getCookieValue() {
        if (loginStatus == LoginStatus.UnLogin) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (cookies.containsKey(SFCommunity)) {
            stringBuilder.append(SFCommunity)
                    .append("=")
                    .append(cookies.get(SFCommunity))
                    .append(";");
        }
        if (loginStatus == LoginStatus.Mini && cookies.containsKey(sessionMinip)) {
            stringBuilder.append(sessionMinip)
                    .append("=")
                    .append(cookies.get(sessionMinip))
                    .append(";");
        } else if (loginStatus == LoginStatus.APP && cookies.containsKey(sessionAPP)) {
            stringBuilder.append(sessionAPP)
                    .append("=")
                    .append(cookies.get(sessionAPP))
                    .append(";");
        }
        return stringBuilder.toString();
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    // TODO: 待支持APP登录获取账号信息
    public AccountJson getAccountJson() throws IOException {
        if (loginStatus == LoginStatus.UnLogin) {
            throw new IllegalStateException("账号未登录!");
        } else if (loginStatus == LoginStatus.APP) {
            throw new IllegalStateException("暂时还不支持获取APP登录获取账号信息!");
        }
        Connection connection = getConnection(WeChatAPI + "/user")
                .headers(getHeaders(HEADERS_MINI))
                .header("cookie", getCookieValue());
        if (timeout > 0) {
            connection.timeout(timeout);
        }
        String json = connection.get().body().text();
        return getGson().fromJson(json, AccountJson.class);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public LoginStatus getLoginStatus() {
        return loginStatus;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public SearchResultJson search(String key) throws IOException {
        return search(key, 0);
    }

    public SearchResultJson search(String key, int page) throws IOException {
        return search(key, page, DEFAULT_SEARCH_SIZE);
    }

    /**
     * @param key 搜索关键字
     * @param page 页码（从0开始）
     * @param size 单页的小说个数，最大值为20
     */
    public SearchResultJson search(String key, int page, int size) throws IOException {
        SearchParams params = new SearchParams(key);
        params.page = page;
        params.size = size;
        return search(params);
    }

    public SearchResultJson search(SearchParams params) throws IOException {
        Connection connection = getConnection(API + "/search/novels/result/new")
                .headers(getHeaders(HEADERS_APP))
                .header("User-Agent", "boluobao/4.9.16(iOS;16.1)/appStore/")
                .data("expand", "typeName,tags,intro,latestchaptitle,latestchapintro,authorname,authorName,sysTags")
                .data("page", params.page + "")
                .data("size", params.size + "")
                .data("q", params.key)
                .data("sort", params.sort.name())
                .data("systagids", "");
        if (params.timeout > 0) {               // 如果SearchParams设置了超时时间，就使用其配置的超时时间
            connection.timeout(timeout);        // 否则就看Sfacg实例是否设置了超时时间
        } else if (timeout > 0) {
            connection.timeout(timeout);
        }
        String json = connection.get().body().text();
        return getGson().fromJson(json, SearchResultJson.class);
    }

}
