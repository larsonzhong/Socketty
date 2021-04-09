package com.skyruler.socketclient.connection.socket.remote;

import com.skyruler.socketclient.connection.intf.IConnectOption;
import com.skyruler.socketclient.connection.socket.SocketConnectOption;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageListener;

import java.util.Map;

public abstract class RemoteSocketConnectOption implements IConnectOption {
    /**
     * 远程服务器ip地址
     */
    private final String host;
    /**
     * 远程服务器端口号
     */
    private final int port;
    /**
     * 心跳包
     */
    private final IMessage heartBeat;
    /**
     * 是终端还是服务器
     */
    private boolean isServer;
    /**
     * 通讯配置
     */
    private final SocketConnectOption skSocketOption;

    /**
     * 静态消息监听器
     */
    private final Map<MessageFilter, IMessageListener> mWrappers;

    protected RemoteSocketConnectOption(String host, int port, SocketConnectOption skSocketOption
            , boolean isServer, Map<MessageFilter, IMessageListener> wrappers, IMessage heartBeat) {
        this.host = host;
        this.port = port;
        this.skSocketOption = skSocketOption;
        this.isServer = isServer;
        this.mWrappers = wrappers;
        this.heartBeat = heartBeat;
    }


    public boolean isServer() {
        return isServer;
    }

    public boolean isClient() {
        return !isServer;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Map<MessageFilter, IMessageListener> getWrappers() {
        return mWrappers;
    }

    public SocketConnectOption getSkSocketOption() {
        return skSocketOption;
    }

    public IMessage getHeartBeat() {
        return heartBeat;
    }

    public ConnectionType getType() {
        return ConnectionType.SOCKET;
    }

    @Override
    public String toString() {
        return "host=" + host + ",port=" + port;
    }
}
