package com.skyruler.socketclient.connection.intf;

public interface IStateListener {

    void onConnect(Object device);

    void onDisconnect(Object device);

    void onDisconnect(Exception e);
}
