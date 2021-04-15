package com.skyruler.middleware.command;

import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.AckMode;
import com.skyruler.socketclient.message.IMessage;

public class TestDirectionCmd extends AbsCommand {
    private static final byte ID = 0x34;
    private static final byte RESP_ID = 0x35;
    private static final byte RESP_DATA_SUCCESS = 0x01;

    private final byte startIndex;
    private final byte endIndex;

    public TestDirectionCmd(byte start, byte end) {
        super(ID, RESP_ID, AckMode.MESSAGE);
        this.startIndex = (byte) (start + 1);
        this.endIndex = (byte) (end + 1);
        super.body = new byte[]{startIndex, endIndex};
    }

    @Override
    public MessageFilter getResultHandler() {
        return new MessageFilter() {
            @Override
            public boolean accept(IMessage msg) {
                if (msg == null) {
                    return false;
                }
                return RESP_DATA_SUCCESS == msg.getBody()[0];
            }
        };
    }

    @Override
    public String toString() {
        return "TestDirectionCmd{" +
                "startIndex=" + startIndex +
                ", endIndex=" + endIndex +
                '}';
    }
}
