package org.anvei.novel.api.sfacg;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import static org.anvei.novel.api.sfacg.ChapListJson.Status;

public class SearchResultJson {

    public Status   status;
    public Data     data;

    public static class Data {
        public List<Novel>  novels;
        public List<Comic>  comics;
        public List<Album>  albums;
    }

    public static class Novel {
        public String       novelName;
        public String       signStatus;
        public Expand       expand;
        public long         novelId;
        public long         weight;
        public boolean      isSensitive;
        public String       authorName;
        public int          markCount;
        public String       lastUpdateTime;
        public double       point;
        public boolean      isFinish;
        public boolean      allowDown;
        public String       novelCover;
        public long         viewTimes;
        public String       bgBanner;
        public String       addTime;
        @SerializedName("Highlight")
        public List<String> highlight;
        public int          typeId;
        public int          charCount;
        public int          authorId;
        public int          categoryId;
    }

    public static class Expand {
        public String       typeName;
        public List<String> tags;
        public String       intro;
        public List<SysTag> sysTags;
    }

    public static class SysTag {
        public String   tagName;
        public int      sysTagId;
    }

    public static class Comic {

    }

    public static class Album {

    }
}
