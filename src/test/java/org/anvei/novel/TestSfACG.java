package org.anvei.novel;

import org.anvei.novel.sfacg.gson.beans.Chapter;
import org.anvei.novel.sfacg.gson.beans.NovelDirs;
import org.anvei.novel.sfacg.gson.beans.Volume;
import org.jsoup.Jsoup;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.anvei.novel.sfacg.SfACG.getNovelDirs;

public class TestSfACG {

    @Test
    public void test1() throws IOException {
        NovelDirs novelDirs = getNovelDirs(591785);
        List<Volume> volumeList = novelDirs.getVolumeList();
        for (Volume volume : volumeList) {
            List<Chapter> chapterList = volume.chapterList;
            System.out.println(volume.title);
            for (Chapter chapter : chapterList) {
                System.out.println("\t" + chapter.title);
            }
        }
    }

    private final Map<String, String> headers = new HashMap<>();

    {
        headers.put("User-Agent", "boluobao/4.9.16(iOS;16.1)/appStore");
        headers.put("Authorization", "Basic YXBpdXNlcjozcyMxLXl0NmUqQWN2QHFlcg==");
        headers.put("Host", "api.sfacg.com");
        headers.put("SFSecurity", "nonce=b5f7ee19-95ad-4700-90ea-356cae913007&" +
                "timestamp=" + System.currentTimeMillis() + "&" +
                "sign=201785D699B7FF8511E642883593F8AF");
    }

    @Test
    public void test2() throws IOException {
        System.out.println(Jsoup.connect("https://api.sfacg.com/Chaps/7006709?chapId=7006709&expand=content")
                .headers(headers)
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                .get());
    }

}
