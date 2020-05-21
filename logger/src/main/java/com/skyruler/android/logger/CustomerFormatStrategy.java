package com.skyruler.android.logger;

import android.text.TextUtils;

import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;
import com.orhanobut.logger.LogcatLogStrategy;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;


/**
 * @author: Rony
 * @email: luojun@skyruler.cn
 * @date: Created 2019/2/13 16:37
 */

public class CustomerFormatStrategy implements FormatStrategy {

    /**
     * Android's max limit for a log entry is ~4076 bytes,
     * so 4000 bytes is used as chunk size since default charset
     * is UTF-8
     */
    private static final int CHUNK_SIZE = 4000;

    @NonNull
    private final LogStrategy logStrategy;
    @Nullable
    private final String tag;

    private CustomerFormatStrategy(@NonNull Builder builder) {
        Utils.checkNotNull(builder);

        logStrategy = builder.logStrategy;
        tag = builder.tag;
    }

    @NonNull
    public static CustomerFormatStrategy.Builder newBuilder() {
        return new Builder();
    }

    @Override
    public void log(int priority, @Nullable String onceOnlyTag, @NonNull String message) {
        Utils.checkNotNull(message);
        String formatTag = formatTag(onceOnlyTag);

        //get bytes of message with system's default charset (which is UTF-8 for Android)
        byte[] bytes = message.getBytes();
        int length = bytes.length;
        if (length <= CHUNK_SIZE) {
            logContent(priority, formatTag, message);
            return;
        }
        for (int i = 0; i < length; i += CHUNK_SIZE) {
            int count = Math.min(length - i, CHUNK_SIZE);
            //create a new String with system's default charset (which is UTF-8 for Android)
            logContent(priority, formatTag, new String(bytes, i, count));
        }
    }


    private void logContent(int logType, @Nullable String tag, @NonNull String chunk) {
        Utils.checkNotNull(chunk);

        String[] lines = chunk.split(System.getProperty("line.separator"));
        for (String line : lines) {
            logChunk(logType, tag, line);
        }
    }

    private void logChunk(int priority, @Nullable String tag, @NonNull String chunk) {
        Utils.checkNotNull(chunk);

        logStrategy.log(priority, tag, chunk);
    }


    @Nullable
    private String formatTag(@Nullable String tag) {
        if (!TextUtils.isEmpty(tag) && !Utils.equals(this.tag, tag)) {
            return this.tag + ":" + tag + ":" + Thread.currentThread().getId();
        }
        return this.tag + ":" + Thread.currentThread().getId();
    }

    public static class Builder {
        @Nullable
        LogStrategy logStrategy;
        @Nullable
        String tag = "LOGGER";

        private Builder() {
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
        public CustomerFormatStrategy build() {
            if (logStrategy == null) {
                logStrategy = new LogcatLogStrategy();
            }
            return new CustomerFormatStrategy(this);
        }
    }
}
