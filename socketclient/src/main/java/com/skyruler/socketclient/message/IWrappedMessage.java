package com.skyruler.socketclient.message;

import com.skyruler.socketclient.filter.MessageFilter;

import java.util.List;

public interface IWrappedMessage {
    List<IMessage> getMessages();

    MessageFilter getMsgFilter();

    MessageFilter getResultFilter();

    int getTimeout();

    AckMode getAckMode();

    enum AckMode {
        NON,
        PACKET,
        MESSAGE
    }
}
