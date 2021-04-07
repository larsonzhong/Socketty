package com.skyruler.filechecklibrary.connection;

import android.content.Context;
import android.util.Log;

import com.skyruler.filechecklibrary.command.AbsCommand;
import com.skyruler.filechecklibrary.connection.intf.IConnectStateCallback;
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
    private static final long SCAN_PERIOD = 10000L;
    private SocketClient socketClient;

    private CopyOnWriteArrayList<IConnectStateCallback> connListeners;


    void setup(Context context) {
        this.socketClient = new SocketClient();
        this.socketClient.setup(context, new IStateListener() {
            @Override
            public void onConnect(Object device) {
                for (IConnectStateCallback listener : connListeners) {
                    listener.onConnected(device);
                }
            }

            @Override
            public void onDisconnect(Object device) {
                for (IConnectStateCallback listener : connListeners) {
                    listener.onDisconnect(device);
                }
            }
        });
    }


    void addConnectStateListener(IConnectStateCallback listener) {
        if (connListeners == null) {
            connListeners = new CopyOnWriteArrayList<>();
        }
        if (listener != null) {
            connListeners.add(listener);
        }
    }

    void removeConnectListener(IConnectStateCallback listener) {
        if (connListeners != null) {
            connListeners.remove(listener);
        }
    }

    void listenerForReport(IMessageListener listener, byte reportID) {
        this.socketClient.addMessageListener(listener, new MessageIdFilter(reportID));
    }

    void removeMsgListener(byte msgID) {
        this.socketClient.removeMessageListener(new MessageIdFilter(msgID));
    }

    boolean startTest(boolean start) {
        return false;
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
                    .Builder(cmd.getMsgID())
                    .body(cmd.getBody())
                    .ackMode(cmd.getAckMode())
                    .msgFilter(cmd.getResponseFilter())
                    .resultHandler(cmd.getResultHandler())
                    .timeout(cmd.getTimeout())
                    .limitBodyLength(cmd.getLimitBodyLength())
                    .build();
            boolean isSend = socketClient.sendMessage(message);
            Log.d(TAG, "sendMessage state=" + isSend);
            return isSend;
        } catch (ConnectionException e) {
            Log.e(TAG, "sendMessage failed :" + e.getMessage());
            return false;
        } catch (UnFormatMessageException e) {
            Log.e(TAG, "sendMessage failed :" + e.getMessage());
            return false;
        }
    }


}
