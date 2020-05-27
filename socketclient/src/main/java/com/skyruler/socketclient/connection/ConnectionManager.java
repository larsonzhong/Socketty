package com.skyruler.socketclient.connection;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import com.skyruler.socketclient.connection.intf.IConnection;
import com.skyruler.socketclient.connection.intf.IConnectionManager;
import com.skyruler.socketclient.connection.intf.IStateListener;
import com.skyruler.socketclient.connection.option.IConnectOption;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageListener;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class ConnectionManager implements IConnectionManager {

    private IConnection mConnection;
    private ExecutorService mExecutor;
    private final Context mContext;
    private IStateListener stateListener;

    public ConnectionManager(Context context, IStateListener listener) {
        this.mContext = context;
        this.mExecutor = newExecutor();
        this.stateListener = listener;
    }

    @Override
    public void connect(IConnectOption bleConnectOption) {
        if (isConnected()) {
            return;
        }
        if (bleConnectOption == null) {
            throw new IllegalArgumentException("Connection parameter is empty, please check connection parameters !!");
        }
        mExecutor.execute(new ConnectTask(bleConnectOption));
    }

    private class ConnectTask implements Runnable {
        private IConnectOption bleConnectOption;

        ConnectTask(IConnectOption bleConnectOption) {
            this.bleConnectOption = bleConnectOption;
        }

        @Override
        public void run() {
            if (isConnected()) {
                return;
            }
            if (bleConnectOption.getType() == IConnectOption.ConnectionType.BLE) {
                mConnection = new BLEConnection(stateListener);
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
    public void sendMessage(IMessage msgDataBean) {
        mConnection.sendMessage(msgDataBean);
    }

    @Override
    public IMessage sendSyncMessage(IMessage msgDataBean, MessageFilter filter, long timeout) {
        if (filter == null || timeout < 0) {
            throw new IllegalArgumentException("can not send sync IMessage without filter or timeout");
        }
        return mConnection.sendSyncMessage(msgDataBean, filter, timeout);
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

}
