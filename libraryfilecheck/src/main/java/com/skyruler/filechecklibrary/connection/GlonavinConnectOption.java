package com.skyruler.filechecklibrary.connection;

import com.skyruler.filechecklibrary.message.MessageStrategy;
import com.skyruler.filechecklibrary.packet.PacketStrategy;
import com.skyruler.socketclient.connection.intf.IConnectOption;
import com.skyruler.socketclient.message.IMessageStrategy;
import com.skyruler.socketclient.message.IPacketStrategy;

import static com.skyruler.socketclient.connection.intf.IConnectOption.ConnectionType.SOCKET;

public class GlonavinConnectOption implements IConnectOption {


    @Override
    public IPacketStrategy getPacketConstructor() {
        return new PacketStrategy();
    }

    @Override
    public IMessageStrategy getMessageConstructor() {
        return new MessageStrategy();
    }

    @Override
    public ConnectionType getType() {
        return SOCKET;
    }

}
