package org.anvei.novel.api.hbooker;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResultJson {

    public int      code;
    public Data     data;

    public static class Data {
        @SerializedName("tag_list")
        public List<Tag>    tagList;
        @SerializedName("book_list")
        public List<Book>   bookList;
    }

    public static class Tag {
        @SerializedName("tag_name")
        public String   tagName;
        public int      num;
    }

    public static class Book {
        @SerializedName("book_id")
        public long bookId;
        @SerializedName("book_name")
        public String bookName;
        public String description;
        @SerializedName("category_index")
        public int categoryIndex;
        public String tag;
        @SerializedName("total_word_count")
        public int totalWordCount;
        @SerializedName("update_status")
        public String updateStatus;
        @SerializedName("is_paid")
        public String isPaid;
        public String discount;
        @SerializedName("discount_end_time")
        public String discountEndTime;
        public String cover;
        @SerializedName("author_name")
        public String authorName;
        public String uptime;
        public String newtime;
        @SerializedName("review_amount")
        public int reviewAmount;
        @SerializedName("reward_amount")
        public int rewardAmount;
        @SerializedName("chapter_amount")
        public int chapterAmount;
        @SerializedName("is_original")
        public int isOriginal;
        @SerializedName("total_click")
        public int totalClick;
        @SerializedName("month_click")
        public int monthClick;
        @SerializedName("week_click")
        public int weekClick;
        @SerializedName("month_no_vip_click")
        public int monthNoVipClick;
        @SerializedName("week_no_vip_click")
        public int weekNoVipClick;
        @SerializedName("total_recommend")
        public int totalRecommend;
        @SerializedName("month_recommend")
        public int monthRecommend;
        @SerializedName("week_recommend")
        public int weekRecommend;
        @SerializedName("total_favor")
        public int totalFavor;
        @SerializedName("month_favor")
        public int monthFavor;
        @SerializedName("week_favor")
        public int weekFavor;
        @SerializedName("current_yp")
        public int currentYP;
        @SerializedName("total_yp")
        public int totalYP;
        @SerializedName("current_blade")
        public int currentBlade;
        @SerializedName("total_blade")
        public int totalBlade;
        @SerializedName("week_fans_value")
        public int weekFansValue;
        @SerializedName("month_fans_value")
        public int monthFansValue;
        @SerializedName("total_fans_value")
        public int totalFansValue;
        @SerializedName("last_chapter_info")
        public ChapInfo lastChapterInfo;
        @SerializedName("book_type")
        public String bookType;
        @SerializedName("transverse_cover")
        public String transverseCover;
    }

    public static class ChapInfo {
        @SerializedName("chapter_id")
        public long chapterId;
        @SerializedName("book_id")
        public long bookId;
        @SerializedName("chapter_index")
        public int chapterIndex;
        @SerializedName("chapter_title")
        public String chapterTitle;
        public String uptime;
        public String mtime;
        @SerializedName("recommend_book_info")
        public String recommendBookInfo;
    }

    public static class BookInfo {
        @SerializedName("book_name")
        public String bookName;
        @SerializedName("book_id")
        public long bookId;
    }
}
