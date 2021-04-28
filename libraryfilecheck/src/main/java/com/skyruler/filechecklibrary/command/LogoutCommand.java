package com.skyruler.filechecklibrary.command;

import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.IMessage;

public class LogoutCommand extends AbsCommand {
    private static final String COMMAND = "Logout";

    LogoutCommand(Builder builder) {
        super(builder.commandStr, builder.dataStr);
    }

    @Override
    public MessageFilter getMessageFilter() {
        return new MessageFilter() {
            @Override
            public boolean accept(IMessage msg) {
                String command = readCommandHeader(msg);
                return COMMAND.equals(command);
            }
        };
    }

    @Override
    public MessageFilter getResultHandler() {
        return new MessageFilter() {
            @Override
            public boolean accept(IMessage msg) {
                // 暂时不需要处理返回结果，一律返回true
                return msg != null;
            }
        };
    }


    public static class Builder {
        String session;

        String commandStr;
        String dataStr;

        public Builder session(String session) {
            this.session = session;
            return this;
        }

        public LogoutCommand build() {
            commandStr = "Command=" + COMMAND + "\r\n" +
                    "Session=" + session + "\r\n";
            dataStr = "";
            return new LogoutCommand(this);
        }

    }

}
