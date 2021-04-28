package com.skyruler.filechecklibrary.connection;

import com.skyruler.filechecklibrary.message.Message;
import com.skyruler.filechecklibrary.packet.Packet;
import com.skyruler.socketclient.connection.socket.conf.SocketConnectOption;
import com.skyruler.socketclient.connection.socket.remote.RemoteSocketConnectOption;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageListener;
import com.skyruler.socketclient.message.IMessageStrategy;
import com.skyruler.socketclient.message.IPacket;
import com.skyruler.socketclient.message.IPacketStrategy;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.skyruler.filechecklibrary.command.AbsCommand.COMMAND_SPLIT_STR;


public class FileCheckConnectOption extends RemoteSocketConnectOption {

    private FileCheckConnectOption(Builder builder) {
        super(builder.host,
                builder.port,
                builder.skSocketOption,
                false,
                builder.mWrappers,
                null);
    }

    @Override
    public IPacketStrategy getPacketConstructor() {
        return new IPacketStrategy() {
            private static final int HEAD_LENGTH = 2;

            @Override
            public IPacket parse(byte[] raw) {
                ByteBuffer buffer = ByteBuffer.wrap(raw);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                buffer.rewind();
                if (raw.length <= HEAD_LENGTH) {
                    // =2表示只发了头（包长），不需要处理
                    return null;
                }
                short length = buffer.getShort();
                if (length < 2 || length > raw.length - HEAD_LENGTH) {
                    // 包不全不处理
                    return null;
                }
                byte[] data = new byte[length];
                buffer.get(data);

                return new Packet.Builder(length, data).build();
            }
        };
    }

    @Override
    public IMessageStrategy getMessageConstructor() {
        return new IMessageStrategy() {

            @Override
            public IMessage parse(IPacket packet) {
                // messageID是固定data的第一个字节
                byte[] packetData = packet.getData();
                String content = new String(packetData);
                String[] strings = content.split(COMMAND_SPLIT_STR);
                String command = strings[0];
                String data = strings.length > 1 ? strings[1] : "";
                return new Message.Builder()
                        .command(command)
                        .data(data)
                        .build();
            }
        };
    }

    @Override
    public ConnectionType getType() {
        return ConnectionType.SOCKET;
    }

    public static class Builder {
        private String host;
        private int port;
        private SocketConnectOption skSocketOption;
        private final Map<MessageFilter, IMessageListener> mWrappers;

        public Builder() {
            mWrappers = new LinkedHashMap<>();
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder skSocketOption(SocketConnectOption option) {
            this.skSocketOption = option;
            return this;
        }

        public Builder addMessageListener(MessageFilter filter, IMessageListener listener) {
            this.mWrappers.put(filter, listener);
            return this;
        }

        public FileCheckConnectOption build() {
            return new FileCheckConnectOption(this);
        }


    }
}
