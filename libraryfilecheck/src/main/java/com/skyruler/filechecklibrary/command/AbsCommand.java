package com.skyruler.filechecklibrary.command;

import com.skyruler.filechecklibrary.message.Message;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.IMessage;

public abstract class AbsCommand {
    public static final String COMMAND_SPLIT_STR = "\r\n\r\n";

    private final String command;
    private final String data;

    protected AbsCommand(String command, String data) {
        this.command = command;
        this.data = data;
    }

    public String getCommand() {
        return command;
    }

    public String getData() {
        return data;
    }

    public abstract MessageFilter getMessageFilter();

    public abstract MessageFilter getResultHandler();

    protected String readCommandHeader(IMessage message) {
        String commandStr = ((Message) message).getCommand();
        return commandStr.split("\r\n")[0].split("=")[1];
    }


}
