package org.anvei.novel.api.hbooker;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SecurityCipher {

    private SecretKeySpec secretKeySpec;          // 密钥

    public SecurityCipher(String key) {
        setKey(key);
    }

    public String decrypt(String src) throws Exception {
        return decrypt(src.getBytes());
    }

    /**
     * 密钥key和密文src解密算法：<br/>
     * 密文需要先进行Base64解密，而密钥先进行一次SHA-256摘要算法，之后在使用密文和密钥进行一次AES解密
     */
    public String decrypt(byte[] src) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] res = cipher.doFinal(Base64.getDecoder().decode(src));
        return new String(res);
    }

    /**
     * 加密算法
     */
    public String encryptToString(String str) {
        return new String(encrypt(str));
    }

    public byte[] encrypt(String str) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] bytes = cipher.doFinal(str.getBytes());
            return Base64.getEncoder().encode(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public void setKey(String key) {
        MessageDigest sha256;
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
            sha256.update(key.getBytes());
            byte[] keyBytes = sha256.digest();
            this.secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

}
