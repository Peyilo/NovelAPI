package org.anvei.novel.utils;

import java.io.*;

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

    public static boolean writeFile(File file, InputStream is) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            while (is.read(bytes) != -1) {
                os.write(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 关闭输出流
        if (os != null) {
            try {
                os.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
