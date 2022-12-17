package org.anvei.novel.api.hbooker;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import static org.anvei.novel.api.hbooker.DivisionInfoJson.ScrollChest;

public class ChapListInfoJson {

    public int code;

    public Data data;

    @SerializedName("scroll_chests")
    public List<ScrollChest> scrollChests;

    public static class Data {
        @SerializedName("chapter_list")
        public List<Chapter> chapterList;
        @SerializedName("max_update_time")
        public long maxUpdateTime;
        @SerializedName("max_chapter_index")
        public int maxChapterIndex;
    }

    public static class Chapter {
        @SerializedName("chapter_id")
        public long chapterId;
        @SerializedName("chapter_index")
        public int chapterIndex;
        @SerializedName("chapter_title")
        public String chapterTitle;
        @SerializedName("word_count")
        public int wordCount;
        @SerializedName("tsukkomi_amount")
        public int tsukkomiAmount;
        @SerializedName("is_paid")
        public String isPaid;
        public String mtime;
        @SerializedName("is_valid")
        public int isValid;
        @SerializedName("auth_access")
        public int authAccess;
    }
}
