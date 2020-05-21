package com.skyruler.socketclient.connection;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IPacket;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class PacketWriter {
    private static final String TAG = "PacketWriter";
    private boolean mShutdown;
    private final Thread mWriteThread;
    private final BlockingQueue<IPacket> mQueue;
    private final BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic gattCharacteristic;

    PacketWriter(BluetoothGatt mBluetoothGatt) {
        this.mShutdown = false;
        this.mBluetoothGatt = mBluetoothGatt;
        this.mQueue = new LinkedBlockingQueue<>();
        this.mWriteThread = new WriteThread();
        this.mWriteThread.setName("Thread[Message Writer]");
        this.mWriteThread.setDaemon(true);
    }

    void startup() {
        mWriteThread.start();
    }

    void sendMessage(IMessage msg) {
        if (mShutdown) {
            return;
        }
        synchronized (this) {
            for (IPacket packet : msg.getPackets()) {
                try {
                    mQueue.put(packet);
                } catch (InterruptedException e) {
                    Log.e(TAG, "send Message error:" + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }
        synchronized (mQueue) {
            mQueue.notifyAll();
        }
    }

    void setWriteCharacteristic(BluetoothGattCharacteristic gattCharacteristic) {
        this.gattCharacteristic = gattCharacteristic;
    }

    void shutdown() {
        mShutdown = true;
        synchronized (mQueue) {
            mQueue.notifyAll();
        }
        //关闭输出线程
        if (mWriteThread != null) {
            mWriteThread.interrupt();
        }
    }

    private class WriteThread extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                while (!mShutdown) {
                    IPacket packet = null;
                    synchronized (mQueue) {
                        while (!mShutdown && (packet = mQueue.poll()) == null) {
                            mQueue.wait();
                        }
                    }

                    synchronized (mBluetoothGatt) {
                        if (packet != null) {
                            gattCharacteristic.setValue(packet.getBytes());
                            mBluetoothGatt.writeCharacteristic(gattCharacteristic);
                        }
                    }
                }
                mQueue.clear();
            } catch (InterruptedException e) {
                //todo 异常断开，需要通知上层
                Log.e(TAG, "write Message error:" + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }


}
