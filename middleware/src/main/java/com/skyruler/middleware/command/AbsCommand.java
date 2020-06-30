package com.skyruler.middleware.command;

import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.filter.MessageIdFilter;
import com.skyruler.socketclient.message.AckMode;

public abstract class AbsCommand {
    private static final int LIMIT_MESSAGE_BODY_LENGTH = 14;
    private static final int LIMIT_PACKET_BODY_LENGTH = 12;
    private static final int SEND_TIMEOUT_SHORT = 2000;
    private static final int SEND_TIMEOUT_LONG = 5000;
    private final AckMode ackMode;
    private final byte responseID;
    private final byte msgID;
    protected byte[] body;

    protected AbsCommand(byte msgID, byte responseID, AckMode ackMode) {
        this.msgID = msgID;
        this.ackMode = ackMode;
        this.responseID = responseID;
    }

    public byte[] getBody() {
        return body;
    }

    public byte getMsgID() {
        return msgID;
    }

    public byte getResponseID() {
        return responseID;
    }

    public int getTimeout() {
        if (this.ackMode == AckMode.PACKET) {
            return SEND_TIMEOUT_SHORT;
        }
        return SEND_TIMEOUT_LONG;
    }

    public int getLimitBodyLength() {
        if (this.ackMode == AckMode.PACKET) {
            return LIMIT_PACKET_BODY_LENGTH;
        }
        return LIMIT_MESSAGE_BODY_LENGTH;
    }

    public AckMode getAckMode() {
        return ackMode;
    }

    public MessageFilter getResponseFilter() {
        return new MessageIdFilter(responseID);
    }

    public abstract MessageFilter getResultHandler();
}
