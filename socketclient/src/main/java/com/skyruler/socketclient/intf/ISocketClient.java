package com.skyruler.socketclient.intf;

import com.skyruler.socketclient.connection.ConnectionOption;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.WrappedMessage;

public interface ISocketClient {
    void scanDevice(boolean enable);

    void connect(ConnectionOption option);

    void disConnect();

    void onDestroy();

    boolean isConnected();

    void sendMessage(WrappedMessage msgDataBean) throws InterruptedException;

    void addConnectionListener(IBleStateListener listener);

    void removeConnectionListener(IBleStateListener listener);

    void addMessageListener(IMessageListener listener, MessageFilter filter);

    void removeMessageListener(MessageFilter filter);
}
