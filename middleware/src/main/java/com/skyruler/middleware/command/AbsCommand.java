package com.skyruler.middleware.command;

import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.IWrappedMessage;

public abstract class AbsCommand {
    private final byte msgID;
    byte[] body;
    IWrappedMessage.AckMode ackMode;
    byte responseID;

    AbsCommand(byte msgID) {
        this.msgID = msgID;
        this.ackMode = IWrappedMessage.AckMode.NON;
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

    public abstract int getTimeout();

    public abstract int getLimitBodyLength();

    public IWrappedMessage.AckMode getAckMode() {
        return ackMode;
    }

    public abstract MessageFilter getMsgFilter();

    public abstract MessageFilter getResultHandler();
}
