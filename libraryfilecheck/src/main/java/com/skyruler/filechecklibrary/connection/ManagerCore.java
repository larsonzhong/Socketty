package com.skyruler.filechecklibrary.connection;

import android.content.Context;
import android.util.Log;

import com.skyruler.filechecklibrary.command.AbsCommand;
import com.skyruler.filechecklibrary.message.WrappedMessage;
import com.skyruler.socketclient.SocketClient;
import com.skyruler.socketclient.connection.intf.IStateListener;
import com.skyruler.socketclient.exception.ConnectionException;
import com.skyruler.socketclient.exception.UnFormatMessageException;
import com.skyruler.socketclient.filter.MessageIdFilter;
import com.skyruler.socketclient.message.IMessageListener;

import java.util.concurrent.CopyOnWriteArrayList;

class ManagerCore {
    private static final String TAG = "ManagerCore";
    private SocketClient socketClient;

    private CopyOnWriteArrayList<IStateListener> connListeners;


    void setup(Context context) {
        this.socketClient = new SocketClient();
        this.socketClient.setup(context, new IStateListener() {
            @Override
            public void onConnect(Object device) {
                for (IStateListener listener : connListeners) {
                    listener.onConnect(device);
                }
            }

            @Override
            public void onDisconnect(Object device) {
                for (IStateListener listener : connListeners) {
                    listener.onDisconnect(device);
                }
            }
        });
    }

    void connect(FileCheckConnectOption option) {
        socketClient.connect(option);
    }


    void addConnectStateListener(IStateListener listener) {
        if (connListeners == null) {
            connListeners = new CopyOnWriteArrayList<>();
        }
        if (listener != null) {
            connListeners.add(listener);
        }
    }

    void removeConnectListener(IStateListener listener) {
        if (connListeners != null) {
            connListeners.remove(listener);
        }
    }

    void listenerForReport(IMessageListener listener, MessageIdFilter filter) {
        this.socketClient.addMessageListener(listener, filter);
    }

    void removeMsgListener(byte msgID) {
        this.socketClient.removeMessageListener(new MessageIdFilter(msgID));
    }

    boolean isConnected() {
        return socketClient.isConnected();
    }

    void disconnect() {
        socketClient.disConnect();
    }

    public void onDestroy() {
        socketClient.onDestroy();
        socketClient = null;
    }

    boolean sendMessage(AbsCommand cmd) {
        try {
            WrappedMessage message = new WrappedMessage
                    .Builder()
                    .command(cmd.getCommand())
                    .data(cmd.getData())
                    .msgFilter(cmd.getResultHandler())
                    .resultHandler(cmd.getResultHandler())
                    .build();
            boolean isSend = socketClient.sendMessage(message);
            Log.d(TAG, "sendMessage state=" + isSend);
            return isSend;
        } catch (ConnectionException | UnFormatMessageException e) {
            Log.e(TAG, "sendMessage failed :" + e.getMessage());
            return false;
        }
    }

}
