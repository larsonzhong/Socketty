package com.larson.socketty.library.connection.socket;

import android.net.LocalSocketAddress;


import com.larson.socketty.library.connection.intf.IConnectOption;
import com.larson.socketty.library.filter.MessageFilter;
import com.larson.socketty.library.message.IMessage;
import com.larson.socketty.library.message.IMessageListener;

import java.util.Map;


/**
 * ......................-~~~~~~~~~-._       _.-~~~~~~~~~-.
 * ............... _ _.'              ~.   .~              `.__
 * ..............'//     NO           \./      BUG         \\`.
 * ............'//                     |                     \\`.
 * ..........'// .-~"""""""~~~~-._     |     _,-~~~~"""""""~-. \\`.
 * ........'//.-"                 `-.  |  .-'                 "-.\\`.
 * ......'//______.============-..   \ | /   ..-============.______\\`.
 * ....'______________________________\|/______________________________`.
 * ..larsonzhong@163.com      created in 2018/8/15     @author : larsonzhong
 */
public abstract class SocketConfig implements IConnectOption {
    /**
     * 连接本地socket（{@link android.net.LocalSocket}）需要用到的Socket名字
     */
    private final String socketName;
    /**
     * 连接本地socket的命名空间
     */
    private final LocalSocketAddress.Namespace nameSpace;
    /**
     * 心跳包
     */
    private final IMessage heartBeat;
    /**
     * 是控制機還是測試機
     */
    private boolean isServer;

    private final SocketConnectOption skSocketOption;

    /**
     * 静态消息监听器
     */
    private final Map<MessageFilter, IMessageListener> mWrappers;

    SocketConfig(LocalSocketAddress.Namespace nameSpace, String socketName, SocketConnectOption skSocketOption
            , boolean isServer, Map<MessageFilter, IMessageListener> wrappers, IMessage heartBeat) {
        this.nameSpace = nameSpace == null ? LocalSocketAddress.Namespace.ABSTRACT : nameSpace;
        this.socketName = socketName;
        this.skSocketOption = skSocketOption;
        this.isServer = isServer;
        this.mWrappers = wrappers;
        this.heartBeat = heartBeat;
    }

    public String getSocketName() {
        return socketName;
    }

    public boolean isServer() {
        return isServer;
    }

    public boolean isClient() {
        return !isServer;
    }

    public LocalSocketAddress.Namespace getNameSpace() {
        return nameSpace;
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
        return "socketName=" + socketName + ",nameSpace=" + nameSpace;
    }
}
