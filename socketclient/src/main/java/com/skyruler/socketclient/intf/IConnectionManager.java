package com.skyruler.socketclient.intf;

import com.skyruler.socketclient.connection.ConnectionOption;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.Message;

public interface IConnectionManager {

    void scanDevice(boolean enable);

    void connect(ConnectionOption bleConnectOption);

    boolean isConnected();

    void disConnect();

    void onDestroy();

    void sendMessage(Message msgDataBean);

    Message sendSyncMessage(Message msgDataBean, MessageFilter filter, long timeout) throws InterruptedException;

    void registerConnectListener(IBleStateListener listener);

    void unRegisterConnectListener(IBleStateListener listener);

    void addMessageListener(IMessageListener listener, MessageFilter filter);

    void removeMessageListener(MessageFilter filter);

}
