package org.anvei.novel;

import java.io.IOException;

public class NovelAPI {

    public static void main(String[] args) {
        try {
            Config.initAppConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("欢迎使用NovelAPI!");
    }

}
