package com.skyruler.middleware.packet;

import com.skyruler.socketclient.message.IPacket;
import com.skyruler.socketclient.message.IPacketConstructor;
import com.skyruler.socketclient.util.BytesUtils;
import com.skyruler.socketclient.util.CRCCheck;

import java.nio.ByteBuffer;

public class Packet implements IPacket {
    public static final short HEADER = 0x24D0;
    private final short length;
    private final byte[] data;
    private final byte crc;

    private Packet(Builder builder) {
        this.length = builder.length;
        this.data = builder.data;
        this.crc = builder.crc;
    }

    public short getLength() {
        return length;
    }

    public byte[] getData() {
        return data;
    }

    public byte getCrc() {
        return crc;
    }

    public byte[] getBytes() {
        int totalLen = 2 + length + 1;
        byte[] bytes = new byte[totalLen];
        ByteBuffer byteBuffer = ByteBuffer.allocate(totalLen);
        byteBuffer.putShort(HEADER);
        byteBuffer.put(data);
        byteBuffer.put(crc);
        byteBuffer.flip();
        byteBuffer.get(bytes);
        return bytes;
    }

    public static class Builder {
        short length;
        byte[] data;
        byte crc;

        public Builder(byte[] data) {
            this.data = data;
            this.crc = checkCRC(data);
            this.length = (short) data.length;
        }

        public Builder(short length, byte[] data, byte crc) {
            this.length = length;
            this.data = data;
            this.crc = crc;
        }

        private byte checkCRC(byte[] body) {
            int totalLen = 2 + length;
            byte[] bytes = new byte[totalLen];
            ByteBuffer byteBuffer = ByteBuffer.allocate(totalLen);
            byteBuffer.putShort(Packet.HEADER);
            byteBuffer.put(body);
            byteBuffer.flip();
            byteBuffer.get(bytes);
            return CRCCheck.checkSumCrc8(bytes, bytes.length);
        }

        public Packet build() {
            return new Packet(this);
        }
    }

    public static class Constructor implements IPacketConstructor {
        @Override
        public IPacket parse(byte[] raw) {
            int index = 0;
            short header = BytesUtils.bytesToShort(raw, 0);
            index += BytesUtils.shortToBytes(header).length;
            short length = BytesUtils.bytesToShort(raw, index);
            index += BytesUtils.shortToBytes(length).length;
            byte[] data = new byte[length];
            System.arraycopy(raw, index, data, 0, length);
            byte crc = raw[raw.length - 1];

            return new Builder(length, data, crc).build();
        }
    }
}
