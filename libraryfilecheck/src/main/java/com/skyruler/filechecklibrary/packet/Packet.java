package com.skyruler.filechecklibrary.packet;

import com.skyruler.socketclient.message.IPacket;
import com.skyruler.socketclient.util.CRCCheck;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Packet implements IPacket {
    static final byte HEADER = 0x24;
    private final byte pkgType;
    private final short length;
    private final byte[] data;

    private Packet(Builder builder) {
        this.length = builder.length;
        this.pkgType = builder.pkgType;
        this.data = builder.data;
    }

    public short getLength() {
        return length;
    }

    public byte[] getData() {
        return data;
    }

    public byte[] getBytes() {
        //固定帧头2+数据长度2+有效数据length+Crc1
        int totalLen = 2 + 2 + length + 1;
        ByteBuffer byteBuffer = ByteBuffer.allocate(totalLen);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(HEADER);
        byteBuffer.put(pkgType);
        byteBuffer.putShort(length);
        byteBuffer.put(data);
        byte[] array = byteBuffer.array();
        byte crc = CRCCheck.checkSumCrc8(array, array.length - 1);
        array[array.length - 1] = crc;
        return array;
    }

    //00100100 11010000 00000000 00000010 00110000 00000001 01010110
    // 0x24      0xD0    0x00      0x02     0x30     0x01     0x56

    public static class Builder {
        byte pkgType;
        short length;
        byte[] data;

        public Builder(byte[] validData) {
            this.pkgType = (byte) 0xD0;
            this.data = validData;
            this.length = (short) validData.length;
        }

        Builder(byte pkgType, short length, byte[] data) {
            this.pkgType = pkgType;
            this.length = length;
            this.data = data;
        }

        public Packet build() {
            return new Packet(this);
        }
    }


}
