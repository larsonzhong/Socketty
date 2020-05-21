package com.skyruler.socketclient.connection.intf;

import android.content.Context;

import com.skyruler.socketclient.connection.option.IConnectOption;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageListener;

public interface IConnection {
    void scanLeDevice(Context mContext, boolean enable);

    void connect(Context mContext, IConnectOption bleConnectOption);

    void disconnect();

    void stopDevice();

    void sendMessage(IMessage msgDataBean);

    IMessage sendSyncMessage(IMessage msgDataBean, MessageFilter filter, long timeout);

    void addMsgListener(IMessageListener listener, MessageFilter filter);

    void removeMsgListener(MessageFilter filter);
}
