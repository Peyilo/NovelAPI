package org.anvei.novel;

public class NovelAPI {

    public NovelAPI() {
        initApi();
    }

    private void initApi() {
        if (Config.getInitConfigTask() != null) {
            Config.getInitConfigTask().run();
        }
    }

    public void destroy() {
        Config.setInitConfigTask(null);
    }

}
