package org.anvei.novel.api.hbooker;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

public class HbookerSecurity {

    public static final String decryptKey = "zG2nSeEfSHfvTCHy5LCcqtBbQehKNLXn";

    /**
     * 解密
     */
    public static String decrypt(String src) {
        return decrypt(src.getBytes());
    }

    public static String decrypt(byte[] src) {
        return decrypt(src, decryptKey);
    }

    public static String decrypt(String src, String key) {
        return decrypt(src.getBytes(), key);
    }

    public static String decrypt(byte[] src, String key) {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            sha256.update(key.getBytes());
            byte[] keyBytes = sha256.digest();
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] res = cipher.doFinal(Base64.getDecoder().decode(src));

            return new String(res).trim().replace("\\\\u", "\\u");
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
