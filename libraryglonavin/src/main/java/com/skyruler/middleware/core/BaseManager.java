package com.skyruler.middleware.core;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.skyruler.middleware.command.AbsCommand;
import com.skyruler.middleware.command.EditionCommand;
import com.skyruler.middleware.connection.IBleStateListener;
import com.skyruler.socketclient.message.IMessageListener;

public abstract class BaseManager {
    private ManagerCore managerCore;

    BaseManager(Context context) {
        managerCore = new ManagerCore();
        managerCore.setup(context);
    }

    public abstract int getMode();

    public abstract String getDeviceName();

    public boolean isTestStart() {
        return managerCore.isTestStart();
    }

    public void addConnectStateListener(IBleStateListener listener) {
        managerCore.addConnectStateListener(listener);
    }

    public void removeConnectListener(IBleStateListener listener) {
        managerCore.removeConnectListener(listener);
    }

    public void scanDevice(boolean enable) {
        managerCore.scanDevice(enable);
    }

    public void connect(BluetoothDevice device) {
        managerCore.connect(device);
    }

    public void disconnect() {
        if (managerCore != null) {
            managerCore.disconnect();
        }
    }

    public boolean isConnected() {
        return managerCore != null && managerCore.isConnected();
    }

    public void onDestroy() {
        managerCore.onDestroy();
        managerCore = null;
    }

    public boolean setTestDirection(byte mStartSID, byte mEndSID) {
        return managerCore.setTestDirection(mStartSID, mEndSID);
    }

    public boolean startTest(boolean start) {
        return managerCore.startTest(start);
    }

    public void getEdition(EditionCommand.EditionCallBack callBack) {
        this.managerCore.getEdition(callBack);
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

    public boolean isBluetoothEnable() {
        return managerCore.isBluetoothEnable();
    }

    public void enableBluetooth(boolean checked) {
        managerCore.enableBluetooth(checked);
    }
}
