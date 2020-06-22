package com.skyruler.socketclient;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.skyruler.socketclient.connection.ConnectionManager;
import com.skyruler.socketclient.connection.intf.IConnectOption;
import com.skyruler.socketclient.connection.intf.IConnectionManager;
import com.skyruler.socketclient.connection.intf.IStateListener;
import com.skyruler.socketclient.exception.ConnectionException;
import com.skyruler.socketclient.exception.UnFormatMessageException;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageListener;
import com.skyruler.socketclient.message.IWrappedMessage;

import java.util.List;

public class SocketClient implements ISocketClient {
    private static final String TAG = "SocketClient";
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
    public boolean sendMessage(IWrappedMessage msgDataBean) throws ConnectionException, UnFormatMessageException {
        IWrappedMessage.AckMode ackMode = msgDataBean.getAckMode();
        MessageFilter msgFilter = msgDataBean.getMsgFilter();
        MessageFilter resultFilter = msgDataBean.getResultFilter();
        int timeout = msgDataBean.getTimeout();

        switch (ackMode) {
            case NON:
                IMessage singleMsg = msgDataBean.getMessages().get(0);
                mConnMgr.sendMessage(singleMsg);
                break;
            case MESSAGE:
                IMessage syncMessage = msgDataBean.getMessages().get(0);
                IMessage retMsg = mConnMgr.sendSyncMessage(syncMessage, msgFilter, timeout);
                return resultFilter.accept(retMsg);
            case PACKET:
                List<IMessage> messages = msgDataBean.getMessages();
                for (IMessage msg : messages) {
                    sendSyncMessage(msg, msgFilter, resultFilter, timeout, 0);
                }
                break;
            default:
        }
        return true;
    }

    /**
     * 如果没发成功，就一直发，发到不能再发为止
     */
    private void sendSyncMessage(IMessage msg, MessageFilter msgFilter, MessageFilter resultFilter, int timeout, int retryTimes)
            throws ConnectionException, UnFormatMessageException {
        IMessage iMessage = mConnMgr.sendSyncMessage(msg, msgFilter, timeout);
        SystemClock.sleep(20);
        if (iMessage == null || !resultFilter.accept(iMessage)) {
            retryTimes++;
            if (retryTimes > 5) {
                throw new ConnectionException("停止重发，重发次数超过限制:5");
            }
            Log.e(TAG, "message send failed,retrying..." + retryTimes);
            sendSyncMessage(msg, msgFilter, resultFilter, timeout, retryTimes);
        }
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
