package org.anvei.novel.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class NetUtils {

    public static Connection getConnection(String url) {
        return Jsoup.connect(url)
                .ignoreContentType(true)
                .ignoreHttpErrors(true);
    }

}
