package org.anvei.novel.api.hbooker;

import com.google.gson.annotations.SerializedName;

// 在对一个章节的内容发起请求之前，会根据chapter_id先向服务器请求一个chapter_command值进行验证
public class ChapterCommandJson {

    public int code;

    public Data data;

    public static class Data {
        public String command;
    }

    @SerializedName("scroll_chests")
    public DivisionInfoJson.ScrollChest scrollChest;

}
