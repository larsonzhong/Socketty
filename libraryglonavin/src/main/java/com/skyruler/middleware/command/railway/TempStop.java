package com.skyruler.middleware.command.railway;

import com.skyruler.middleware.command.AbsCommand;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.AckMode;
import com.skyruler.socketclient.message.IMessage;

public class TempStop extends AbsCommand {
    private static final byte ID = 0x38;
    private static final byte RESP_ID = 0x39;
    private static final byte RESP_DATA_FAILED= 0x00;
    private static final byte RESP_DATA_SUCCESS = 0x01;

    public TempStop(byte[] siteIds) {
        super(ID, RESP_ID, AckMode.MESSAGE);
        super.body = siteIds;
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
}
