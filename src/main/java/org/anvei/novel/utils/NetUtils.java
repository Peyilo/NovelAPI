package org.anvei.novel.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class NetUtils {

    public static final String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36";

    public static Connection getConnection(String url) {
        return Jsoup.connect(url)
                .ignoreContentType(true)
                .ignoreHttpErrors(true);
    }

}
