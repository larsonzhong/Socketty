package com.skyruler.middleware.util;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class FileUtil {

    public static void saveStringFile(String path, String content) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(new File(path))));
        bw.write(content);
        bw.flush();
        bw.close();
    }

    public static String readFileString(String path) {
        File jsonFile = new File(path);
        String jsonStr;
        try {
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8);
            int ch = 0;
            StringBuilder sb = new StringBuilder();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
