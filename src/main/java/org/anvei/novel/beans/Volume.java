package org.anvei.novel.beans;

import java.util.ArrayList;
import java.util.List;

public class Volume {

    public int volumeOrder;                         // 卷的序号

    public String title;                            // 卷名

    public final List<Chapter> chapterList = new ArrayList<>();     //章节列表

}
