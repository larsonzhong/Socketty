package com.skyruler.socketclient.connection.option;

import com.skyruler.socketclient.message.IMessageConstructor;
import com.skyruler.socketclient.message.IPacketConstructor;

public interface IConnectOption {
    IPacketConstructor getPacketConstructor();

    IMessageConstructor getMessageConstructor();

    ConnectionType getType();

    enum ConnectionType {
        SOCKET,
        BLE
    }
}
