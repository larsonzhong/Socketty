package com.skyruler.socketclient.connection;

import android.content.Context;

import com.skyruler.socketclient.connection.ble.BLEConnection;
import com.skyruler.socketclient.connection.intf.IConnectOption;
import com.skyruler.socketclient.connection.intf.IConnection;
import com.skyruler.socketclient.connection.intf.IConnectionManager;
import com.skyruler.socketclient.connection.intf.IStateListener;
import com.skyruler.socketclient.connection.socket.local.LocalSocketConnection;
import com.skyruler.socketclient.connection.socket.remote.RemoteSocketConnection;
import com.skyruler.socketclient.exception.ConnectionException;
import com.skyruler.socketclient.exception.UnFormatMessageException;
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
    public void connect(IConnectOption connectOption) {
        if (isConnected()) {
            return;
        }
        if (connectOption == null) {
            throw new IllegalArgumentException("SocketConnection parameter is empty, please check connection parameters !!");
        }
        mExecutor.execute(new ConnectTask(connectOption));
    }

    private class ConnectTask implements Runnable {
        private IConnectOption connectOption;

        ConnectTask(IConnectOption connectOption) {
            this.connectOption = connectOption;
        }

        @Override
        public void run() {
            if (isConnected()) {
                return;
            }
            if (connectOption.getType() == IConnectOption.ConnectionType.BLE) {
                mConnection = new BLEConnection(connectOption);
                mConnection.connect(mContext, stateListener);
            } else if (connectOption.getType() == IConnectOption.ConnectionType.LOCAL_SOCKET) {
                mConnection = new LocalSocketConnection(connectOption);
                mConnection.connect(mContext, stateListener);
            } else if (connectOption.getType() == IConnectOption.ConnectionType.SOCKET) {
                mConnection = new RemoteSocketConnection(connectOption);
                mConnection.connect(mContext, stateListener);
            }
        }
    }

    @Override
    public boolean isConnected() {
        return mConnection != null && mConnection.isConnected();
    }

    @Override
    public void disConnect() {
        if (mConnection != null) {
            mConnection.disconnect();
        }
    }

    @Override
    public void onDestroy() {
        if (mConnection != null) {
            mConnection.disconnect();
            mConnection.onDestroy();
            mConnection = null;
        }
    }

    @Override
    public void sendMessage(IMessage msgDataBean) {
        mConnection.sendMessage(msgDataBean);
    }

    @Override
    public IMessage sendSyncMessage(IMessage msgDataBean, MessageFilter filter, long timeout) throws ConnectionException, UnFormatMessageException {
        if (filter == null || timeout < 0) {
            throw new UnFormatMessageException("can not send sync IMessage without filter or timeout");
        }
        if (mConnection == null) {
            throw new ConnectionException("设备未连接，请连接设备");
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
            public Thread newThread(Runnable r) {
                return new Thread(r, "ConnectionManager thread: " + integer.getAndIncrement());
            }
        };
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepActiveTime, timeUnit, workQueue, factory);
    }

}
