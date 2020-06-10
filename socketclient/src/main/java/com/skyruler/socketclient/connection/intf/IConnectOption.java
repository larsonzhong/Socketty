package com.skyruler.socketclient.connection.intf;

import com.skyruler.socketclient.message.IMessageStrategy;
import com.skyruler.socketclient.message.IPacketStrategy;

public interface IConnectOption {
    IPacketStrategy getPacketConstructor();

    IMessageStrategy getMessageConstructor();

    ConnectionType getType();

    enum ConnectionType {
        SOCKET,
        BLE
    }
}
