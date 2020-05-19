package com.skyruler.socketclient.connection;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
class PacketReader {
    private PacketRouter packetRouter;
    private BlockingQueue<byte[]> mQueue;
    private Thread mDataRunnable;
    private AtomicBoolean mShutdown;

    PacketReader(PacketRouter packetRouter) {
        this.packetRouter = packetRouter;
        this.mDataRunnable = new DataRunnable();
        mShutdown = new AtomicBoolean(false);
    }

    synchronized void startup() {
        mDataRunnable.start();
    }

    private class DataRunnable extends Thread {
        @Override
        public void run() {
            while (!mShutdown.get()) {
                byte[] bytes = new byte[0];
                try {
                    bytes = mQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                packetRouter.onDataReceive(bytes);
            }
        }
    }

    void shutdown() {
        mShutdown.set(true);
        if (mQueue != null) {
            mQueue.clear();
            mQueue = null;
        }
        if (mDataRunnable != null) {
            mDataRunnable.interrupt();
        }
    }


    void onDataReceive(BluetoothGattCharacteristic characteristic) {
        byte[] received = characteristic.getValue();
        mQueue.add(received);
    }

    private ExecutorService newExecutor() {
        int corePoolSize = 10;
        int maxPoolSize = 500;
        long keepActiveTime = 200;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(1);
        ThreadFactory factory = new ThreadFactory() {
            private final AtomicInteger integer = new AtomicInteger();

            @Override
            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r, "ConnectionManager thread: " + integer.getAndIncrement());
            }
        };
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepActiveTime, timeUnit, workQueue, factory);
    }

}
