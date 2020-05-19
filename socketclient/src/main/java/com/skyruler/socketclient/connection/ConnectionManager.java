package com.skyruler.socketclient.connection;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.intf.IBleStateListener;
import com.skyruler.socketclient.intf.IConnection;
import com.skyruler.socketclient.intf.IConnectionManager;
import com.skyruler.socketclient.intf.IMessageListener;
import com.skyruler.socketclient.message.Message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class ConnectionManager implements IConnectionManager {
    private CopyOnWriteArrayList<IBleStateListener> connListeners;
    private IConnection mConnection;
    private ExecutorService mExecutor;
    private final Context mContext;

    public ConnectionManager(Context context) {
        this.mContext = context;
        this.mExecutor = newExecutor();
    }

    @Override
    public void scanDevice(boolean enable) {
        mConnection.scanLeDevice(mContext, enable);
    }

    @Override
    public void connect(ConnectionOption bleConnectOption) {
        if (isConnected()) {
            return;
        }
        if (bleConnectOption == null) {
            throw new IllegalArgumentException("Connection parameter is empty, please check connection parameters !!");
        }
        mExecutor.execute(new ConnectTask(bleConnectOption));
    }

    private class ConnectTask implements Runnable {
        private ConnectionOption bleConnectOption;

        ConnectTask(ConnectionOption bleConnectOption) {
            this.bleConnectOption = bleConnectOption;
        }

        @Override
        public void run() {
            if (isConnected()) {
                return;
            }
            if (bleConnectOption.getType() == ConnectionOption.ConnectionType.BLE) {
                mConnection = new BLEConnection(connListener);
                mConnection.connect(mContext, bleConnectOption);
            }
        }
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void disConnect() {
        mConnection.disconnect();
    }

    @Override
    public void onDestroy() {
        mConnection.stopDevice();
    }

    @Override
    public void sendMessage(Message msgDataBean) {
        mConnection.sendMessage(msgDataBean);
    }

    @Override
    public Message sendSyncMessage(Message msgDataBean, MessageFilter filter, long timeout) {
        if (filter == null || timeout < 0) {
            throw new IllegalArgumentException("can not send sync message without filter or timeout");
        }
        return mConnection.sendSyncMessage(msgDataBean, filter, timeout);
    }

    @Override
    public void registerConnectListener(IBleStateListener listener) {
        if (connListeners == null) {
            connListeners = new CopyOnWriteArrayList<>();
        }
        if (listener != null) {
            connListeners.add(listener);
        }
    }

    @Override
    public void unRegisterConnectListener(IBleStateListener listener) {
        if (connListeners != null) {
            connListeners.remove(listener);
        }
    }


    @Override
    public void addMessageListener(IMessageListener listener, MessageFilter filter) {
        if (mConnection != null) {
            mConnection.addMsgListener(listener, filter);
        }
    }

    @Override
    public void removeMessageListener(MessageFilter filter) {
        if (mConnection != null) {
            mConnection.removeMsgListener(filter);
        }
    }

    private ExecutorService newExecutor() {
        int corePoolSize = 50;
        int maxPoolSize = 500;
        long keepActiveTime = 200;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(50);
        ThreadFactory factory = new ThreadFactory() {
            private final AtomicInteger integer = new AtomicInteger();

            @Override
            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r, "ConnectionManager thread: " + integer.getAndIncrement());
            }
        };
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepActiveTime, timeUnit, workQueue, factory);
    }

    private IBleStateListener connListener = new IBleStateListener() {
        @Override
        public void onServiceDiscover(BluetoothGatt gatt) {
            for (IBleStateListener listener : connListeners) {
                listener.onServiceDiscover(gatt);
            }
        }

        @Override
        public void onScanResult(BluetoothDevice device, boolean isConnected) {
            for (IBleStateListener listener : connListeners) {
                listener.onScanResult(device, isConnected);
            }
        }

        @Override
        public void onConnect(BluetoothGatt gatt) {
            for (IBleStateListener listener : connListeners) {
                listener.onConnect(gatt);
            }
        }

        @Override
        public void onDisconnect(BluetoothGatt gatt) {
            for (IBleStateListener listener : connListeners) {
                listener.onDisconnect(gatt);
            }
        }
    };

}
