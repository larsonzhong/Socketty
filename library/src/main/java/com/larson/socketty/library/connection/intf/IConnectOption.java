package com.larson.socketty.library.connection.intf;


import com.larson.socketty.library.message.IMessageStrategy;
import com.larson.socketty.library.message.IPacketStrategy;

public interface IConnectOption {
    IPacketStrategy getPacketConstructor();

    IMessageStrategy getMessageConstructor();

    ConnectionType getType();

    enum ConnectionType {
        SOCKET,
        BLE
    }
}
