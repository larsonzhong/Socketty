package com.skyruler.socketclient.filter;

import com.skyruler.socketclient.message.Message;

public interface MessageFilter {
    boolean accept(Message msg);
}
