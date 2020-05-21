package com.skyruler.socketclient.filter;


import com.skyruler.socketclient.message.IMessage;

public interface MessageFilter {
    boolean accept(IMessage msg);
}
