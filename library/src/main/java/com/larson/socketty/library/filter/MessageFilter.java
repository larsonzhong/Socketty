package com.larson.socketty.library.filter;


import com.larson.socketty.library.message.IMessage;

public interface MessageFilter {
    boolean accept(IMessage msg);
}
