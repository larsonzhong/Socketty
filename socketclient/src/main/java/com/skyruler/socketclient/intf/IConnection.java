package com.skyruler.socketclient.intf;

import android.content.Context;

import com.skyruler.socketclient.connection.BleConnectOption;
import com.skyruler.socketclient.connection.ConnectionOption;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.Message;

public interface IConnection {
    void scanLeDevice(Context mContext, boolean enable);

    void connect(Context mContext, ConnectionOption bleConnectOption);

    void disconnect();

    void stopDevice();

    void sendMessage(Message msgDataBean);

    Message sendSyncMessage(Message msgDataBean, MessageFilter filter, long timeout);

    void addMsgListener(IMessageListener listener, MessageFilter filter);

    void removeMsgListener(MessageFilter filter);
}
