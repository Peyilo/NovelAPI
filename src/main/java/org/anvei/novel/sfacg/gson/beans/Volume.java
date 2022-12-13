package org.anvei.novel.sfacg.gson.beans;

import java.util.List;

public class Volume {

    public int volumeId;

    public String title;

    public float sno;

    public List<Chapter> chapterList;

    @Override
    public String toString() {
        return "Volume{" +
                "volumeId=" + volumeId +
                ", title='" + title + '\'' +
                ", sno=" + sno +
                ", chapterList=" + chapterList +
                '}';
    }
}
