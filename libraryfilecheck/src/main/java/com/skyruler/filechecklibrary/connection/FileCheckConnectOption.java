package com.skyruler.filechecklibrary.connection;

import com.skyruler.filechecklibrary.message.MessageStrategy;
import com.skyruler.filechecklibrary.packet.PacketStrategy;
import com.skyruler.socketclient.connection.socket.conf.SocketConnectOption;
import com.skyruler.socketclient.connection.socket.remote.RemoteSocketConnectOption;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.IMessageListener;
import com.skyruler.socketclient.message.IMessageStrategy;
import com.skyruler.socketclient.message.IPacketStrategy;

import java.util.LinkedHashMap;
import java.util.Map;


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
    public ConnectionType getType() {
        return ConnectionType.SOCKET;
    }

    public static class Builder {
        private String host;
        private int port;
        private SocketConnectOption skSocketOption;
        private Map<MessageFilter, IMessageListener> mWrappers;

        public Builder() {
            mWrappers = new LinkedHashMap<>();
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder skSocketOption(SocketConnectOption option) {
            this.skSocketOption = option;
            return this;
        }

        public Builder addMessageListener(MessageFilter filter, IMessageListener listener) {
            this.mWrappers.put(filter, listener);
            return this;
        }

        public FileCheckConnectOption build() {
            return new FileCheckConnectOption(this);
        }


    }
}
