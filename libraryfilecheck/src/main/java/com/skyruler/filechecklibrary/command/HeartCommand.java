package com.skyruler.filechecklibrary.command;

import com.skyruler.socketclient.filter.MessageFilter;

public class HeartCommand extends AbsCommand {
    private static final String COMMAND = "Heartbeat";

    HeartCommand(Builder builder) {
        super(builder.commandStr, builder.dataStr);
    }

    @Override
    public MessageFilter getMessageFilter() {
        return null;
    }

    @Override
    public MessageFilter getResultHandler() {
        // 登出服务器没有任何消息返回
        return null;
    }


    public static class Builder {
        String session;

        String commandStr;
        String dataStr;

        public Builder session(String session) {
            this.session = session;
            return this;
        }

        public HeartCommand build() {
            commandStr = "Command=" + COMMAND + "\r\n" +
                    "Session=" + session + "\r\n";
            dataStr = "";
            return new HeartCommand(this);
        }

    }

}