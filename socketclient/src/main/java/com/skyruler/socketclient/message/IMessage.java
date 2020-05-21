package com.skyruler.socketclient.message;

public interface IMessage {

    short getMsgId();

    byte[] getBody();

    IPacket[] getPackets();
}
