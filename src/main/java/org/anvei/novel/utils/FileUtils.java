package org.anvei.novel.utils;

import java.io.File;
import java.io.IOException;

public class FileUtils {

    public static File createFile(File parent, String name) throws IOException {
        if (parent.isFile()) {
            throw new IllegalArgumentException("parent必须是一个文件夹");
        }
        if (!parent.exists()) {
            parent.mkdirs();
        }
        File file = new File(parent, name);
        int index = name.lastIndexOf('.');
        int count = 2;
        while (file.exists()) {
            if (index != -1) {
                file = new File(parent, name.substring(0, index)
                        + " (" + count + ")"
                        + name.substring(index));
            }
            count++;
        }
        file.createNewFile();
        return file;
    }

}
