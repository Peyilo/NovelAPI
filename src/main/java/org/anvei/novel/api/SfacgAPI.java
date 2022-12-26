package org.anvei.novel.api;

import org.anvei.novel.api.sfacg.*;
import org.anvei.novel.utils.SecurityUtils;
import org.jsoup.Connection;

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
public class SfacgAPI implements API {

    private static final String API = "https://api.sfacg.com";              // SFACG APP查询API

    // cookie的相应的key值
    private static final String SFCommunity = ".SFCommunity";
    private static final String sessionAPP = "session_APP";

    private static final int DEFAULT_SEARCH_SIZE = 12;          // 默认一页查询十二条数据

    private static final String DEFAULT_DEVICE_TOKEN = "6F9A9878-637A-4A24-BB42-4B589E29C9F3";
    private static final String DEFAULT_USER_AGENT = "boluobao/4.9.16(iOS;16.1)/appStore/6F9A9878-637A-4A24-BB42-4B589E29C9F3";

    public static final int SUCCESS_CODE = 200;

    // MD5加盐，由apk文件解压获取libsfdata.so库文件，再对so文件的汇编指令流分析，
    // 最后可以在MD5::MD5(std::string const&)函数下得出该盐值
    private static final String salt = "FMLxgOdsfxmN!Dt4";

    public enum LoginStatus {
        UnLogin,                // 未登录
        Login,                  // 登录
    }

    private LoginStatus loginStatus = LoginStatus.UnLogin;          // 账号登录状态

    private final Map<String, String> cookies = new HashMap<>();    // Cookies

    private String userAgent;

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
     * 配置APP所需的请求头信息
    */
    protected Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic YXBpdXNlcjozcyMxLXl0NmUqQWN2QHFlcg==");
        headers.put("User-Agent", getUserAgent());
        return headers;
    }

    /**
     * 获取SFSecurity的值（该值将作为请求头部进行验证，服务器用其来反爬虫）  <br/>
     */
    protected String getSFSecurity(String deviceToken) {
        String nonce = UUID.randomUUID().toString();
        String timestamp = System.currentTimeMillis() + "";
        String sign = SecurityUtils.getMD5Str(nonce, timestamp, deviceToken, salt);
        return "nonce=" + nonce + "&timestamp=" + timestamp + "&devicetoken=" + deviceToken
                + "&sign=" + sign.toUpperCase();
    }

    /**
     * 获取小说章节列表信息
     */
    public String getUserAgent() {
        if (userAgent == null) {
            return DEFAULT_USER_AGENT;
        }
        return userAgent;
    }

    public ChapListJson getChapListJson(long novelId) throws IOException {
        Connection connection = getConnection(API + "/novels/" + novelId + "/dirs")
                .headers(getHeaders());
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
        Connection connection = getConnection(API + "/Chaps/" + chapId)
                .headers(getHeaders())
                .header("cookie", getCookieValue())
                .header("SFSecurity", getSFSecurity(DEFAULT_DEVICE_TOKEN))
                .header("User-Agent", DEFAULT_USER_AGENT)
                .data("chapsId", chapId + "")
                .data("expand", "content,chatlines,tsukkomi,needFireMoney,originNeedFireMoney");
        if (timeout > 0) {
            connection.timeout(timeout);
        }
        String json = connection.get().body().text();
        return getGson().fromJson(json, ChapContentJson.class);
    }

    /**
     * 模拟登录功能，登录之后，会保存相应的cookies
     */
    public boolean login(String username, String password) {
        this.username = username;
        this.password = password;
        return login();
    }

    public boolean login() {
        System.out.println("[INFO] Attempting to sign in, current loginStatus: " + loginStatus.name());
        if (loginStatus != LoginStatus.UnLogin) {
            loginOut();
        }
        Connection connection = getConnection(API + "/sessions")
                .headers(getHeaders())
                .header("SFSecurity", getSFSecurity(DEFAULT_DEVICE_TOKEN))
                .data("userName", username)
                .data("passWord", password)
                .method(Connection.Method.POST);
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
        if (loginJson.status.httpCode != SUCCESS_CODE) {
            return false;
        }
        cookies.put(SFCommunity, response.cookie(SFCommunity));
        cookies.put(sessionAPP, response.cookie(sessionAPP));
        loginStatus = LoginStatus.Login;
        return true;
    }

    /**
     * 登出账号: 清空cookies
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
        if (cookies.containsKey(sessionAPP)) {
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

    /**
     * 获取账号信息
     */
    public AccountJson getAccountJson() throws IOException {
        if (loginStatus == LoginStatus.UnLogin) {
            throw new IllegalStateException("账号未登录!");
        }
        Connection connection = getConnection(API + "/user")
                .headers(getHeaders())
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
                .headers(getHeaders())
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
