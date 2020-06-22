package com.skyruler.middleware.message;

import com.skyruler.middleware.packet.Packet;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.util.ArrayUtils;

public class Message implements IMessage {
    private static final byte[] EMPTY_BODY = new byte[0];
    private final byte msgId;
    private final byte[] body;

    private Message(Builder builder) {
        this.msgId = builder.messageID;
        this.body = builder.body == null ? EMPTY_BODY : builder.body;
    }

    @Override
    public short getMsgId() {
        return msgId;
    }

    @Override
    public byte[] getBody() {
        return body;
    }

    @Override
    public Packet[] getPackets() {
        byte[] payload = ArrayUtils.concatBytes(new byte[]{msgId}, body);
        Packet packet = new Packet.Builder(payload).build();
        return new Packet[]{packet};
    }

    static class Builder {
        byte messageID;
        byte[] body = EMPTY_BODY;

        Builder(byte msgId) {
            this.messageID = msgId;
        }

        Builder body(byte[] body) {
            this.body = body;
            return this;
        }

        IMessage build() {
            return new Message(this);
        }
    }


}
