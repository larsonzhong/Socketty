package com.larson.socketty.library;

import android.content.Context;

import com.larson.socketty.library.connection.intf.IConnectOption;
import com.larson.socketty.library.connection.intf.IStateListener;
import com.larson.socketty.library.exception.ConnectionException;
import com.larson.socketty.library.exception.UnFormatMessageException;
import com.larson.socketty.library.filter.MessageFilter;
import com.larson.socketty.library.message.IMessageListener;
import com.larson.socketty.library.message.IWrappedMessage;


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
