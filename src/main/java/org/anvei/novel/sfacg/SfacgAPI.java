package org.anvei.novel.sfacg;

import com.google.gson.Gson;
import org.anvei.novel.sfacg.gson.ChapContent;
import org.anvei.novel.sfacg.gson.ChapList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class SfacgAPI {

    private static final String API = "https://api.sfacg.com";              // SFACG的IOS、Android查询API
    private static final String WeChatAPI = "https://minipapi.sfacg.com";   // 微信小程序API

    private volatile static Gson gson;

    private static Gson getGson() {
        if (gson == null) {
            synchronized (Gson.class) {
                if (gson == null) {
                    gson = new Gson();
                }
            }
        }
        return gson;
    }

    /**
     * 获取小说章节列表信息
     */
    public static ChapList getChapList(long novelId) throws IOException {
        Document document = Jsoup.connect(API + "/novels/" + novelId + "/dirs")
                .header("Authorization", "Basic YXBpdXNlcjozcyMxLXl0NmUqQWN2QHFlcg==")
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .get();
        String text = document.body().text();                   // JSON解析
        return getGson().fromJson(text, ChapList.class);
    }

    /**
     * 利用微信小程序API，获取章节内容信息（iOS、Android端获取章节内容的API被加密过了，暂时无法使用其获取信息）
     */
    public static ChapContent getChapContent(long chapId) throws IOException {
        Document document = Jsoup.connect(WeChatAPI + "/pas/mpapi/Chaps/" + chapId)
                .header("sf-minip-info", "minip_novel/1.0.72(iOS;16.1)/wxmp")
                .data("expand", "content,needFireMoney,originNeedFireMoney,tsukkomi")
                .data("autoOrder", "true")
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .get();
        String text = document.body().text();
        return getGson().fromJson(text, ChapContent.class);
    }

}
