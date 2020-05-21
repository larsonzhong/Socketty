package com.skyruler.socketclient.message;

public interface IMessageListener {
    void processMessage(IMessage msg);
}