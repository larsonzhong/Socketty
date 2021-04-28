package com.skyruler.middleware.command;

import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.AckMode;
import com.skyruler.socketclient.message.IMessage;

public class TestDirectionCmd extends AbsCommand {
    private static final byte ID = 0x34;
    private static final byte RESP_ID = 0x35;
    private static final byte RESP_DATA_SUCCESS = 0x01;

    private byte startSid;
    private byte endSid;

    public TestDirectionCmd(byte startSid, byte endSid) {
        super(ID, RESP_ID, AckMode.MESSAGE);
        this.startSid = startSid;
        this.endSid = endSid;
        super.body = new byte[]{this.startSid, this.endSid};
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
                "startIndex=" + startSid +
                ", endIndex=" + endSid +
                '}';
    }
}
