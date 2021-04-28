package com.skyruler.socketclient.connection.intf;

public interface IStateListener {

    void onConnected(Object device);

    void onConnectFailed(String reason);

    void onDisconnect(Object device);

}
