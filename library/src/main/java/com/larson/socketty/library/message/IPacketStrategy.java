package com.larson.socketty.library.message;

public interface IPacketStrategy {

    IPacket parse(byte[] data);
}
