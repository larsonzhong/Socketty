package com.skyruler.socketclient;

import android.content.Context;

import com.skyruler.socketclient.connection.ConnectionManager;
import com.skyruler.socketclient.connection.ConnectionOption;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.intf.IBleStateListener;
import com.skyruler.socketclient.intf.IConnectionManager;
import com.skyruler.socketclient.intf.IMessageListener;
import com.skyruler.socketclient.intf.ISocketClient;
import com.skyruler.socketclient.message.Message;
import com.skyruler.socketclient.message.WrappedMessage;

import java.util.List;

public class SocketClient implements ISocketClient {
    private IConnectionManager mConnMgr;

    public void setup(Context context) {
        mConnMgr = new ConnectionManager(context);
    }

    @Override
    public void scanDevice(boolean enable) {
        mConnMgr.scanDevice(enable);
    }

    @Override
    public void connect(ConnectionOption option) {
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
    public void sendMessage(WrappedMessage msgDataBean) throws InterruptedException {
        WrappedMessage.AckMode ackMode = msgDataBean.getAckMode();
        MessageFilter filter = msgDataBean.getFilter();
        int timeout = msgDataBean.getTimeout();

        switch (ackMode) {
            case NON:
                Message singleMsg = msgDataBean.getMessages().get(0);
                mConnMgr.sendMessage(singleMsg);
                break;
            case MESSAGE:
                Message syncMessage = msgDataBean.getMessages().get(0);
                mConnMgr.sendSyncMessage(syncMessage, filter, timeout);
                break;
            case PACKET:
                List<Message> messages = msgDataBean.getMessages();
                for (Message msg : messages) {
                    mConnMgr.sendSyncMessage(msg, filter, timeout);
                }
                break;
            default:
        }
    }

    @Override
    public void addConnectionListener(IBleStateListener listener) {
        mConnMgr.registerConnectListener(listener);
    }

    @Override
    public void removeConnectionListener(IBleStateListener listener) {
        mConnMgr.unRegisterConnectListener(listener);
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
