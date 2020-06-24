package com.skyruler.glonavin.command;

import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.filter.MessageIdFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IWrappedMessage;

public class TestDirectionCmd extends AbsCommand {
    private static final byte ID = 0x34;
    private static final byte RESP_ID = 0x35;
    private static final byte RESP_DATA_SUCCESS = 0x01;

    private byte startIndex;
    private byte endIndex;

    public TestDirectionCmd(byte start, byte end) {
        super(ID);
        super.responseID = RESP_ID;
        this.startIndex = (byte) (start + 1);
        this.endIndex = (byte) (end + 1);
        super.body = new byte[]{startIndex, endIndex};
        super.ackMode = IWrappedMessage.AckMode.MESSAGE;
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
