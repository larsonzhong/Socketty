package com.skyruler.glonavin.command.subway;

import com.skyruler.glonavin.command.AbsCommand;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.AckMode;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IWrappedMessage;
import com.skyruler.socketclient.util.ArrayUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EditionCommand extends AbsCommand {
    private static final byte ID = 0x60;
    private static final byte RESP_ID = 0x61;
    private EditionCallBack callBack;

    public static class Edition {
        private short softVersionName;
        private short hardVersionName;
        private short portoVersionName;

        Edition(short softVersionName, short hardVersionName, short portoVersionName) {
            this.softVersionName = softVersionName;
            this.hardVersionName = hardVersionName;
            this.portoVersionName = portoVersionName;
        }

        public String getSoftVersionName() {
            byte[] bytes = ArrayUtils.shortToBytes(softVersionName);
            return "V" + bytes[0] + "." + bytes[1];
        }

        public String getHardVersionName() {
            byte[] bytes = ArrayUtils.shortToBytes(hardVersionName);
            return "V" + bytes[0] + "." + bytes[1];
        }

        public String getPortoVersionName() {
            byte[] bytes = ArrayUtils.shortToBytes(portoVersionName);
            return "V" + bytes[0] + "." + bytes[1];
        }
    }

    public interface EditionCallBack {
        void handleEdition(Edition edition);
    }

    public EditionCommand(EditionCallBack callBack) {
        super(ID, RESP_ID, AckMode.MESSAGE);
        this.callBack = callBack;
        super.body = new byte[2];
    }

    @Override
    public MessageFilter getResultHandler() {
        return new MessageFilter() {
            @Override
            public boolean accept(IMessage msg) {
                byte[] raw = msg.getBody();
                ByteBuffer buffer = ByteBuffer.wrap(raw);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                buffer.rewind();

                byte id = buffer.get();
                short softVersion = buffer.getShort();
                short hardVersion = buffer.getShort();
                short protocolVersion = buffer.getShort();

                Edition edition = new Edition(softVersion, hardVersion, protocolVersion);
                callBack.handleEdition(edition);
                return id == RESP_ID;
            }
        };
    }

}
