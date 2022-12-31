package org.anvei.novel;

import org.anvei.novel.download.DownloadParams;
import org.anvei.novel.download.DownloadTask;
import org.anvei.novel.download.DownloadTasks;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class TestDownloadTask {

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
    public void test1() {
        DownloadTask task = DownloadTasks.getDownloadTask(NovelSource.SfacgAPP);
        DownloadParams params = new DownloadParams("E:\\Text File\\Novel", 400306);
        task.startDownload(params);
        boolean res = task.waitFinished();
        System.out.println(res);
        System.out.println(task.getChapterCount());
        System.out.println(task.getTargetFile());
    }

}
