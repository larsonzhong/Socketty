package com.skyruler.filechecklibrary.message;

import com.skyruler.filechecklibrary.packet.Packet;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.util.ArrayUtils;

import java.nio.charset.StandardCharsets;

public class Message implements IMessage {
    private static final byte[] EMPTY_BODY = new byte[0];
    private final byte[] payload;

    public Message(byte[] payload) {
        this.payload = payload;
    }

    @Override
    public short getMsgId() {
        // 由于文件上传协议不带msgId校验，因此统一为0
        return 0;
    }

    @Override
    public byte[] getBody() {
        return payload;
    }

    @Override
    public Packet[] getPackets() {
        Packet packet = new Packet.Builder(payload).build();
        return new Packet[]{packet};
    }

    static class Builder {
        byte[] commandInBytes;
        byte[] data;

        Builder command(String command) {
            this.commandInBytes = command.getBytes(StandardCharsets.UTF_8);
            return this;
        }

        Builder data(String data) {
            this.data = data == null ? EMPTY_BODY : data.getBytes(StandardCharsets.UTF_8);
            return this;
        }

        /**
         * 字段    内容
         * 指令    command或空
         * 分隔符  空行
         * 数据    数据，可以为空
         */
        IMessage build() {
            byte[] splitBytes = "\r\n".getBytes();
            byte[] payload = ArrayUtils.concatBytes(commandInBytes, splitBytes, data);
            return new Message(payload);
        }
    }


}
