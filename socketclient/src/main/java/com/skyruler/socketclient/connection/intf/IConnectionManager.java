package com.skyruler.socketclient.connection.intf;

import com.skyruler.socketclient.connection.option.IConnectOption;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageListener;

public interface IConnectionManager {

    void scanDevice(boolean enable);

    void connect(IConnectOption bleConnectOption);

    boolean isConnected();

    void disConnect();

    void onDestroy();

    void sendMessage(IMessage msgDataBean);

    IMessage sendSyncMessage(IMessage msgDataBean, MessageFilter filter, long timeout) throws InterruptedException;

    void registerConnectListener(IBleStateListener listener);

    void unRegisterConnectListener(IBleStateListener listener);

    void addMessageListener(IMessageListener listener, MessageFilter filter);

    void removeMessageListener(MessageFilter filter);

}
