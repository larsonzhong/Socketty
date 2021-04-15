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
            commandStr = "[Request]" + "\r\n" +
                    "Command=" + command + "\r\n" +
                    "User=" + imei + "\r\n" +
                    "Pass=" + pass + "\r\n" +
                    "Sver=" + sver + "\r\n" +
                    "Cver=" + cver + "\r\n";
            dataStr = "";
            return new LoginCommand(this);
        }

    }
}
