package org.anvei.novel.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtils {

    public static String getMD5Str(String... str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            for (String s : str) {
                md5.update(s.getBytes());
            }
            byte[] bytes = md5.digest();
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : bytes) {
                int temp = b & 0xff;
                if (temp < 16) {
                    stringBuilder.append("0");
                }
                stringBuilder.append(Integer.toHexString(temp));
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}
