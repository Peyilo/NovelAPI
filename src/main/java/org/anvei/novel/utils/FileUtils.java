package org.anvei.novel.utils;

import java.io.*;

public class FileUtils {

    /**
     * 在parent文件夹下，创建一个不重名的文件，文件名name
     */
    public static File createFile(File parent, String name) throws IOException {
        File file;
        if (parent != null) {
            if (parent.isFile()) {
                throw new IllegalArgumentException("parent must be a directory");
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

    /**
     * 根据传入的File对象获取一个不重名的File对象（如果重名就在后面加上数字） <br/>
     * 得到的文件一定是还未创建的File对象
     */
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
            } else {
                file = new File(parent, name +  " (" + count + ")");
            }
            count++;
        }
        return file;
    }

    /**
     * 向指定文件写入流
     */
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

    /**
     * 复制文件
     */
    public static boolean copy(File target, File source) {
        try {
            FileInputStream is = new FileInputStream(source);
            return writeFile(target, is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

}
