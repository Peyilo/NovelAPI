package org.anvei.novel.utils;

import java.io.*;

public class FileUtils {

    public static File createFile(File parent, String name) throws IOException {
        File file;
        if (parent != null) {
            if (parent.isFile()) {
                throw new IllegalArgumentException("parent必须是一个文件夹");
            }
            if (!parent.exists()) {
                parent.mkdirs();
            }
            file = new File(parent, name);
        } else {
            file = new File(name);
        }
        file = getFile(file);
        file.createNewFile();
        return file;
    }

    public static File getFile(File file) {
        String parent = file.getParent();
        String name = file.getName();
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
