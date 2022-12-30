package org.anvei.novel;

import org.anvei.novel.download.DownloadParams;
import org.anvei.novel.download.DownloadTask;
import org.anvei.novel.download.DownloadTasks;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
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
        DownloadParams params = new DownloadParams();
        params.parent = new File("E:\\Text File\\Novel");
        params.novelId = 400306;
        params.maxThreadCount = 10;
        params.fileName = "此刻开始血族女孩逃亡世界";
        params.account = username;
        params.certificate = password;
        DownloadTask task = DownloadTasks.getDownloadTask(NovelSource.SfacgAPP);
        task.startDownload(params);
        boolean res = task.waitFinished();
        System.out.println(res);
        System.out.println(task.getChapterCount());
        System.out.println(task.getTargetFile());
    }

}
