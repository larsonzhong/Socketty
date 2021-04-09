package com.skyruler.filechecklibrary.command;

import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.IMessage;

public class LoginCommand extends AbsCommand {

    LoginCommand(Builder builder) {
        super(builder.commandStr, builder.dataStr);
    }

    @Override
    public MessageFilter getMessageFilter() {
        return new MessageFilter() {
            @Override
            public boolean accept(IMessage msg) {
                return msg != null;
            }
        };
    }

    public static class Builder {
        String command;
        String imei;
        String pass;
        String sver;
        String cver;

        String commandStr;
        String dataStr;

        public Builder command(String command) {
            this.command = command;
            return this;
        }

        public Builder imei(String user) {
            this.imei = user;
            return this;
        }

        public Builder pass(String pass) {
            this.pass = pass;
            return this;
        }

        public Builder sver(String sver) {
            this.sver = sver;
            return this;
        }

        public Builder cver(String cver) {
            this.cver = cver;
            return this;
        }

        public LoginCommand build() {
            commandStr = new StringBuilder()
                    .append("[Request]").append("\r\n")
                    .append("Command=").append(command).append("\r\n")
                    .append("User=").append(imei).append("\r\n")
                    .append("Pass=").append(pass).append("\r\n")
                    .append("Sver=").append(sver).append("\r\n")
                    .append("Cver=").append(cver).append("\r\n")
                    .toString();
            dataStr = "";
            return new LoginCommand(this);
        }

    }
}
