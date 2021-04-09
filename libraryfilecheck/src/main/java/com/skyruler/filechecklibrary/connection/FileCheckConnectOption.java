package com.skyruler.filechecklibrary.connection;

import com.skyruler.filechecklibrary.message.MessageStrategy;
import com.skyruler.filechecklibrary.packet.PacketStrategy;
import com.skyruler.socketclient.connection.intf.IConnectOption;
import com.skyruler.socketclient.connection.socket.SocketConnectOption;
import com.skyruler.socketclient.connection.socket.remote.RemoteSocketConnectOption;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageListener;
import com.skyruler.socketclient.message.IMessageStrategy;
import com.skyruler.socketclient.message.IPacketStrategy;

import java.util.Map;

import static com.skyruler.socketclient.connection.intf.IConnectOption.ConnectionType.SOCKET;

public class FileCheckConnectOption extends RemoteSocketConnectOption {

    private FileCheckConnectOption(Builder builder) {
        super(builder.host,
                builder.port,
                builder.skSocketOption,
                false,
                builder.mWrappers,
                null);
    }

    @Override
    public IPacketStrategy getPacketConstructor() {
        return new PacketStrategy();
    }

    @Override
    public IMessageStrategy getMessageConstructor() {
        return new MessageStrategy();
    }

    @Override
    public IConnectOption.ConnectionType getType() {
        return SOCKET;
    }

    public class Builder {
        private String host;
        private int port;
        private IMessage heartBeat;
        private SocketConnectOption skSocketOption;
        private Map<MessageFilter, IMessageListener> mWrappers;
    }
}
