package com.skyruler.socketclient.connection.socket.remote;

import android.content.Context;

import com.skyruler.socketclient.connection.PacketRouter;
import com.skyruler.socketclient.connection.intf.IConnectOption;
import com.skyruler.socketclient.connection.intf.ISocketConnection;
import com.skyruler.socketclient.connection.intf.IStateListener;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageListener;

import java.io.OutputStream;

public class RemoteSocketConnection implements ISocketConnection {
    private static final String TAG = "LocalSocketConnection";

    public RemoteSocketConnection(IConnectOption cfg) {
        /*mConfig = (RemoteSocketConfig) cfg;
        packetRouter = new PacketRouter();*/
    }

    @Override
    public void onSocketCloseUnexpected(Exception e) {

    }

    @Override
    public OutputStream getOutputStream() {
        return null;
    }

    @Override
    public void connect(Context mContext, IStateListener listener) {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void sendMessage(IMessage msgDataBean) {

    }

    @Override
    public IMessage sendSyncMessage(IMessage msgDataBean, long timeout) throws IllegalAccessException {
        return null;
    }

    @Override
    public IMessage sendSyncMessage(IMessage msgDataBean, MessageFilter filter, long timeout) {
        return null;
    }

    @Override
    public void addMsgListener(IMessageListener listener, MessageFilter filter) {

    }

    @Override
    public void removeMsgListener(MessageFilter filter) {

    }
}
