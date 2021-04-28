package com.skyruler.socketclient.connection.socket;

import com.skyruler.socketclient.connection.intf.IConnectOption;
import com.skyruler.socketclient.connection.socket.conf.SocketConnectOption;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageListener;

import java.util.Map;

public abstract class BaseSocketConnectOption implements IConnectOption {
    /**
     * Heartbeat package
     */
    protected IMessage heartBeat;

    /**
     * Socket connect configuration
     */
    protected final SocketConnectOption skSocketOption;

    /**
     * Is it a control machine or a test machine
     */
    private final boolean isServer;

    /**
     * Static message listener
     */
    private final Map<MessageFilter, IMessageListener> mWrappers;

    public BaseSocketConnectOption(IMessage heartBeat, SocketConnectOption skSocketOption
            , boolean isServer, Map<MessageFilter, IMessageListener> wrappers) {
        this.isServer = isServer;
        this.mWrappers = wrappers;
        this.heartBeat = heartBeat;
        this.skSocketOption = skSocketOption;
    }

    public boolean isServer() {
        return isServer;
    }

    public boolean isClient() {
        return !isServer;
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

    public void updateHeartBeat(IMessage heartBeat) {
        this.heartBeat = heartBeat;
    }
}
