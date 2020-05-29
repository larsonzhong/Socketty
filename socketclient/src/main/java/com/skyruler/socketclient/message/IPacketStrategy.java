package com.skyruler.socketclient.message;

public interface IPacketStrategy {

    IPacket parse(byte[] data);
}
