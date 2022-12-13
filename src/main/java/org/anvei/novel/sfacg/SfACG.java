package org.anvei.novel.sfacg;

import com.google.gson.Gson;
import org.anvei.novel.sfacg.gson.beans.NovelDirs;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class SfACG {

    private static final String API = "https://api.sfacg.com";       // SFACG的IOS、Android查询API


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

    // 获取小说章节列表信息
    public static NovelDirs getNovelDirs(int novelId) throws IOException {
        Document document = Jsoup.connect(API + "/novels/" + novelId + "/dirs")
                .header("Authorization", "Basic YXBpdXNlcjozcyMxLXl0NmUqQWN2QHFlcg==")
                .ignoreContentType(true)
                .get();
        String text = document.body().text();       // JSON解析
        return getGson().fromJson(text, NovelDirs.class);
    }

    // 获取章节内容信息
    public static Document getChapDocument(int chapId) throws IOException {
        return null;
    }

}
