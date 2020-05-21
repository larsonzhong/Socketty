package com.skyruler.socketclient.connection;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
class PacketReader {
    private static final String TAG = "PacketReader";
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
                    Log.e(TAG, "read data error:" + e.getMessage());
                    Thread.currentThread().interrupt();
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

}
