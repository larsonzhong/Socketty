package com.skyruler.filechecklibrary.command;

import com.skyruler.socketclient.filter.MessageFilter;

public abstract class AbsCommand {
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
}
