package com.larson.socketty.library.message;


import com.larson.socketty.library.filter.MessageFilter;

import java.util.List;

public interface IWrappedMessage {
    List<IMessage> getMessages();

    MessageFilter getMsgFilter();

    MessageFilter getResultFilter();

    int getTimeout();

    AckMode getAckMode();

}
