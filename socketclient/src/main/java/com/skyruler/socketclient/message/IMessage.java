package com.skyruler.socketclient.message;

import com.skyruler.socketclient.packet.Packet;

public interface IMessage {

    short getMsgId();

    byte[] getBody();

    Packet[] getPackets();
}
