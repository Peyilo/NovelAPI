package org.anvei.novel;

import org.anvei.novel.api.SfacgAPI;
import org.anvei.novel.api.sfacg.ChapListJson;
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

}
