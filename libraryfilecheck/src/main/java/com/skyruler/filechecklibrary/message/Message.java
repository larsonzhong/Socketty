package com.skyruler.filechecklibrary.message;

import android.text.TextUtils;

import com.skyruler.filechecklibrary.packet.Packet;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.util.ArrayUtils;

import static com.skyruler.filechecklibrary.command.AbsCommand.COMMAND_SPLIT_STR;

public class Message implements IMessage {
    private final byte[] payload;
    private final String command;
    private final String data;

    public Message(byte[] payload) {
        this.payload = payload;
        String[] strings = readCommandStrs(payload);
        command = strings.length > 0 ? strings[0] : null;
        data = strings.length > 1 ? strings[1] : null;
    }

    public Message(Builder builder) {
        this.payload = builder.payload;
        this.command = builder.command;
        this.data = builder.data;
    }

    private String[] readCommandStrs(byte[] payload) {
        // 通常情况下，服务器返回一个packet足够
        String s1 = new String(payload);
        if (TextUtils.isEmpty(s1)) {
            return new String[]{};
        }
        return s1.split(COMMAND_SPLIT_STR);
    }

    public String getCommand() {
        return command;
    }

    public String getData() {
        return data;
    }

    @Override
    public short getMsgId() {
        // 由于文件上传协议不带msgId校验，因此统一为0
        return 0;
    }

    @Override
    public byte[] getBody() {
        return payload;
    }

    @Override
    public Packet[] getPackets() {
        Packet packet = new Packet.Builder(payload).build();
        return new Packet[]{packet};
    }

    public static class Builder {
        String command;
        String data;
        byte[] payload;

        public Builder command(String command) {
            this.command = command;
            return this;
        }

        public Builder data(String data) {
            this.data = data;
            return this;
        }

        /**
         * 字段    内容
         * 指令    command或空
         * 分隔符  空行
         * 数据    数据，可以为空
         */
        public IMessage build() {
            byte[] commandInBytes = command.getBytes();
            byte[] dataInBytes = data.getBytes();
            byte[] splitBytes = "\r\n".getBytes();
            payload = ArrayUtils.concatBytes(commandInBytes, splitBytes, dataInBytes);
            return new Message(this);
        }
    }


}
