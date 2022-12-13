package org.anvei.novel.sfacg.gson.beans;

import java.util.List;

public class Data {

    public int novelId;

    public String lastUpdateTime;

    public List<Volume> volumeList;

    @Override
    public String toString() {
        return "Data{" +
                "novelId=" + novelId +
                ", lastUpdateTime='" + lastUpdateTime + '\'' +
                ", volumeList=" + volumeList +
                '}';
    }
}
