package reeiss.bonree.crawler.utils;

import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by GodRui on 2018/3/20.
 */

public class Utils {
    public static void e(String tag, String msg) {  //信息太长,分段打印
        //因为String的length是字符数量不是字节数量所以为了防止中文字符过多，
        //  把4*1024的MAX字节打印长度改为2001字符数
        int max_str_length = 2001 - tag.length();
        //大于4000时
        while (msg.length() > max_str_length) {
            Log.e(tag, msg.substring(0, max_str_length));
            msg = msg.substring(max_str_length);
        }
        //剩余部分
        Log.e(tag, msg);
    }


    public static File printStringToFile(StringBuffer str, String fileName, boolean append) {
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        FileWriter writer = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            } /*else {
                file.delete();
                file.createNewFile();
            }*/
            writer = new FileWriter(file, append);
            writer.write(str.toString());
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write("\r\n");
            writer.flush();
            writer.close();
            writer = null;
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void printException(Exception e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(baos));
        String exception = baos.toString();
        Log.e("JerryZhu", "run: " + exception);
    }

    public static File printStringToFile(String title, String content, String dirName, String fileName, boolean append) {
        File file = new File(Environment.getExternalStorageDirectory() + "/" + dirName, fileName);
        if (!file.getParentFile().exists()) {
            boolean mkdirs = file.getParentFile().mkdirs();
        }
        FileWriter writer = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            } /*else {
                file.delete();
                file.createNewFile();
            }*/
            writer = new FileWriter(file, append);
            writer.write(title);
            writer.write("\r\n");
            writer.write(content);
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write("\r\n");
            writer.write("\r\n");
            writer.flush();
            writer.close();
            writer = null;
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
