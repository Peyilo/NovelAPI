package org.anvei.novel.api.sfacg;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import static org.anvei.novel.api.sfacg.ChapListJson.Status;

public class NovelHomeJson {

    public Status   status;

    public Data     data;

    public static class Data {
        public String novelName;
        public String authorName;
        public int authorId;
        public int novelId;
        public int categoryId;
        public int typeId;
        public String novelCover;
        public int charCount;
        public boolean isSensitive;
        public int markCount;
        public String bgBanner;
        public int viewTimes;
        public String signStatus;
        public Expand expand;
        public float point;         // 评分
        public String addTime;
        public String lastUpdateTime;
        public boolean allowDown;
        public boolean isFinish;
    }

    public static class Expand {
        public String typeName;
        public List<Tag> tags;
        public String discountExpireDate;
        public List<Flag> homeFlag;
        public String intro;
        public List<SysTag> sysTags;
        public float discount;
        public int originTotalNeedFireMoney;
        public int ticket;
        @SerializedName("rankinglist")
        public RankingList rankingList;
        public int bonurange;
        public List<Award> essayAwards;
        public int bonus;
        public int ticketRange;
        public String bigNovelCover;
        public String preOrderInfo;
        public String signLevel;
        public int pointCount;
        public String bigBgBanner;
        public int fav;
        public int totalNeedFireMoney;
        public Chapter latestChapter;
        public boolean canUnlockWithId;
    }

    public static class Flag {
    }

    public static class Tag {
    }

    public static class SysTag {
        public String tagName;
        public int sysTagId;
    }

    public static class RankingList {
        public String desc;
        public int type;
        public int dateRange;
    }

    public static class Award {
    }

    public static class Chapter {
        public String title;
        public int chapId;
        public String addTime;
    }
}
