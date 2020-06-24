package com.skyruler.glonavin.command.railway;

import com.skyruler.glonavin.command.AbsCommand;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.AckMode;
import com.skyruler.socketclient.message.IMessage;

public class SkipStation extends AbsCommand {
    private static final byte ID = 0x36;
    private static final byte RESP_ID = 0x37;
    private static final byte RESP_DATA_FAILED= 0x00;
    private static final byte RESP_DATA_SUCCESS = 0x01;

    public SkipStation(byte[] seqNumbers) {
        super(ID, RESP_ID, AckMode.MESSAGE);
        super.body = seqNumbers;
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
