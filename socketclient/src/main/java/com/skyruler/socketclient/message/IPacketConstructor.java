package com.skyruler.socketclient.message;

public interface IPacketConstructor {

    IPacket parse(byte[] data);
}
