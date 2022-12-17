package org.anvei.novel;

import static org.anvei.novel.utils.SecurityUtils.getMD5Str;

import org.junit.Test;

public class TestSecurity {

    public static final String nonce = "ea18d10e-fd71-4cad-92e7-ade0c4691218";
    public static final String timeStamp = "1671097855801";
    public static final String deviceToken = "6F9A9878-637A-4A24-BB42-4B589E29C9F3";
    public static final String sign = "74D7E69366844E0BEA0A5215409092C5";

    @Test
    public void test() {
        System.out.println(getMD5Str(nonce + timeStamp + deviceToken + "td9#Kn_p7vUw"));
    }

    @Test
    public void test2() {
        System.out.println(getMD5Str(nonce + timeStamp + deviceToken + "xw3#a12-x"));
    }

}
