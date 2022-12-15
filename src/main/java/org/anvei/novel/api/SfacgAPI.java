package org.anvei.novel.api;

import org.anvei.novel.api.sfacg.AccountJson;
import org.anvei.novel.api.sfacg.ChapContentJson;
import org.anvei.novel.api.sfacg.ChapListJson;
import org.anvei.novel.api.sfacg.LoginJson;
import org.anvei.novel.utils.FileUtils;
import org.jsoup.Connection;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.anvei.novel.utils.NetUtils.getConnection;
import static org.anvei.novel.utils.TextUtils.getGson;

public class SfacgAPI {

    private static final String API = "https://api.sfacg.com";              // SFACG的iOS、Android查询API
    private static final String WeChatAPI = "https://minipapi.sfacg.com/pas/mpapi";   // 微信小程序API

    private static final String WeChatVipAPI = "https://m.sfacg.com";

    private final Map<String, String> cookies = new HashMap<>();

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
     * 获取小说章节列表信息
     */
    public ChapListJson getChapListJson(long novelId) throws IOException {
        Connection connection = getConnection(API + "/novels/" + novelId + "/dirs")
                .header("Authorization", "Basic YXBpdXNlcjozcyMxLXl0NmUqQWN2QHFlcg==");
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
                .header("sf-minip-info", "minip_novel/1.0.72(iOS;16.1)/wxmp")
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
     * 该函数只能保存图片形式的vip章节，而且图片非常模糊
     */
    public boolean saveVipChapPic(long novelId, long chapId, File target) {
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

    private static final String SFCommunity = ".SFCommunity";
    private static final String sessionMinip = "session_Minip1";

    /**
     * 模拟登录功能，登录之后，会保存相应的cookies
     */
    public boolean login() {
        Connection connection = getConnection(WeChatAPI + "/sessions")
                .header("sf-minip-info", "minip_novel/1.0.72(iOS;16.1)/wxmp")
                .data("username", username)
                .data("password", password)
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
        if (loginJson.status.httpCode != 200) {
            return false;
        }
        cookies.put(SFCommunity, response.cookie(SFCommunity));
        cookies.put(sessionMinip, response.cookie(sessionMinip));
        return true;
    }

    public boolean login(String username, String password) {
        this.username = username;
        this.password = password;
        cookies.clear();
        return login();
    }

    private String getCookieValue() {
        StringBuilder stringBuilder = new StringBuilder();
        if (cookies.containsKey(SFCommunity)) {
            stringBuilder.append(SFCommunity)
                    .append("=")
                    .append(cookies.get(SFCommunity))
                    .append(";");
        }
        if (cookies.containsKey(sessionMinip)) {
            stringBuilder.append(sessionMinip)
                    .append("=")
                    .append(cookies.get(sessionMinip))
                    .append(";");
        }
        return stringBuilder.toString();
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public AccountJson getAccountJson() throws IOException {
        Connection connection = getConnection(WeChatAPI + "/user")
                .header("sf-minip-info", "minip_novel/1.0.72(iOS;16.1)/wxmp")
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

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
