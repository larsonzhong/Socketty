package com.skyruler.socketclient.connection.socket;

import android.util.Log;

import com.skyruler.socketclient.connection.PacketRouter;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class PacketReader {
    private static final String TAG = "SocketPacketReader";

    private final PacketRouter packetRouter;
    private final ExecutorService mExecutorService;
    private BlockingQueue<byte[]> mQueue;
    private ReadRunnable mReadRunnable;
    private DataRunnable mDataRunnable;
    private Future<Boolean> mReadRunnableFuture;
    private Future<Boolean> mDataRunnableFuture;
    /**
     * is PacketReader shutdown
     */
    private AtomicBoolean bExit;
    private final BaseSocketConnection mConnection;
    private InputStream mInputStream;

    /**
     * Creates a new MessageReader with the special connection
     *
     * @param conn the connection
     */
    public PacketReader(BaseSocketConnection conn, PacketRouter router) {
        mConnection = conn;
        mInputStream = mConnection.getInputStream();
        packetRouter = router;
        mExecutorService = newExecutor();
        init();
    }

    /**
     * initialize the reader inorder to be use.the reader is initialized
     * during the first connection  and when reconnecting due to abruptly disconnection
     */
    private void init() {
        Log.d(TAG, "init SocketPacketReader..");
        bExit = new AtomicBoolean(false);
        mQueue = new LinkedBlockingQueue<>();
        mReadRunnable = new ReadRunnable();
        mDataRunnable = new DataRunnable();
    }


    private ExecutorService newExecutor() {
        //设置核心池大小
        int corePoolSize = 10;
        //设置线程池最大能接受多少线程
        int maxPoolSize = 500;
        //当前线程数大于corePoolSize、小于maximumPoolSize时，超出corePoolSize的线程数的生命周期
        long keepActiveTime = 200;
        //设置时间单位，秒
        TimeUnit timeUnit = TimeUnit.SECONDS;
        //设置线程池缓存队列的排队策略为FIFO，并且指定缓存队列大小为1
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(1);
        //创建ThreadPoolExecutor线程池对象，并初始化该对象的各种参数
        ThreadFactory factory = new ThreadFactory() {
            private final AtomicInteger integer = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "ConnectionManager thread: " + integer.getAndIncrement());
            }
        };
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepActiveTime, timeUnit, workQueue, factory);
    }

    /**
     * Starts the packet read thread.
     */
    public synchronized void startup() {
        mReadRunnableFuture = mExecutorService.submit(mReadRunnable);
        mDataRunnableFuture = mExecutorService.submit(this.mDataRunnable);
    }

    private class ReadRunnable implements Callable<Boolean> {

        @Override
        public Boolean call() {
            try {
                while (!bExit.get()) {
                    try {
                        byte[] buffer = new byte[4096];
                        int num = mInputStream.read(buffer);
                        if (num == 0) {
                            continue;
                        } else if (num < 0) {
                            throw new IOException("和远程服务器的连接断开");
                        }
                        byte[] bytes = new byte[num];
                        System.arraycopy(buffer, 0, bytes, 0, num);
                        mQueue.add(bytes);
                    } catch (SocketTimeoutException e) {
                        //ignore
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                mConnection.onSocketCloseUnexpected(e);
            }

            Log.e(TAG, "ReadRunnable exit!");
            return true;
        }
    }

    private class DataRunnable implements Callable<Boolean> {

        /**
         * 结束前通过发送一个空包来跳出循环
         */
        public void stop() {
            try {
                mQueue.add(new byte[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public Boolean call() {
            Log.d(TAG, "start read block thread ..");
            try {
                while (!bExit.get()) {
                    byte[] bytes = mQueue.take();
                    if (bytes == null) {
                        continue;
                    }
                    packetRouter.onDataReceive(bytes);
                }
            } catch (InterruptedException e) {
                mConnection.onSocketCloseUnexpected(e);
            }

            Log.e(TAG, "DataRunnable exit!");
            return true;
        }
    }


    public void shutdown() {
        try {
            bExit.set(true);
            if (mInputStream != null) {
                mInputStream.close();
                mInputStream = null;
            }

            mDataRunnable.stop();
            mReadRunnableFuture.cancel(true);
            mDataRunnableFuture.cancel(true);

            if (mExecutorService != null) {
                mExecutorService.shutdown();
                try {
                    if (!mExecutorService.awaitTermination(1, TimeUnit.SECONDS)) {
                        mExecutorService.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    mExecutorService.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
            if (mQueue != null) {
                mQueue.clear();
                mQueue = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
