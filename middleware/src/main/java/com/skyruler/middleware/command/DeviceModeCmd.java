package com.skyruler.middleware.command;

import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.filter.MessageIdFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IWrappedMessage;

public class DeviceModeCmd extends AbsCommand {
    private static final byte ID = 0x30;
    private static final byte RESP_ID = 0x31;
    private static final byte MODE_INDOOR = 0x00;
    private static final byte MODE_SUBWAY = 0x01;
    private static final byte MODE_RAILWAY = 0x02;
    private static final byte RESP_DATA_SUCCESS = 0x01;

    private byte modeCode;

    public DeviceModeCmd(String modeString) {
        super(ID);
        super.responseID = RESP_ID;
        this.modeCode = parseModeCode(modeString);
        super.body = new byte[]{modeCode};
        super.ackMode = IWrappedMessage.AckMode.MESSAGE;
    }

    private byte parseModeCode(String modeString) {
        switch (modeString) {
            case "地铁模式":
                return MODE_SUBWAY;
            case "人员模式":
                return MODE_INDOOR;
            case "高铁模式":
                return MODE_RAILWAY;
            default:
                return MODE_SUBWAY;
        }
    }

    @Override
    public int getTimeout() {
        return SEND_TIMEOUT_LONG;
    }

    @Override
    public int getLimitBodyLength() {
        return 14;
    }

    @Override
    public MessageFilter getMsgFilter() {
        return new MessageIdFilter(responseID);
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
