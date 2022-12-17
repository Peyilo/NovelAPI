package org.anvei.novel.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {

    private volatile static Gson gson;

    public static Gson getGson() {
        if (gson == null) {
            synchronized (Gson.class) {
                if (gson == null) {
                    gson = new GsonBuilder()
                            .setPrettyPrinting()            // 格式化输出json字符串
                            .serializeNulls()               // 输出json字符串时，不忽略为null的字段
                            //.registerTypeAdapter(List.class, null)
                            .create();
                }
            }
        }
        return gson;
    }

    public static String toPrettyFormat(String json) {
        JsonObject object = JsonParser.parseString(json).getAsJsonObject();
        return getGson().toJson(object);
    }

    public static String toPrettyFormat(Object o) {
        return getGson().toJson(o);
    }

    public static String escapeUTF8(String str) {
        // 将字符串里的utf-8形式字符串替换成相应的字符
        if (str.contains("\\u")) {
            StringBuilder stringBuilder = new StringBuilder();
            Matcher m = Pattern.compile("\\\\u([0-9A-Fa-f]{4})").matcher(str);
            while (m.find()) {
                try {
                    int cp = Integer.parseInt(m.group(1), 16);
                    m.appendReplacement(stringBuilder, "");
                    stringBuilder.appendCodePoint(cp);
                } catch (NumberFormatException ignored) {
                }
            }
            m.appendTail(stringBuilder);
            str = stringBuilder.toString();
        }
        //str = str.replace("\\/", "\\");
        return str;
    }

}
