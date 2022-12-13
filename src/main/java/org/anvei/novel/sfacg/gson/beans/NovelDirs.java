package org.anvei.novel.sfacg.gson.beans;

import java.util.List;

public class NovelDirs {

    public Status status;

    public Data data;

    @Override
    public String toString() {
        return "NovelDirs{" +
                "status=" + status +
                ", data=" + data +
                '}';
    }

    public List<Volume> getVolumeList() {
        if (data == null)
            return null;
        return data.volumeList;
    }
}
