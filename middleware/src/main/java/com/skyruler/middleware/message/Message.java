package com.skyruler.middleware.message;

import com.skyruler.middleware.packet.Packet;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageConstructor;
import com.skyruler.socketclient.message.IPacket;
import com.skyruler.socketclient.util.ArrayUtils;

public class Message implements IMessage {
    private static final byte[] EMPTY_BODY = new byte[0];
    private final byte msgId;
    private final byte[] body;

    private Message(Builder builder) {
        this.msgId = builder.messageID;
        this.body = builder.body;
    }

    @Override
    public short getMsgId() {
        return 0;
    }

    @Override
    public byte[] getBody() {
        return new byte[0];
    }

    @Override
    public Packet[] getPackets() {
        byte[] payload = ArrayUtils.concatBytes(new byte[]{msgId}, body);
        Packet packet = new Packet.Builder(payload).build();
        return new Packet[]{packet};
    }

    public static class Builder {
        byte messageID;
        byte[] body = EMPTY_BODY;

        Builder(byte msgId) {
            this.messageID = msgId;
        }

        Builder body(byte[] body) {
            this.body = body;
            return this;
        }

        public Builder(Packet packet) {
            // messageID是固定data的第一个字节
            this.messageID = packet.getData()[0];
            this.body = ArrayUtils.subBytes(packet.getData(), 1, packet.getData().length - 1);
        }

        IMessage build() {
            return new Message(this);
        }
    }

    public static class Constructor implements IMessageConstructor {

        @Override
        public IMessage parse(IPacket packet) {
            return null;
        }
    }
}
