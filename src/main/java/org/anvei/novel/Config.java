package org.anvei.novel;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Config {

    private static final String APP_CONFIG_FILE_PATH = "app.properties";
    public static final String KEY_HBOOKER_CONFIG_FILE_PATH = "hbooker_config_file_path";
    public static final String KEY_SFACG_CONFIG_FILE_PATH = "sfacg_config_file_path";

    public static String HBOOKER_CONFIG_FILE_PATH;

    public static String SFACG_CONFIG_FILE_PATH;

    private static Properties appProperties;

    public static void initAppConfig() throws IOException {
        appProperties = new Properties();
        InputStream is = Config.class.getClassLoader().getResourceAsStream(APP_CONFIG_FILE_PATH);
        if (is == null) {
            throw new IllegalStateException("Unknown state");
        }
        appProperties.load(is);
        is.close();
        String s1 = appProperties.getProperty(KEY_HBOOKER_CONFIG_FILE_PATH);
        if (s1 != null && !s1.equals("")) {
            HBOOKER_CONFIG_FILE_PATH = s1;
        }
        String s2 = appProperties.getProperty(KEY_SFACG_CONFIG_FILE_PATH);
        if (s2 != null && !s2.equals("")) {
            SFACG_CONFIG_FILE_PATH = s2;
        }
    }

    // 配置app.properties
    public static void setAppConfig(String key, String value) {
        if (isAppConfigKey(key)) {
            appProperties.setProperty(key, value);
            if (key.equals(KEY_HBOOKER_CONFIG_FILE_PATH)) {
                setHbookerConfigFilePath(value);
            } else if (key.equals(KEY_SFACG_CONFIG_FILE_PATH)) {
                setSfacgConfigFilePath(value);
            }
        }
    }

    public static String getHbookerConfigFilePath() {
        return HBOOKER_CONFIG_FILE_PATH;
    }

    public static void setHbookerConfigFilePath(String hbookerConfigFilePath) {
        HBOOKER_CONFIG_FILE_PATH = hbookerConfigFilePath;
    }

    public static String getSfacgConfigFilePath() {
        return SFACG_CONFIG_FILE_PATH;
    }

    public static void setSfacgConfigFilePath(String sfacgConfigFilePath) {
        SFACG_CONFIG_FILE_PATH = sfacgConfigFilePath;
    }

    private static boolean isAppConfigKey(String key) {
        return key.equals(KEY_HBOOKER_CONFIG_FILE_PATH) || key.equals(KEY_SFACG_CONFIG_FILE_PATH);
    }

    public static boolean saveAppConfig() {
        URL url = Config.class.getResource(APP_CONFIG_FILE_PATH);
        if (url == null) {
            return false;
        }
        try {
            FileOutputStream os = new FileOutputStream(new File(url.toURI()));
            appProperties.store(os, "");
            return true;
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return false;
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
