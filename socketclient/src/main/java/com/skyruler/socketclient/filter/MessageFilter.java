package com.skyruler.socketclient.filter;


import com.skyruler.socketclient.message.IMessage;

public interface MessageFilter {

    /**
     * Whether this message is an desired message
     *
     * @param msg Message to be processed
     * @return acceptable
     */
    boolean accept(IMessage msg);
}
