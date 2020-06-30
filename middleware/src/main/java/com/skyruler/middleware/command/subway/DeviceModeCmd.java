package com.skyruler.middleware.command.subway;

import com.skyruler.middleware.command.AbsCommand;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.AckMode;
import com.skyruler.socketclient.message.IMessage;

public class DeviceModeCmd extends AbsCommand {
    private static final byte ID = 0x30;
    private static final byte RESP_ID = 0x31;
    public static final byte MODE_INDOOR = 0x00;
    public static final byte MODE_SUBWAY = 0x01;
    public static final byte MODE_RAILWAY = 0x02;
    private static final byte RESP_DATA_SUCCESS = 0x01;

    private byte modeCode;

    public DeviceModeCmd(byte modeCode) {
        super(ID, RESP_ID, AckMode.MESSAGE);
        this.modeCode = modeCode;
        super.body = new byte[]{modeCode};
    }

    @Override
    public MessageFilter getResultHandler() {
        return new MessageFilter() {
            @Override
            public boolean accept(IMessage msg) {
                return msg != null && RESP_DATA_SUCCESS == msg.getBody()[0];
            }
        };
    }

    @Override
    public String toString() {
        return "DeviceModeCmd{" +
                "modeCode=" + modeCode +
                '}';
    }
}
