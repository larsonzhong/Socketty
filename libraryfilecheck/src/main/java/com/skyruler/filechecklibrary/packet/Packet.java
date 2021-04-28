package com.skyruler.filechecklibrary.packet;

import com.skyruler.socketclient.message.IPacket;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * 包组成如下：
 * Length       int     2	每个TCP/IP包长度，最大不超过65535字节
 * packet       char   N	接口分组包
 */
public class Packet implements IPacket {
    /*包头length长度为2*/
    public static final int HEADER_LENGTH = 2;
    private final short length;
    private final byte[] data;

    private Packet(Builder builder) {
        this.length = builder.length;
        this.data = builder.data;
    }

    public short getLength() {
        return length;
    }

    public byte[] getData() {
        return data;
    }

    public byte[] getBytes() {
        // 固定帧头（长度）2+有效数据length
        int totalLen = HEADER_LENGTH + length;
        ByteBuffer byteBuffer = ByteBuffer.allocate(totalLen);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putShort(length);
        byteBuffer.put(data);
        return byteBuffer.array();
    }


    public static class Builder {
        final short length;
        final byte[] data;

        public Builder(byte[] validData) {
            this.data = validData;
            this.length = (short) validData.length;
        }

        public Builder(short length, byte[] data) {
            this.length = length;
            this.data = data;
        }

        public Packet build() {
            return new Packet(this);
        }
    }


}
