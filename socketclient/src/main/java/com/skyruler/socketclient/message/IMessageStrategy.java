package com.skyruler.socketclient.message;

public interface IMessageStrategy {

    IMessage parse(IPacket packet);
}
