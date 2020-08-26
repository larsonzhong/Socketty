package com.larson.socketty.library.connection.intf;


import com.larson.socketty.library.exception.ConnectionException;
import com.larson.socketty.library.exception.UnFormatMessageException;
import com.larson.socketty.library.filter.MessageFilter;
import com.larson.socketty.library.message.IMessage;
import com.larson.socketty.library.message.IMessageListener;

public interface IConnectionManager {

    void connect(IConnectOption bleConnectOption);

    boolean isConnected();

    void disConnect();

    void onDestroy();

    void sendMessage(IMessage msgDataBean);

    IMessage sendSyncMessage(IMessage msgDataBean, MessageFilter filter, long timeout) throws ConnectionException, UnFormatMessageException;

    void addMessageListener(IMessageListener listener, MessageFilter filter);

    void removeMessageListener(MessageFilter filter);

}
