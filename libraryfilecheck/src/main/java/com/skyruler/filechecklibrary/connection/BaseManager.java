package com.skyruler.filechecklibrary.connection;

import android.content.Context;

import com.skyruler.filechecklibrary.command.AbsCommand;
import com.skyruler.filechecklibrary.connection.intf.IConnectStateCallback;
import com.skyruler.socketclient.message.IMessageListener;

public abstract class BaseManager {
    private ManagerCore managerCore;

    BaseManager(Context context) {
        managerCore = new ManagerCore();
        managerCore.setup(context);
    }


    public void addConnectStateListener(IConnectStateCallback listener) {
        managerCore.addConnectStateListener(listener);
    }

    public void removeConnectListener(IConnectStateCallback listener) {
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

    public boolean startTest(boolean start) {
        return managerCore.startTest(start);
    }

    void addMessageListener(IMessageListener listener, byte msgID) {
        this.managerCore.listenerForReport(listener, msgID);
    }

    void removeMsgListener(byte msgID) {
        this.managerCore.removeMsgListener(msgID);
    }

    boolean sendMessage(AbsCommand cmd) {
        return this.managerCore.sendMessage(cmd);
    }

}
