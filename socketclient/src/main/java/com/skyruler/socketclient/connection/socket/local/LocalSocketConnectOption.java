package com.skyruler.socketclient.connection.socket.local;

import android.net.LocalSocketAddress;

import com.skyruler.socketclient.connection.socket.BaseSocketConnectOption;
import com.skyruler.socketclient.connection.socket.conf.SocketConnectOption;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageListener;

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
public abstract class LocalSocketConnectOption extends BaseSocketConnectOption {
    /**
     * 连接本地socket（{@link android.net.LocalSocket}）需要用到的Socket名字
     */
    private final String socketName;
    /**
     * 连接本地socket的命名空间
     */
    private final LocalSocketAddress.Namespace nameSpace;

    LocalSocketConnectOption(LocalSocketAddress.Namespace nameSpace, String socketName, SocketConnectOption skSocketOption
            , boolean isServer, Map<MessageFilter, IMessageListener> wrappers, IMessage heartBeat) {
        super(heartBeat, skSocketOption, isServer, wrappers);
        this.nameSpace = nameSpace == null ? LocalSocketAddress.Namespace.ABSTRACT : nameSpace;
        this.socketName = socketName;
    }

    public String getSocketName() {
        return socketName;
    }

    public LocalSocketAddress.Namespace getNameSpace() {
        return nameSpace;
    }

    public ConnectionType getType() {
        return ConnectionType.LOCAL_SOCKET;
    }

    @Override
    public String toString() {
        return "socketName=" + socketName + ",nameSpace=" + nameSpace;
    }
}
