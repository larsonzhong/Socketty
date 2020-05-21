package com.skyruler.socketclient;

import android.content.Context;

import com.skyruler.socketclient.connection.ConnectionManager;
import com.skyruler.socketclient.connection.option.IConnectOption;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.connection.intf.IBleStateListener;
import com.skyruler.socketclient.connection.intf.IConnectionManager;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageListener;
import com.skyruler.socketclient.message.IWrappedMessage;

import java.util.List;

public class SocketClient implements ISocketClient {
    private IConnectionManager mConnMgr;

    @Override
    public void setup(Context context) {
        mConnMgr = new ConnectionManager(context);
    }

    @Override
    public void scanDevice(boolean enable) {
        mConnMgr.scanDevice(enable);
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
    public void sendMessage(IWrappedMessage msgDataBean) throws InterruptedException {
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
                mConnMgr.sendSyncMessage(syncMessage, filter, timeout);
                break;
            case PACKET:
                List<IMessage> messages = msgDataBean.getMessages();
                for (IMessage msg : messages) {
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
