package com.skyruler.socketclient.message;

import com.skyruler.socketclient.filter.MessageFilter;

import java.util.List;

public interface IWrappedMessage {
    byte getMessageID();

    List<IMessage> getMessages();

    MessageFilter getFilter();

    int getTimeout();

    AckMode getAckMode();

    enum AckMode {
        NON,
        PACKET,
        MESSAGE
    }
}
