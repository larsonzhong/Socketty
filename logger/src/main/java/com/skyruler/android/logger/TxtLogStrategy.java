package com.skyruler.android.logger;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.orhanobut.logger.LogStrategy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;


public class TxtLogStrategy implements LogStrategy {
    @NonNull
    private final Handler handler;

    public TxtLogStrategy(@NonNull Handler handler) {
        this.handler = handler;
    }

    @Override
    public void log(int priority, @Nullable String tag, @NonNull String message) {
        Utils.checkNotNull(message);

        // do nothing on the calling thread, simply pass the tag/msg to the background thread
        handler.sendMessage(handler.obtainMessage(priority, message));
    }


    static class WriteHandler extends Handler {

        @NonNull
        private final String folder;
        @NonNull
        private final String fileName;
        private final int maxFileSize;
        private final ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>() {
            @Override
            protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            }
        };

        WriteHandler(@NonNull Looper looper, @NonNull String folder, @NonNull String fileName, int maxFileSize) {
            super(Utils.checkNotNull(looper));
            this.folder = Utils.checkNotNull(folder);
            this.fileName = Utils.checkNotNull(fileName);
            this.maxFileSize = maxFileSize;
        }

        @SuppressWarnings("checkstyle:emptyblock")
        @Override
        public void handleMessage(@NonNull Message msg) {
            String content = (String) msg.obj;

            FileWriter fileWriter = null;
            File logFile = getLogFile(folder, fileName);

            try {
                fileWriter = new FileWriter(logFile, true);

                writeLog(fileWriter, content);

                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                if (fileWriter != null) {
                    try {
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (IOException e1) { /* fail silently */ }
                }
            }
        }

        /**
         * This is always called on a single background thread.
         * Implementing classes must ONLY write to the fileWriter and nothing more.
         * The abstract class takes care of everything else including close the stream and catching IOException
         *
         * @param fileWriter an instance of FileWriter already initialised to the correct file
         */
        private void writeLog(@NonNull FileWriter fileWriter, @NonNull String content) throws IOException {
            Utils.checkNotNull(fileWriter);
            Utils.checkNotNull(content);

            fileWriter.append(content);
        }

        private File getLogFile(@NonNull String folderName, @NonNull String fileName) {
            Utils.checkNotNull(folderName);
            Utils.checkNotNull(fileName);

            File folder = new File(folderName);
            if (!folder.exists()) {
                //TODO: What if folder is not created, what happens then?
                folder.mkdirs();
            }

            File file = new File(folder, fileName);
            if (file.exists()) {
                if (file.length() >= maxFileSize) {
                    String path = file.getParent();
                    final String copyTimeString = dateFormat.get().format(new Date());
                    final File backupFile = new File(path, "log_" + copyTimeString + ".txt");
                    if (file.renameTo(backupFile)) {
                        Observable.empty().subscribeOn(Schedulers.io())
                                .doOnComplete(new Action() {
                                    @Override
                                    public void run() {
                                        String logZipName = backupFile.getParent() + File.separatorChar + "log_" + copyTimeString + ".zip";
                                        try {
                                            Utils.zipFile(logZipName, backupFile, true);
                                            backupFile.delete();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).subscribe();
                    }
                    file = new File(folder, fileName);
                }
            }

            return file;
        }
    }
}
