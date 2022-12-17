package org.anvei.novel.api.hbooker;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// 小说分卷信息
public class DivisionInfoJson {

    public int  code;
    public Data data;

    @SerializedName("scroll_chests")
    public List<ScrollChest> scrollChests;

    public static class Data {
        @SerializedName("division_list")
        public List<Division> divisionList;
    }

    public static class ScrollChest {

    }

    public static class Division {
        @SerializedName("division_id")
        public long divisionId;
        @SerializedName("division_index")
        public int divisionIndex;
        @SerializedName("division_name")
        public String divisionName;
        @SerializedName("description")
        public String divisionDescription;
    }
}
