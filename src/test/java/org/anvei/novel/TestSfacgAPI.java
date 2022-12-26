package org.anvei.novel;

import org.anvei.novel.api.SfacgAPI;
import org.anvei.novel.api.sfacg.AccountJson;
import org.anvei.novel.api.sfacg.ChapContentJson;
import org.anvei.novel.api.sfacg.ChapListJson;
import org.anvei.novel.utils.TextUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.anvei.novel.api.sfacg.ChapListJson.Chapter;
import static org.anvei.novel.api.sfacg.ChapListJson.Volume;

public class TestSfacgAPI {

    private String username;
    private String password;

    @Before
    public void init() throws IOException {
        Config.initAppConfig();
        Config.setSfacgConfigFilePath(TestUtils.SfacgConfigPath);
        try {
            username = Config.getSfacgConfig(Config.KEY_SFACG_USERNAME);
            password = Config.getSfacgConfig(Config.KEY_SFACG_PASSWORD);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void test1() throws IOException {
        ChapListJson novelDirs = new SfacgAPI().getChapListJson(591785);
        List<Volume> volumeList = novelDirs.getVolumeList();
        for (Volume volume : volumeList) {
            List<Chapter> chapterList = volume.chapterList;
            System.out.println(volume.title);
            for (Chapter chapter : chapterList) {
                System.out.println("\t" + chapter.title);
            }
        }
    }

    @Test
    public void test2() throws IOException {
        SfacgAPI api = new SfacgAPI(username, password);
        boolean login = api.login();
        if (login) {
            System.out.println("登录成功");
            AccountJson accountJson = api.getAccountJson();
            System.out.println(TextUtils.toPrettyFormat(accountJson));
            ChapListJson chapListJson = api.getChapListJson(591785);
            List<ChapListJson.Volume> volumeList = chapListJson.getVolumeList();
            ChapListJson.Volume volume = volumeList.get(volumeList.size() - 1);
            List<ChapListJson.Chapter> chapterList = volume.chapterList;
            ChapListJson.Chapter chapter = chapterList.get(chapterList.size() - 2);
            ChapContentJson chapContentJson = api.getChapContentJson(chapter.chapId);
            System.out.println(TextUtils.toPrettyFormat(chapContentJson));
        }
    }

}
