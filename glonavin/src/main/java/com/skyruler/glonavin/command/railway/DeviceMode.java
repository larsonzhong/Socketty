package com.skyruler.glonavin.command.railway;

import com.skyruler.glonavin.command.AbsCommand;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.AckMode;
import com.skyruler.socketclient.message.IMessage;

public class DeviceMode extends AbsCommand {
    private static final byte ID = 0x30;
    private static final byte RESP_ID = 0x31;
    private static final byte RESP_FILE_READ_SUCCESS = 0x01;
    private static final byte RESP_FILE_NOT_EXIST = 0x02;

    enum Mode {
        SelfCheck("自检模式", (byte) 0x03),
        Railway("高铁模式", (byte) 0x04),
        Road("公路模式", (byte) 0x05);

        private final String name;
        private final byte mode;

        Mode(String name, byte mode) {
            this.name = name;
            this.mode = mode;
        }
    }

    public DeviceMode(Mode locMode) {
        super(ID, RESP_ID, AckMode.MESSAGE);
        super.body = new byte[]{locMode.mode};
    }

    @Override
    public MessageFilter getResultHandler() {
        return new MessageFilter() {
            @Override
            public boolean accept(IMessage msg) {
                return msg != null && RESP_FILE_READ_SUCCESS == msg.getBody()[0];
            }
        };
    }
}
