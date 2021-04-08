package com.skyruler.filechecklibrary.message;

import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageStrategy;
import com.skyruler.socketclient.message.IPacket;

public class MessageStrategy implements IMessageStrategy {

    @Override
    public IMessage parse(IPacket packet) {
        // messageID是固定data的第一个字节
        byte[] packetData = packet.getData();
        String content = new String(packetData);
        String[] strings = content.split("\r\n\r\n");
        String command = strings[0];
        String data = strings[1];
        return new Message.Builder()
                .command(command)
                .data(data)
                .build();
    }
}
