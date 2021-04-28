package com.skyruler.filechecklibrary.command;

import android.util.Log;

import com.skyruler.filechecklibrary.command.result.Session;
import com.skyruler.filechecklibrary.message.Message;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.IMessage;

public class LoginCommand extends AbsCommand {

    private static final String COMMAND_HEADER = "Login";
    private LoginCallback loginCallback;

    LoginCommand(Builder builder) {
        super(builder.commandStr, builder.dataStr);
        this.loginCallback = builder.loginCallback;
    }

    @Override
    public MessageFilter getMessageFilter() {
        return new MessageFilter() {
            @Override
            public boolean accept(IMessage msg) {
                String command = readCommandHeader(msg);
                return COMMAND_HEADER.equals(command);
            }
        };
    }

    @Override
    public MessageFilter getResultHandler() {
        return new MessageFilter() {
            @Override
            public boolean accept(IMessage msg) {
                if (msg == null) {
                    loginCallback.onLoginTimeout();
                    return false;
                }

                String commandStr = ((Message) msg).getCommand();
                String[] strings = commandStr.split("\r\n");


                String session = strings[1].split("=")[1];
                //AC/NAC#代表Request处理结果,Accept和Not accept
                boolean logged = strings[2] != null && strings[2].split("=")[1].equals("AC");
                String code = logged ? null : strings[3].split("=")[1];
                boolean updateSoftware = strings[4] != null && strings[4].split("=")[1].equals("Y");
                boolean updateConfig = strings[5] != null && strings[5].split("=")[1].equals("Y");

                Session loginResult = new Session(logged, session, code, updateSoftware, updateConfig);
                loginCallback.onLoginResponse(loginResult);
                Log.i("LoginCommand", "login result :" + session);
                return true;
            }
        };
    }

    public interface LoginCallback {
        /**
         * on server return login state
         *
         * @param loginResult logged Session
         */
        void onLoginResponse(Session loginResult);

        /**
         * return null when login timeout
         */
        void onLoginTimeout();
    }

    public static class Builder {
        String command;
        String imei;
        String pass;
        String sver;
        String cver;

        String commandStr;
        String dataStr;
        private LoginCallback loginCallback;

        public Builder(LoginCallback loginCallback) {
            this.command = COMMAND_HEADER;
            this.loginCallback = loginCallback;
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
            commandStr = "Command=" + command + "\r\n" +
                    "User=" + imei + "\r\n" +
                    "Pass=" + pass + "\r\n" +
                    "Sver=" + sver + "\r\n" +
                    "Cver=" + cver + "\r\n";
            dataStr = "";
            return new LoginCommand(this);
        }

    }
}
