package org.anvei.novel.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtils {

    public static String getMD5Str(String... str) {
        return getDigestStr("MD5", str);    // 数组长度16，即16 * 8 = 128位加密
    }

    public static String getSHA256(String... str) {
        return getDigestStr("SHA-256", str);// 数组长度32，即32 * 8 = 256位加密
    }

    public static String getDigestStr(String algorithm, String... str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance(algorithm);
            for (String s : str) {
                md5.update(s.getBytes());
            }
            byte[] bytes = md5.digest();
            return getBytesHexStr(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getBytesHexStr(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            int temp = b & 0xff;
            if (temp < 16) {
                stringBuilder.append("0");
            }
            stringBuilder.append(Integer.toHexString(temp));
        }
        return stringBuilder.toString();
    }
}
