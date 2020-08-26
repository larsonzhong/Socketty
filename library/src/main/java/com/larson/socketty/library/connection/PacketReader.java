package com.larson.socketty.library.connection;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;


import com.larson.socketty.library.util.ArrayUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class PacketReader {
    private static final String TAG = "PacketReader";
    private PacketRouter packetRouter;
    private BlockingQueue<byte[]> mQueue;
    private Thread mDataRunnable;
    private AtomicBoolean mShutdown;

    public PacketReader(PacketRouter packetRouter) {
        this.packetRouter = packetRouter;
        this.mShutdown = new AtomicBoolean(false);
        this.mDataRunnable = new DataRunnable();
        this.mQueue = new LinkedBlockingQueue<>();
    }

    public synchronized void startup() {
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
                    Log.e(TAG, "read data error:" + e.getMessage());
                    Thread.currentThread().interrupt();
                }
                packetRouter.onDataReceive(bytes);
            }
        }
    }

    public void shutdown() {
        mShutdown.set(true);
        if (mQueue != null) {
            mQueue.clear();
            mQueue = null;
        }
        if (mDataRunnable != null) {
            mDataRunnable.interrupt();
        }
    }


    public void onDataReceive(BluetoothGattCharacteristic characteristic) {
        byte[] received = characteristic.getValue();
        mQueue.add(received);
        Log.d(TAG, "read packet>>>" + ArrayUtils.bytesToHex(received));
    }

}
