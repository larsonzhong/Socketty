package com.skyruler.android.logger;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

/**
 * @author: Rony
 * @email: luojun@skyruler.cn
 * @date: Created 2019/2/13 17:33
 */

public class TxtFormatStrategy implements FormatStrategy {
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String SEPARATOR = ":";

    private final Date date;
    private final SimpleDateFormat dateFormat;
    @NonNull
    private final LogStrategy logStrategy;
    @Nullable
    private final String tag;

    public TxtFormatStrategy(@NonNull Builder builder) {
        Utils.checkNotNull(builder);

        date = builder.date;
        dateFormat = builder.dateFormat;
        logStrategy = builder.logStrategy;
        tag = builder.tag;
    }

    @NonNull
    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public void log(int priority, @Nullable String onceOnlyTag, @NonNull String message) {
        Utils.checkNotNull(message);
        String tag = formatTag(onceOnlyTag);
        StringBuilder builder = new StringBuilder();
        //time
        date.setTime(System.currentTimeMillis());
        builder.append("[");
        builder.append(dateFormat.format(date));
        builder.append("]");
        builder.append(" ");

        // level
        builder.append(Utils.logLevel(priority));
        builder.append("/");

        // tag
        builder.append(tag);
        builder.append(SEPARATOR);

        // message
        builder.append(" ");
        builder.append(message);

        // new line
        builder.append(NEW_LINE);

        logStrategy.log(priority, tag, builder.toString());
    }

    @Nullable
    private String formatTag(@Nullable String tag) {
        if (!TextUtils.isEmpty(tag) && !Utils.equals(this.tag, tag)) {
            return tag + ":" + Thread.currentThread().getId();
        }
        return this.tag + ":" + Thread.currentThread().getId();
    }

    public static final class Builder {
        /**
         * 10M averages to file
         */
        private static final int MAX_BYTES = 10 * 1024 * 1024;
        Date date;
        SimpleDateFormat dateFormat;
        private String folder;
        private String fileName;
        LogStrategy logStrategy;
        String tag = "LOGGER";

        private Builder() {
        }

        @NonNull
        public Builder date(@Nullable Date val) {
            date = val;
            return this;
        }

        @NonNull
        public Builder dateFormat(@Nullable SimpleDateFormat val) {
            dateFormat = val;
            return this;
        }

        @NonNull
        public Builder setFolder(@Nullable String val) {
            folder = val;
            return this;
        }

        @NonNull
        public Builder setFileName(@Nullable String val) {
            fileName = val;
            return this;
        }

        @NonNull
        public Builder logStrategy(@Nullable LogStrategy val) {
            logStrategy = val;
            return this;
        }

        @NonNull
        public Builder tag(@Nullable String tag) {
            this.tag = tag;
            return this;
        }

        @NonNull
        public TxtFormatStrategy build() {
            if (date == null) {
                date = new Date();
            }

            if (dateFormat == null) {
                dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS", Locale.getDefault());
            }

            if (folder == null) {
                String diskPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                folder = diskPath + File.separatorChar + "SkyRuler";
            }

            if (fileName == null) {
                fileName = "log.txt";
            }

            if (logStrategy == null) {
                HandlerThread ht = new HandlerThread("AndroidFileLogger." + folder);
                ht.start();
                Handler handler = new TxtLogStrategy.WriteHandler(ht.getLooper(), folder, fileName, MAX_BYTES);
                logStrategy = new TxtLogStrategy(handler);
            }
            return new TxtFormatStrategy(this);
        }
    }
}
