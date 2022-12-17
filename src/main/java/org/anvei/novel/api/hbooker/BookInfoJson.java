package org.anvei.novel.api.hbooker;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import static org.anvei.novel.api.hbooker.SearchResultJson.ChapInfo;

public class BookInfoJson {

    public int  code;
    public Data data;

    public static class Data {
        @SerializedName("book_info")
        public BookInfo         bookInfo;                   // 小说详细信息
        @SerializedName("is_inshelf")
        public int              isInShelf;
        @SerializedName("is_buy")
        public int              isBuy;
        @SerializedName("up_reader_info")
        public UpReaderInfo     upReaderInfo;               // 作者的阅读账号信息
        @SerializedName("related_list")
        public List<Related>    relatedList;                // 同类作品
        @SerializedName("book_shortage_reommend_list")
        public List<Recommend>  recommendList;              // 推荐列表
    }

    public static class BookInfo {
        @SerializedName("book_id")
        public long bookId;
        @SerializedName("book_name")
        public String bookName;
        public String description;
        @SerializedName("book_src")
        public String bookSrc;
        @SerializedName("category_index")
        public int categoryIndex;
        public String tag;
        @SerializedName("total_word_count")
        public int totalWordCount;
        @SerializedName("up_status")
        public String upStatus;
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

    public static class UpReaderInfo {
        @SerializedName("reader_id")
        public long readerId;
        public String account;
        @SerializedName("reader_name")
        public String readerName;
        @SerializedName("avatar_url")
        public String avatarUrl;
        @SerializedName("avatar_thumb_url")
        public String avatarThumbUrl;
        @SerializedName("base_status")
        public String baseStatus;
        @SerializedName("exp_lv")
        public int expLevel;
        @SerializedName("exp_value")
        public int expValue;
        public int gender;
        @SerializedName("vip_lv")
        public int vipLevel;
        @SerializedName("vip_value")
        public int vipValue;
        @SerializedName("is_author")
        public int isAuthor;
        @SerializedName("is_uploader")
        public int isUploader;
        @SerializedName("is_following")
        public int isFollowing;
        @SerializedName("used_decoration")
        public List<Decoration> usedDecoration;
        @SerializedName("is_in_blacklist")
        public int isInBlackList;
        public String ctime;
    }

    public static class Related {

    }

    public static class Recommend {

    }

    public static class Decoration {

    }
}
