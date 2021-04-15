package com.skyruler.socketclient.filter;

import com.skyruler.socketclient.message.IMessage;

public class MessageIdFilter implements MessageFilter {
    private final byte mId;

    public MessageIdFilter(byte id) {
        mId = id;
    }

    @Override
    public boolean accept(IMessage msg) {
        return mId == msg.getMsgId();
    }

    @Override
    public String toString() {
        return "MessageIdFilter by ID: " + mId;
    }
}
