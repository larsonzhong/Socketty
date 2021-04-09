package com.skyruler.socketclient;

import android.content.Context;

import com.skyruler.socketclient.connection.intf.IConnectOption;
import com.skyruler.socketclient.exception.ConnectionException;
import com.skyruler.socketclient.exception.UnFormatMessageException;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.connection.intf.IStateListener;
import com.skyruler.socketclient.message.IMessageListener;
import com.skyruler.socketclient.message.IWrappedMessage;

public interface ISocketClient {
    void setup(Context context, IStateListener listener);

    void connect(IConnectOption option);

    void disConnect();

    void onDestroy();

    boolean isConnected();

    boolean sendMessage(IWrappedMessage msgDataBean) throws ConnectionException, UnFormatMessageException;

    void addMessageListener(IMessageListener listener, MessageFilter filter);

    void removeMessageListener(MessageFilter filter);
}
