package com.skyruler.glonavin.command;

import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.AckMode;
import com.skyruler.socketclient.message.IMessage;

public class TestControlCmd extends AbsCommand {
    private static final byte ID = 0x32;
    private static final byte RESP_ID = 0x33;
    private static final byte MODE_START = 0x00;
    private static final byte MODE_STOP = 0x01;

    private byte startMode;

    public TestControlCmd(boolean isStart) {
        super(ID, RESP_ID, AckMode.MESSAGE);
        this.startMode = parseStartMode(isStart);
        super.body = new byte[]{startMode};
    }

    private byte parseStartMode(boolean isStart) {
        return isStart ? MODE_START : MODE_STOP;
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
