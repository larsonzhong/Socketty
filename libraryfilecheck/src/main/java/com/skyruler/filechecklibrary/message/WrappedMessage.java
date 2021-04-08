package com.skyruler.filechecklibrary.message;

import com.skyruler.filechecklibrary.packet.Packet;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.AckMode;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IWrappedMessage;
import com.skyruler.socketclient.util.ArrayUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class WrappedMessage implements IWrappedMessage {
    private static final int DEFAULT_MESSAGE_BODY_LIMIT = 65535 - Packet.HEADER_LENGTH;
    private static final int DEFAULT_MESSAGE_TIMEOUT = 5000;
    private final List<IMessage> messages;
    private final MessageFilter filter;
    private final MessageFilter resultHandler;


    private WrappedMessage(Builder builder) {
        this.messages = builder.messages;
        this.filter = builder.msgFilter;
        this.resultHandler = builder.resultHandler;
    }

    public List<IMessage> getMessages() {
        return messages;
    }

    @Override
    public MessageFilter getResultFilter() {
        return resultHandler;
    }

    public MessageFilter getMsgFilter() {
        return filter;
    }

    @Override
    public int getTimeout() {
        return DEFAULT_MESSAGE_TIMEOUT;
    }

    @Override
    public AckMode getAckMode() {
        return AckMode.PACKET;
    }

    public static class Builder {
        List<IMessage> messages;
        String command;
        String data;
        private MessageFilter msgFilter;
        private MessageFilter resultHandler;

        public Builder() {
        }

        public Builder command(String command) {
            this.command = command;
            return this;
        }

        public Builder data(String data) {
            this.data = data;
            return this;
        }

        public Builder msgFilter(MessageFilter filter) {
            this.msgFilter = filter;
            return this;
        }

        public Builder resultHandler(MessageFilter filter) {
            this.resultHandler = filter;
            return this;
        }

        public WrappedMessage build() {
            messages = buildMessageList();
            return new WrappedMessage(this);
        }

        private List<IMessage> buildMessageList() {
            List<IMessage> messageList = new ArrayList<>();
            IMessage message = new Message.Builder()
                    .command(command)
                    .data(data)
                    .build();
            byte[] bytes = message.getBody();

            boolean needSplit = bytes != null && bytes.length > DEFAULT_MESSAGE_BODY_LIMIT;
            if (!needSplit) {
                messageList.add(message);
                return messageList;
            }

            // TCP/IP 数据包最大为65535 Bytes。超过该大小的数据包，必须拆开发送
            // 包拆分规则暂定为：将除length外的数据分割发送
            List<byte[]> payloads = ArrayUtils.divide(bytes, DEFAULT_MESSAGE_BODY_LIMIT);
            for (short i = 0; i < payloads.size(); i++) {
                byte[] payload = payloads.get(i);
                byte[] data = ByteBuffer
                        .allocate(DEFAULT_MESSAGE_BODY_LIMIT + Packet.HEADER_LENGTH)
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .putShort((short) payload.length)
                        .put(payload)
                        .array();
                IMessage msg = new Message(data);
                messageList.add(msg);
            }
            return messageList;
        }
    }


}
