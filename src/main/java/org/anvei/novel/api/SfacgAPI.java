package org.anvei.novel.api;

import org.anvei.novel.NovelSource;
import org.anvei.novel.api.sfacg.*;
import org.anvei.novel.utils.FileUtils;
import org.anvei.novel.utils.SecurityUtils;
import org.anvei.novel.utils.TextUtils;
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
    // 最后可以在MD5::MD5(std::string const&)函数下追踪内存时可以获取该盐值
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

    private static File sfacgCache;

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

    // 获取小说主页信息
    public NovelHomeJson getNovelHomeJson(long novelId) throws IOException {
        Connection connection = getConnection(API + "/novels/" + novelId)
                .headers(getHeaders())
                .header("SFSecurity", getSFSecurity(DEFAULT_DEVICE_TOKEN))
                .data("expand", "intro,ticket,fav,typeName,tags,sysTags,pointCount,signLevel,discount,discountExpireDate,totalNeedFireMoney,originTotalNeedFireMoney,latestchapter,bigBgBanner,bigNovelCover,preOrderInfo,canUnlockWithAd,rankinglist,ticketrange,bonurange,bonunum,homeFlag,essayawards");
        if (timeout > 0) {
            connection.timeout(timeout);
        }
        String json = connection.get().body().text();
        // System.out.println(TextUtils.toPrettyFormat(json));
        return getGson().fromJson(json, NovelHomeJson.class);
    }

    // 获取章节列表
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
     * 获取章节内容信息
     */
    public ChapContentJson getChapContentJson(long chapId) throws IOException {
        Connection connection = getConnection(API + "/Chaps/" + chapId)
                .headers(getHeaders())
                .header("cookie", getCookieValue())
                .header("SFSecurity", getSFSecurity(DEFAULT_DEVICE_TOKEN))
                .data("chapsId", chapId + "")
                .data("expand", "content,chatlines,tsukkomi,needFireMoney,originNeedFireMoney");
        if (timeout > 0) {
            connection.timeout(timeout);
        }
        String json = connection.get().body().text();
        return getGson().fromJson(json, ChapContentJson.class);
    }

    public static File getSfacgCache() {
        return sfacgCache;
    }

    // 设置账号缓存文件
    public static void setSfacgCache(File sfacgCache) {
        SfacgAPI.sfacgCache = sfacgCache;
        if (!sfacgCache.exists()) {
            try {
                sfacgCache.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        // 在登录之前
        if (!loginFlag && sfacgCache != null) {
            try {
                String json = FileUtils.readAllString(sfacgCache);
                SfacgCacheJson sfacgCacheJson = getGson().fromJson(json, SfacgCacheJson.class);
                // 如果上次登录过该账号，就使用cookies确认账号登录是否还有效
                if (sfacgCacheJson != null && sfacgCacheJson.account != null &&
                    sfacgCacheJson.password != null) {
                    decrypt(sfacgCacheJson);
                    if (sfacgCacheJson.account.equals(username) && sfacgCacheJson.password.equals(password) &&
                            sfacgCacheJson.cookies.sessionAPP != null && sfacgCacheJson.cookies.SFCommunity != null) {
                        cookies.put(SFCommunity, sfacgCacheJson.cookies.SFCommunity);
                        cookies.put(sessionAPP, sfacgCacheJson.cookies.sessionAPP);
                        loginStatus = LoginStatus.Login;        // 将登录状态置为Login，以免getAccountJson()抛异常
                        AccountJson accountJson = getAccountJson();
                        if (accountJson.status.httpCode == SUCCESS_CODE) {
                            // 利用缓存的cookies登陆成功
                            System.out.println("[INFO] Attempting to sign in by cookies!");
                            return true;
                        }
                        System.out.println("[INFO] Cookies cache 失效!");
                        // 缓存的cookies已经失效，试着重新登录
                        cookies.clear();
                        loginStatus = LoginStatus.UnLogin;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("[INFO] Attempting to sign in by username and password!");
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
        if (sfacgCache != null) {
            // 更新sfacg_cache文件
            SfacgCacheJson sfacgCacheJson = new SfacgCacheJson();
            sfacgCacheJson.cookies = new SfacgCacheJson.Cookies();
            sfacgCacheJson.cookies.SFCommunity = response.cookie(SFCommunity);
            sfacgCacheJson.cookies.sessionAPP = response.cookie(sessionAPP);
            sfacgCacheJson.account = username;
            sfacgCacheJson.password = password;
            encrypt(sfacgCacheJson);
            boolean writeRes = FileUtils.writeFile(TextUtils.toPrettyFormat(sfacgCacheJson), sfacgCache);
            System.out.println("Cookies cache write result: " + writeRes);
        }
        loginStatus = LoginStatus.Login;
        if (loginFlag) {
            loginFlag = false;
        }
        return true;
    }

    private void decrypt(SfacgCacheJson sfacgCacheJson) {

    }

    private void encrypt(SfacgCacheJson sfacgCacheJson) {

    }

    private String decrypt(String str) {
        return str;
    }

    private String encrypt(String str) {
        return str;
    }

    private boolean loginFlag = false;          // 该标志位，必须通过一次账号登录才能将其从true置为false

    /**
     * 登出账号: 清空cookies
     */
    public void loginOut() {
        cookies.clear();
        loginStatus = LoginStatus.UnLogin;
        loginFlag = true;
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

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
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

    @Override
    public NovelSource getNovelSource() {
        return NovelSource.SfacgAPP;
    }

}
