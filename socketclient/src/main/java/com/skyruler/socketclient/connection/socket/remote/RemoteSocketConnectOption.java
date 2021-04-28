package com.skyruler.socketclient.connection.socket.remote;

import com.skyruler.socketclient.connection.socket.BaseSocketConnectOption;
import com.skyruler.socketclient.connection.socket.conf.SocketConnectOption;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageListener;

import java.util.Map;

public abstract class RemoteSocketConnectOption extends BaseSocketConnectOption {
    /**
     * 远程服务器ip地址
     */
    private final String host;

    /**
     * 远程服务器端口号
     */
    private final int port;


    protected RemoteSocketConnectOption(String host, int port, SocketConnectOption skSocketOption
            , boolean isServer, Map<MessageFilter, IMessageListener> wrappers, IMessage heartBeat) {
        super(heartBeat, skSocketOption, isServer, wrappers);
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public ConnectionType getType() {
        return ConnectionType.SOCKET;
    }

    @Override
    public String toString() {
        return "host=" + host + ",port=" + port;
    }
}
