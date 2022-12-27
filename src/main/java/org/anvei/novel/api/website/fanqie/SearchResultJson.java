package org.anvei.novel.api.website.fanqie;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResultJson {

    public int code;

    public Data data;

    @SerializedName("log_id")
    public String logId;
    public String message;

    public static class Data {
        @SerializedName("total_count")
        public int totalCount;
        @SerializedName("search_book_data_list")
        public List<BookData> bookDataList;
        @SerializedName("search_author_data_list")
        public List<AuthorData> authorDataList;
    }

    public static class BookData {
        @SerializedName("book_name")
        public String bookName;
        public String author;
        @SerializedName("thumb_uri")
        public String coverUrl;                 // 封面地址
        @SerializedName("book_id")
        public String bookId;
        public String category;                 // 分类
        @SerializedName("book_abstract")
        public String intro;                    // 简介
        @SerializedName("word_count")
        public int wordCount;                   // 字数
        @SerializedName("creation_status")
        public int creationStatus;
        @SerializedName("read_count")
        public int readCount;                   // 在读人数
        @SerializedName("first_chapter_id")
        public String firstChapterId;
        @SerializedName("last_chapter_id")
        public String lastChapterId;
        @SerializedName("last_chapter_title")
        public String lastChapterTitle;
        @SerializedName("last_chapter_time")
        public String lastChapterTime;
    }

    public static class AuthorData {

    }
}
