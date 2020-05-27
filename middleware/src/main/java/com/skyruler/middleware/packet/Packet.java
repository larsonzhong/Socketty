package com.skyruler.middleware.packet;

import com.skyruler.socketclient.message.IPacket;
import com.skyruler.socketclient.message.IPacketConstructor;
import com.skyruler.socketclient.util.BytesUtils;
import com.skyruler.socketclient.util.CRCCheck;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Packet implements IPacket {
    private static final short HEADER = 0x24D0;
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
        //固定帧头2+数据长度2+有效数据length+Crc1
        int totalLen = 2 + 2 + length + 1;
        byte[] bytes = new byte[totalLen];
        ByteBuffer byteBuffer = ByteBuffer.allocate(totalLen);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putShort(HEADER);
        byteBuffer.putShort(length);
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

        public Builder(byte[] validData) {
            this.data = validData;
            this.length = (short) validData.length;
            this.crc = checkCRC();
        }

        Builder(short length, byte[] data, byte crc) {
            this.length = length;
            this.data = data;
            this.crc = crc;
        }

        //00100100 11010000 00000000 00000010 00110000 00000001 01010110
        // 0x24      0xD0    0x00      0x02     0x30     0x01     0x56
        private byte checkCRC() {
            // 校验数据=固定帧头2+数据长度2+有效数据长度length
            int totalLen = 2 + 2 + length;
            byte[] bytes = new byte[totalLen];
            ByteBuffer byteBuffer = ByteBuffer.allocate(totalLen);
            byteBuffer.putShort(Packet.HEADER);
            byteBuffer.putShort(length);
            byteBuffer.put(data);
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
            ByteBuffer buffer = ByteBuffer.wrap(raw);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.rewind();
            if (raw.length < 2) {
                return null;
            }
            short header = buffer.getShort();
            if (header != HEADER) {
                return null;
            }
            short length = buffer.getShort();
            if (length != raw.length - 5) {
                return null;
            }
            byte[] data = new byte[length];
            buffer.get(data);
            byte crc = buffer.get();
            return new Builder(length, data, crc).build();
        }
    }
}
