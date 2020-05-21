package com.skyruler.socketclient.message;

public interface IMessageConstructor {

    IMessage parse(IPacket packet);
}
