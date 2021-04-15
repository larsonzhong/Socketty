package com.skyruler.socketclient.connection.ble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import com.skyruler.socketclient.connection.PacketRouter;
import com.skyruler.socketclient.util.ArrayUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class BlePacketReader {
    private static final String TAG = "PacketReader";

    private final PacketRouter packetRouter;
    private final Thread mDataRunnable;
    private final AtomicBoolean mShutdown;
    private BlockingQueue<byte[]> mQueue;

    public BlePacketReader(PacketRouter packetRouter) {
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


    public void onBleDataReceive(BluetoothGattCharacteristic characteristic) {
        byte[] received = characteristic.getValue();
        mQueue.add(received);
        Log.d(TAG, "read packet>>>" + ArrayUtils.bytesToHex(received));
    }

}
