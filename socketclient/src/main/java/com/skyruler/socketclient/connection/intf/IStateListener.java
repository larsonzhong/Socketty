package com.skyruler.socketclient.connection.intf;

public interface IStateListener {

    void onDeviceConnect(Object device);

    void onDeviceDisconnect(Object device);

    void onSocketConnected();

    void onSocketDisconnect();
}
