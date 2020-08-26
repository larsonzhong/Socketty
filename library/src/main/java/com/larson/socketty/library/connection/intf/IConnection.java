package com.larson.socketty.library.connection.intf;

import android.content.Context;

import com.larson.socketty.library.filter.MessageFilter;
import com.larson.socketty.library.message.IMessage;
import com.larson.socketty.library.message.IMessageListener;


public interface IConnection {

    void connect(Context mContext, IStateListener listener);

    void disconnect();

    boolean isConnected();

    void onDestroy();

    void sendMessage(IMessage msgDataBean);

    IMessage sendSyncMessage(IMessage msgDataBean, long timeout) throws IllegalAccessException;

    IMessage sendSyncMessage(IMessage msgDataBean, MessageFilter filter, long timeout);

    void addMsgListener(IMessageListener listener, MessageFilter filter);

    void removeMsgListener(MessageFilter filter);

}
