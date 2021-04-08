package com.skyruler.filechecklibrary.connection;

import android.content.Context;

import com.skyruler.filechecklibrary.command.AbsCommand;
import com.skyruler.socketclient.connection.intf.IStateListener;
import com.skyruler.socketclient.filter.MessageIdFilter;
import com.skyruler.socketclient.message.IMessageListener;

public abstract class BaseManager {
    private ManagerCore managerCore;

    BaseManager(Context context) {
        managerCore = new ManagerCore();
        managerCore.setup(context);
    }

    public void addConnectStateListener(IStateListener listener) {
        managerCore.addConnectStateListener(listener);
    }

    public void removeConnectListener(IStateListener listener) {
        managerCore.removeConnectListener(listener);
    }


    public void disconnect() {
        managerCore.disconnect();
    }

    public boolean isConnected() {
        return managerCore.isConnected();
    }

    public void onDestroy() {
        managerCore.onDestroy();
        managerCore = null;
    }

    void addMessageListener(IMessageListener listener, MessageIdFilter filter) {
        this.managerCore.listenerForReport(listener, filter);
    }

    void removeMsgListener(byte msgID) {
        this.managerCore.removeMsgListener(msgID);
    }

    boolean sendMessage(AbsCommand cmd) {
        return this.managerCore.sendMessage(cmd);
    }

}
