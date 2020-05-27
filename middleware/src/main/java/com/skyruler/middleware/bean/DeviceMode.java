package com.skyruler.middleware.bean;

import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.filter.MessageIdFilter;

public enum DeviceMode {
    INDOOR((byte) 0x00, "人员模式"),
    SUBWAY((byte) 0x01, "地铁模式");

    private final byte msgID;
    private final byte responseID;
    private byte modeCode;
    private String desc;

    DeviceMode(byte modeCode, String value2) {
        this.msgID = (byte) 0x30;
        this.responseID = (byte) 0x31;
        this.modeCode = modeCode;
        this.desc = value2;
    }

    public byte[] getBody() {
        return new byte[]{modeCode};
    }

    public byte getMsgID() {
        return msgID;
    }

    public MessageFilter getResponseFilter() {
        return new MessageIdFilter(responseID);
    }
}
