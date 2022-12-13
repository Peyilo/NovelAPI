package org.anvei.novel.sfacg.gson.beans;

import com.google.gson.annotations.SerializedName;

public class Chapter {

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

    @Override
    public String toString() {
        return "Chapter{" +
                "chapId=" + chapId +
                ", novelId=" + novelId +
                ", volumeId=" + volumeId +
                ", needFireMoney=" + needFireMoney +
                ", originNeedFireMoney=" + originNeedFireMoney +
                ", chapterOriginFireMoney=" + chapterOriginFireMoney +
                ", charCount=" + charCount +
                ", rowNum=" + rowNum +
                ", chapOrder=" + chapOrder +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", sno=" + sno +
                ", isVip=" + isVip +
                ", addTime='" + addTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", canUnlockWithAd=" + canUnlockWithAd +
                ", nTitle='" + nTitle + '\'' +
                ", isRubbish=" + isRubbish +
                ", auditStatus=" + auditStatus +
                '}';
    }
}
