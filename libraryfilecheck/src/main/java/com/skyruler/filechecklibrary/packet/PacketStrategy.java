package com.skyruler.filechecklibrary.packet;

import com.skyruler.socketclient.message.IPacket;
import com.skyruler.socketclient.message.IPacketStrategy;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketStrategy implements IPacketStrategy {
    private static final int HEAD_LENGTH = 2;

    @Override
    public IPacket parse(byte[] raw) {
        ByteBuffer buffer = ByteBuffer.wrap(raw);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.rewind();
        if (raw.length <= HEAD_LENGTH) {
            // =2表示只发了头（包长），不需要处理
            return null;
        }
        short length = buffer.getShort();
        if (length > raw.length - HEAD_LENGTH) {
            // 包不全不处理
            return null;
        }
        byte[] data = new byte[length];
        buffer.get(data);

        return new Packet.Builder(length, data).build();
    }
}
