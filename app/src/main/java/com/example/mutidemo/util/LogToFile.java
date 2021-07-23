package com.example.mutidemo.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 日志写入文件
 */
public class LogToFile {

    /**
     * @param file 待写入的文件
     * @param log  待写入的内容
     */
    public static void write(File file, String log) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
            bufferedWriter.write(log);
            bufferedWriter.newLine();//换行
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String read(File file) {
        StringBuilder builder;
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(file));
            String line = bfr.readLine();
            builder = new StringBuilder();
            while (line != null) {
                builder.append(line);
                builder.append("\n");
                line = bfr.readLine();
            }
            bfr.close();
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
