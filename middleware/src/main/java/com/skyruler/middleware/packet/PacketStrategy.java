package com.skyruler.middleware.packet;

import com.skyruler.socketclient.message.IPacket;
import com.skyruler.socketclient.message.IPacketStrategy;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketStrategy implements IPacketStrategy {
    @Override
    public IPacket parse(byte[] raw) {
        ByteBuffer buffer = ByteBuffer.wrap(raw);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.rewind();
        if (raw.length < 2) {
            return null;
        }
        byte header = buffer.get();
        if (header != Packet.HEADER) {
            return null;
        }
        byte pkgType = buffer.get();
        short length = buffer.getShort();
        if (length != raw.length - 5) {
            return null;
        }
        byte[] data = new byte[length];
        buffer.get(data);
        byte crc = buffer.get();

        // CRCCheck.checkSumCrc8(array, array.length - 1)
        return new Packet.Builder(pkgType, length, data).build();
    }
}
