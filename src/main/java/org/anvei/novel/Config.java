package org.anvei.novel;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Config {

    private static final String APP_CONFIG_FILE_PATH = "src/main/resources/app.properties";
    private static final String KEY_HBOOKER_CONFIG_FILE_PATH = "hbooker_config_file_path";
    private static final String KEY_SFACG_CONFIG_FILE_PATH = "sfacg_config_file_path";

    public static String HBOOKER_CONFIG_FILE_PATH;

    public static String SFACG_CONFIG_FILE_PATH;

    public static void initAppConfig() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(APP_CONFIG_FILE_PATH, StandardCharsets.UTF_8));
        String s1 = properties.getProperty(KEY_HBOOKER_CONFIG_FILE_PATH);
        if (s1 != null && !s1.equals("")) {
            HBOOKER_CONFIG_FILE_PATH = s1;
        }
        String s2 = properties.getProperty(KEY_SFACG_CONFIG_FILE_PATH);
        if (s2 != null && !s2.equals("")) {
            SFACG_CONFIG_FILE_PATH = s2;
        }
    }

    public static final String KEY_HBOOKER_ACCOUNT = "hbooker_account";
    public static final String KEY_HBOOKER_LOGIN_TOKEN = "hbooker_login_token";
    public static final String KEY_HBOOKER_APP_VERSION = "hbooker_app_version";
    public static final String KEY_HBOOKER_DEVICE_TOKEN = "hbooker_device_token";

    public static String getHbookerConfig(String key) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(HBOOKER_CONFIG_FILE_PATH, StandardCharsets.UTF_8));
        return properties.getProperty(key);
    }

    public static final String KEY_SFACG_USERNAME = "sfacg_username";
    public static final String KEY_SFACG_PASSWORD = "sfacg_password";

    public static String getSfacgConfig(String key) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(SFACG_CONFIG_FILE_PATH, StandardCharsets.UTF_8));
        return properties.getProperty(key);
    }

}
