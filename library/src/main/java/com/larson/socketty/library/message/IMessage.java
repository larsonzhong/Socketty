package com.larson.socketty.library.message;

public interface IMessage {

    short getMsgId();

    byte[] getBody();

    IPacket[] getPackets();
}
