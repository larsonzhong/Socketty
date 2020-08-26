package com.larson.socketty.library.connection.intf;

public interface IStateListener {

    void onConnect(Object device);

    void onDisconnect(Object device);
}
