package com.skyruler.socketclient.intf;

import com.skyruler.socketclient.message.Message;

public interface IMessageListener {
    void processMessage(Message msg);
}