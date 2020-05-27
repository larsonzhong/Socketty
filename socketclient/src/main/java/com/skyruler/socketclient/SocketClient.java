package com.skyruler.socketclient;

import android.content.Context;

import com.skyruler.socketclient.connection.ConnectionManager;
import com.skyruler.socketclient.connection.intf.IConnectionManager;
import com.skyruler.socketclient.connection.intf.IStateListener;
import com.skyruler.socketclient.connection.option.IConnectOption;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageListener;
import com.skyruler.socketclient.message.IWrappedMessage;

import java.util.List;

public class SocketClient implements ISocketClient {
    private IConnectionManager mConnMgr;

    @Override
    public void setup(Context context, IStateListener listener) {
        mConnMgr = new ConnectionManager(context, listener);
    }

    @Override
    public void connect(IConnectOption option) {
        mConnMgr.connect(option);
    }

    @Override
    public void disConnect() {
        mConnMgr.disConnect();
    }

    @Override
    public void onDestroy() {
        mConnMgr.onDestroy();
    }

    @Override
    public boolean isConnected() {
        return mConnMgr.isConnected();
    }

    @Override
    public boolean sendMessage(IWrappedMessage msgDataBean) throws InterruptedException {
        IWrappedMessage.AckMode ackMode = msgDataBean.getAckMode();
        MessageFilter filter = msgDataBean.getFilter();
        int timeout = msgDataBean.getTimeout();

        switch (ackMode) {
            case NON:
                IMessage singleMsg = msgDataBean.getMessages().get(0);
                mConnMgr.sendMessage(singleMsg);
                break;
            case MESSAGE:
                IMessage syncMessage = msgDataBean.getMessages().get(0);
                IMessage retMsg = mConnMgr.sendSyncMessage(syncMessage, filter, timeout);
                return retMsg != null;
            case PACKET:
                List<IMessage> messages = msgDataBean.getMessages();
                for (IMessage msg : messages) {
                    IMessage iMessage = mConnMgr.sendSyncMessage(msg, filter, timeout);
                    if (iMessage == null) {
                        return false;
                    }
                }
                break;
            default:
        }
        return true;
    }

    @Override
    public void addMessageListener(IMessageListener listener, MessageFilter filter) {
        mConnMgr.addMessageListener(listener, filter);
    }

    @Override
    public void removeMessageListener(MessageFilter filter) {
        mConnMgr.removeMessageListener(filter);
    }
}
