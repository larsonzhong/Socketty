package com.skyruler.android.logger;

import android.text.TextUtils;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

/**
 * 日志输出 输出到标准Log的同时，写到一个文件方便在非调试模式查看
 *
 * @author: Rony
 * @email: luojun@skyruler.cn
 * @date: Created 2019/2/13 16:32
 */
public final class Log {

    private static String mTag = "Logger";
    private static AtomicBoolean isInitialized = new AtomicBoolean(false);
    private static boolean isReleaseLoggable = false;
    private static boolean isWriteFile = true;

    public static void initialization(String publicTag, boolean releaseLoggable, boolean writeFile, String folder, String fileName) {
        if (!isInitialized.get()) {
            synchronized (Log.class) {
                if (!isInitialized.get()) {
                    isReleaseLoggable = releaseLoggable;
                    if (!TextUtils.isEmpty(publicTag)) {
                        mTag = publicTag;
                    }
                    isWriteFile = writeFile;
                    CustomerFormatStrategy formatStrategy = CustomerFormatStrategy.newBuilder()
                            .tag(mTag)
                            .build();
                    Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
                        @Override
                        public boolean isLoggable(int priority, @Nullable String tag) {
                            return isReleaseLoggable || BuildConfig.DEBUG;
                        }
                    });
                    if (isWriteFile) {
                        TxtFormatStrategy txtFormatStrategy = TxtFormatStrategy.newBuilder()
                                .tag(mTag)
                                .setFolder(folder)
                                .setFileName(fileName)
                                .build();
                        Logger.addLogAdapter(new DiskLogAdapter(txtFormatStrategy));
                    }
                    isInitialized.set(true);
                }
            }
        }
    }

    private static void initialization() {
        initialization(mTag, isReleaseLoggable, isWriteFile, null, null);
    }

    public static void deInitialization() {
        isInitialized.set(false);
        Logger.clearLogAdapters();
    }

    public static void d(@NonNull String message, @Nullable Object... args) {
        initialization();
        Logger.d(message, args);
    }

    public static void d(@NonNull String tag, @NonNull String message, @Nullable Object... args) {
        initialization();
        Logger.t(tag).d(message, args);
    }

    public static void d(@Nullable Object object) {
        initialization();
        Logger.d(object);
    }

    public static void d(@NonNull String tag, @Nullable Object object) {
        initialization();
        Logger.t(tag).d(object);
    }

    public static void d(@Nullable Throwable throwable) {
        initialization();
        Logger.e(throwable, "");
    }

    public static void d(@NonNull String tag, @Nullable Throwable throwable) {
        initialization();
        Logger.t(tag).e(throwable, "");
    }

    public static void e(@NonNull String message, @Nullable Object... args) {
        initialization();
        Logger.e(null, message, args);
    }

    public static void e(@NonNull String tag, @NonNull String message, @Nullable Object... args) {
        initialization();
        Logger.t(tag).e(null, message, args);
    }

    public static void e(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args) {
        initialization();
        Logger.e(throwable, message, args);
    }

    public static void e(@NonNull String tag, @Nullable Throwable throwable, @NonNull String message, @Nullable Object... args) {
        initialization();
        Logger.t(tag).e(throwable, message, args);
    }

    public static void e(@Nullable Throwable throwable) {
        initialization();
        Logger.e(throwable, "");
    }

    public static void e(@NonNull String tag, @Nullable Throwable throwable) {
        initialization();
        Logger.t(tag).e(throwable, "");
    }

    public static void i(@NonNull String message, @Nullable Object... args) {
        initialization();
        Logger.i(message, args);
    }

    public static void i(@NonNull String tag, @NonNull String message, @Nullable Object... args) {
        initialization();
        Logger.t(tag).i(message, args);
    }

    public static void v(@NonNull String message, @Nullable Object... args) {
        initialization();
        Logger.v(message, args);
    }

    public static void v(@NonNull String tag, @NonNull String message, @Nullable Object... args) {
        initialization();
        Logger.t(tag).v(message, args);
    }

    public static void w(@NonNull String message, @Nullable Object... args) {
        initialization();
        Logger.w(message, args);
    }

    public static void w(@NonNull String tag, @NonNull String message, @Nullable Object... args) {
        initialization();
        Logger.t(tag).w(message, args);
    }

    /**
     * Tip: Use this for exceptional situations to log
     * ie: Unexpected errors etc
     */
    public static void wtf(@NonNull String message, @Nullable Object... args) {
        initialization();
        Logger.wtf(message, args);
    }

    public static void wtf(@NonNull String tag, @NonNull String message, @Nullable Object... args) {
        initialization();
        Logger.t(tag).wtf(message, args);
    }

    /**
     * Formats the given json content and print it
     */
    public static void json(@Nullable String json) {
        initialization();
        Logger.json(json);
    }

    public static void json(@NonNull String tag, @Nullable String json) {
        initialization();
        Logger.t(tag).json(json);
    }

    /**
     * Formats the given xml content and print it
     */
    public static void xml(@Nullable String xml) {
        initialization();
        Logger.xml(xml);
    }

    public static void xml(@NonNull String tag, @Nullable String xml) {
        initialization();
        Logger.t(tag).xml(xml);
    }

    /**
     * Tip: Use this for exceptional situations to log
     * ie: Unexpected errors etc
     */
    public static void debug(@NonNull String message, @Nullable Object... args) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        String tag = mTag + ":" + Thread.currentThread().getId();
        String msg = createMessage(message, args);
        android.util.Log.d(tag, msg);
    }

    public static void debug(@NonNull String tag, @NonNull String message, @Nullable Object... args) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        tag = mTag + ":" + tag + ":" + Thread.currentThread().getId();
        String msg = createMessage(message, args);
        android.util.Log.d(tag, msg);
    }

    @NonNull
    private static String createMessage(@NonNull String message, @Nullable Object... args) {
        return args == null || args.length == 0 ? message : String.format(message, args);
    }
}
