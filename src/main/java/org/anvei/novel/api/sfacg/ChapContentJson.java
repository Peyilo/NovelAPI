package org.anvei.novel.api.sfacg;

import java.util.List;

public class ChapContentJson {

    public Status   status;
    public Data     data;

    public static class Status {
        public int msgType;
        public int errorCode;
        public int httpCode;
    }

    public static class Data {
        public int      chapOrder;
        public boolean  isRubbish;
        public int      charCount;
        public String   ntitle;
        public String   addTime;
        public String   updateTime;
        public String   title;
        public boolean  isVip;
        public int      novelId;
        public Expand   expand;
        public int      sno;
        public int      rowNum;
        public int      chapId;
        public int      volumeId;
        public int      auditStatus;
    }

    public static class Expand {
        public int                  originNeedFireMoney;
        public boolean              isBranch;
        public int                  needFireMoney;
        public List<TsukkomiType>   tsukkomi;
        public String               content;
    }

    public static class TsukkomiType {
        public int count;
        public int row;
    }

    public String getContent() {
        return data.expand.content;
    }

    public boolean isVip() {
        return data.isVip;
    }

    public String getTitle() {
        return data.title;
    }

    public int getChapOrder() {
        return data.chapOrder;
    }

    public int getCharCount() {
        return data.charCount;
    }

    public int getHttpCode() {
        return status.httpCode;
    }

}
