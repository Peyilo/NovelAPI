package org.anvei.novel.api.hbooker;

import com.google.gson.annotations.SerializedName;

public class ChapterInfoJson {

    public int code;

    public Data data;

    public static class Data {
        @SerializedName("chapter_info")
        public ChapterInfo chapterInfo;
    }

    @SerializedName("scroll_chests")
    public DivisionInfoJson.ScrollChest scrollChest;

    public static class ChapterInfo {
        @SerializedName("chapter_id")
        public long chapterId;
        @SerializedName("book_id")
        public long bookId;
        @SerializedName("division_id")
        public long divisionId;
        @SerializedName("unit_hlb")
        public int unitHlb;
        @SerializedName("chapter_index")
        public int chapterIndex;
        @SerializedName("chapter_title")
        public String chapterTitle;
        @SerializedName("author_say")
        public String authorSay;
        @SerializedName("word_count")
        public int wordCount;
        public String discount;
        @SerializedName("is_paid")
        public String isPaid;
        @SerializedName("auth_access")
        public int authAccess;
        @SerializedName("buy_amount")
        public int buyAmount;
        @SerializedName("tsukkomi_amount")
        public int tsukkomiAmount;
        @SerializedName("total_hlb")
        public int totalHlb;
        public String uptime;
        public String mtime;
        public String ctime;
        @SerializedName("recommend_book_info")
        public String recommendBookInfo;
        @SerializedName("base_status")
        public int baseStatus;
        @SerializedName("txt_content")
        public String txtContent;
    }
}
