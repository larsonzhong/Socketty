package com.skyruler.android.logger;

import android.os.SystemClock;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

import static com.orhanobut.logger.Logger.ASSERT;
import static com.orhanobut.logger.Logger.DEBUG;
import static com.orhanobut.logger.Logger.ERROR;
import static com.orhanobut.logger.Logger.INFO;
import static com.orhanobut.logger.Logger.VERBOSE;
import static com.orhanobut.logger.Logger.WARN;

public class Utils {
    private final static int MAX_BUFFER_SIZE = 10240;

    static String logLevel(int value) {
        switch (value) {
            case VERBOSE:
                return "V";
            case DEBUG:
                return "D";
            case INFO:
                return "I";
            case WARN:
                return "W";
            case ERROR:
                return "E";
            case ASSERT:
                return "A";
            default:
                return "U";
        }
    }

    /**
     * Returns true if a and b are equal, including if they are both null.
     * <p><i>Note: In platform versions 1.1 and earlier, this method only worked well if
     * both the arguments were instances of String.</i></p>
     *
     * @param a first CharSequence to check
     * @param b second CharSequence to check
     * @return true if a and b are equal
     * <p>
     * NOTE: Logic slightly change due to strict policy on CI -
     * "Inner assignments should be avoided"
     */
    static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) {
            return true;
        }
        if (a != null && b != null) {
            int length = a.length();
            if (length == b.length()) {
                if (a instanceof String && b instanceof String) {
                    return a.equals(b);
                } else {
                    for (int i = 0; i < length; i++) {
                        if (a.charAt(i) != b.charAt(i)) {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @NonNull
    static <T> T checkNotNull(@Nullable final T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    public static void zipFile(String zipFileName, File file, boolean nowait) throws Exception {
        String fileName = file.getName();

        ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(
                zipFileName));

        outputStream.putNextEntry(new ZipEntry(fileName));

        FileInputStream inputStream = new FileInputStream(file);
        // 普通压缩文件

        int size;
        byte[] buffer = new byte[MAX_BUFFER_SIZE];
        int count = 0;
        while ((size = inputStream.read(buffer, 0, MAX_BUFFER_SIZE)) != -1) {
            outputStream.write(buffer, 0, size);

            if (!nowait && count % 5 == 0) {
                SystemClock.sleep(10);
            }
            count++;
        }
        inputStream.close();
        outputStream.close();
    }


}
