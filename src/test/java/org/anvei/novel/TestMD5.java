package org.anvei.novel;

import org.anvei.novel.utils.Security;
import org.junit.Test;

public class TestMD5 {

    @Test
    public void test1() {
        String md5Str = Security.getMD5Str("Hello");
        assert md5Str != null;
        System.out.println("length = " + md5Str.length());
        System.out.println(md5Str);
    }

    /*
    SFSecurity: nonce=b5f7ee19-95ad-4700-90ea-356cae913007
        &timestamp=1670818946877
        &devicetoken=6F9A9878-637A-4A24-BB42-4B589E29C9F3
        &sign=201785D699B7FF8511E642883593F8AF
    */
    @Test
    public void test2() {
        String timestamp = "1670818946877";
        String devicetoken = "6F9A9878637A4A24BB424B589E29C9F3";
        String sign = "201785D699B7FF8511E642883593F8AF";
        System.out.println(Security.getMD5Str(timestamp, devicetoken));
    }

}
