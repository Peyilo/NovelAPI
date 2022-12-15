package org.anvei.novel.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TextUtils {

    private volatile static Gson gson;

    public static Gson getGson() {
        if (gson == null) {
            synchronized (Gson.class) {
                if (gson == null) {
                    gson = new GsonBuilder()
                            .setPrettyPrinting()            // 格式化输出json字符串
                            .serializeNulls()               // 输出json字符串时，不忽略为null的字段
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

}
