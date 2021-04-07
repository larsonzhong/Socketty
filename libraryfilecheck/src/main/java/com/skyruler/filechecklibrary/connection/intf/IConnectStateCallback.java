package com.skyruler.filechecklibrary.connection.intf;

public interface IConnectStateCallback {
    void onConnected(Object device);

    void onDisconnect(Object device);
}
