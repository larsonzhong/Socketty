package com.skyruler.socketclient.connection.intf;

import com.skyruler.socketclient.message.IMessageStrategy;
import com.skyruler.socketclient.message.IPacketStrategy;

public interface IConnectOption {
    /**
     * 包构造器，传入字节数据，构建出一个完整packet
     *
     * @return 包构造器
     */
    IPacketStrategy getPacketConstructor();

    /**
     * 消息构造器，传入Packet{@link com.skyruler.socketclient.message.IPacket}，
     * 构建出一个完整Message{@link com.skyruler.socketclient.message.IMessage}
     *
     * @return 消息
     */
    IMessageStrategy getMessageConstructor();

    /**
     * 连接类型，从下面枚举取
     *
     * @return 连接类型
     */
    ConnectionType getType();

    enum ConnectionType {
        /**
         * Socket连接远程服务器
         */
        SOCKET,
        /**
         * 本地Socket连接C
         */
        LOCAL_SOCKET,
        /**
         * 蓝牙设备连接
         */
        BLE
    }
}
