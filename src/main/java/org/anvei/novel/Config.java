package org.anvei.novel;

import com.google.gson.Gson;

public class Config {

    private static Runnable initConfigTask;

    public static void setInitConfigTask(Runnable initConfigTask) {
        Config.initConfigTask = initConfigTask;
    }

    public static Runnable getInitConfigTask() {
        return initConfigTask;
    }

}
