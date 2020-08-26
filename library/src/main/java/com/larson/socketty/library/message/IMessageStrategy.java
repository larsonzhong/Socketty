package com.larson.socketty.library.message;

public interface IMessageStrategy {

    IMessage parse(IPacket packet);
}
