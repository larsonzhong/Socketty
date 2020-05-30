package com.skyruler.middleware.message;

import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IWrappedMessage;
import com.skyruler.socketclient.util.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class WrappedMessage implements IWrappedMessage {
    private static final int DEFAULT_MESSAGE_BODY_LIMIT = 1024;
    private static final int DEFAULT_MESSAGE_TIMEOUT = 5000;
    private final byte messageID;
    private final MessageFilter filter;
    private final List<IMessage> messages;
    private final int timeout;
    private final AckMode ackMode;


    private WrappedMessage(Builder builder) {
        this.messageID = builder.msgId;
        this.messages = builder.messages;
        this.timeout = builder.timeout;
        this.filter = builder.msgFilter;
        this.ackMode = builder.ackMode;
    }

    public List<IMessage> getMessages() {
        return messages;
    }

    @Override
    public MessageFilter getResultFilter() {
        return null;
    }

    public MessageFilter getMsgFilter() {
        return filter;
    }

    public int getTimeout() {
        return timeout;
    }

    public AckMode getAckMode() {
        return ackMode;
    }

    public static class Builder {
        List<IMessage> messages;
        byte[] body;
        byte msgId;
        private int timeout;
        private AckMode ackMode;
        private int limitBodyLength;
        private MessageFilter msgFilter;
        private MessageFilter resultHandler;

        public Builder(byte msgId) {
            this.msgId = msgId;
            this.timeout = DEFAULT_MESSAGE_TIMEOUT;
            this.limitBodyLength = DEFAULT_MESSAGE_BODY_LIMIT;
        }

        public Builder body(byte[] body) {
            this.body = body;
            return this;
        }

        public Builder limitBodyLength(int bodyLength) {
            this.limitBodyLength = bodyLength;
            return this;
        }

        public Builder ackMode(AckMode ackMode) {
            this.ackMode = ackMode;
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

        public Builder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public WrappedMessage build() {
            messages = buildMessageList();
            return new WrappedMessage(this);
        }

        private List<IMessage> buildMessageList() {
            List<IMessage> messageList = new ArrayList<>();
            boolean needSplit = body.length > limitBodyLength;
            if (needSplit) {
                List<byte[]> payloads = ArrayUtils.divide(body, limitBodyLength);
                for (byte[] payload : payloads) {
                    IMessage msg = new Message.Builder(msgId).body(payload).build();
                    messageList.add(msg);
                }
            } else {
                IMessage msg = new Message.Builder(msgId).body(body).build();
                messageList.add(msg);
            }
            return messageList;
        }
    }


}
