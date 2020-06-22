package com.skyruler.middleware.command;

import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.filter.MessageIdFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IWrappedMessage;

public class TestControlCmd extends AbsCommand {
    private static final byte ID = 0x32;
    private static final byte RESP_ID = 0x33;
    private static final byte MODE_START = 0x00;
    private static final byte MODE_STOP = 0x01;

    private byte startMode;

    public TestControlCmd(boolean isStart) {
        super(ID);
        super.responseID = RESP_ID;
        this.startMode = parseStartMode(isStart);
        super.body = new byte[]{startMode};
        super.ackMode = IWrappedMessage.AckMode.MESSAGE;
    }

    private byte parseStartMode(boolean isStart) {
        return isStart ? MODE_START : MODE_STOP;
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
                return msg != null && (MODE_START == msg.getBody()[0] || MODE_STOP == msg.getBody()[0]);
            }
        };
    }

    @Override
    public String toString() {
        return "TestControlCmd{" +
                "startMode=" + startMode +
                '}';
    }

    public boolean isStartTest() {
        return startMode == MODE_START;
    }
}
