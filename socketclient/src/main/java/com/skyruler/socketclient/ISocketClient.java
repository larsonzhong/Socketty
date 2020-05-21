package com.skyruler.socketclient;

import android.content.Context;

import com.skyruler.socketclient.connection.option.IConnectOption;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.connection.intf.IBleStateListener;
import com.skyruler.socketclient.message.IMessageListener;
import com.skyruler.socketclient.message.IWrappedMessage;

public interface ISocketClient {
    void setup(Context context);

    void scanDevice(boolean enable);

    void connect(IConnectOption option);

    void disConnect();

    void onDestroy();

    boolean isConnected();

    void sendMessage(IWrappedMessage msgDataBean) throws InterruptedException;

    void addConnectionListener(IBleStateListener listener);

    void removeConnectionListener(IBleStateListener listener);

    void addMessageListener(IMessageListener listener, MessageFilter filter);

    void removeMessageListener(MessageFilter filter);
}
