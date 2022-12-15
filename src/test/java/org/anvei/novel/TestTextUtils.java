package org.anvei.novel;

import org.junit.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TestTextUtils {

    @Test
    public void test() {
        String encode = URLEncoder.encode("来自深渊", StandardCharsets.UTF_8);
        System.out.println(encode);
    }
}
