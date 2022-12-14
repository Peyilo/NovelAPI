package org.anvei.novel.api.sfacg;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChapList {

    public Status   status;
    public Data     data;

    public static class Status {
        public int          httpCode;
        public int          errorCode;
        public int          msgType;
        public String       msg;
    }

    public static class Data {
        public int              novelId;
        public String           lastUpdateTime;
        public List<Volume>     volumeList;
    }

    public static class Volume {
        public int              volumeId;
        public String           title;
        public float            sno;
        public List<Chapter>    chapterList;
    }

    public static class Chapter {
        public int      chapId;
        public int      novelId;
        public int      volumeId;
        public int      needFireMoney;
        public int      originNeedFireMoney;
        public int      chapterOriginFireMoney;
        public int      charCount;
        public int      rowNum;
        public int      chapOrder;
        public String   title;
        public String   content;
        public float    sno;
        public boolean  isVip;
        @SerializedName("AddTime")
        public String   addTime;
        public String   updateTime;
        public boolean  canUnlockWithAd;
        public String   nTitle;
        public boolean  isRubbish;
        public int      auditStatus;
    }

    public List<Volume> getVolumeList() {
        if (data == null)
            return null;
        return data.volumeList;
    }

    // chapOrder从1开始
    public Chapter getChapter(int chapOrder) {
        return getChapter(0, chapOrder);
    }

    public Chapter getChapter(int volOrder, int chapOrder) {
        return data.volumeList.get(volOrder).chapterList.get(chapOrder - 1);
    }

    public int getHttpCode() {
        return status.httpCode;
    }
}
