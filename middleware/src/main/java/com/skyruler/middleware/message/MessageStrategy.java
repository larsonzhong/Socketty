package com.skyruler.middleware.message;

import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageStrategy;
import com.skyruler.socketclient.message.IPacket;
import com.skyruler.socketclient.util.ArrayUtils;

public  class MessageStrategy implements IMessageStrategy {

    @Override
    public IMessage parse(IPacket packet) {
        // messageID是固定data的第一个字节
        byte[] raw = packet.getData();
        byte messageID = raw[0];
        byte[] body = ArrayUtils.subBytes(raw, 1, raw.length - 1);
        return new Message.Builder(messageID).body(body).build();
    }
}
