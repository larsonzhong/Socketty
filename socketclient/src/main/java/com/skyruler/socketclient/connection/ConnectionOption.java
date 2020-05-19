package com.skyruler.socketclient.connection;

public interface ConnectionOption {

    ConnectionType getType();

    enum ConnectionType {
        SOCKET,
        BLE
    }
}
