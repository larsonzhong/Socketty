package com.skyruler.socketclient.filter;

import com.skyruler.socketclient.message.Message;

public class MessageIdFilter implements MessageFilter {
    private byte mId;

    public MessageIdFilter(byte id) {
        mId = id;
    }

    @Override
    public boolean accept(Message msg) {
        return mId == msg.getMsgId();
    }

    @Override
    public String toString() {
        return "MessageIdFilter by ID: " + mId;
    }
}
