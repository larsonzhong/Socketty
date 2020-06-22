package com.skyruler.middleware.command;

import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.filter.MessageIdFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IWrappedMessage;

public class TempStopStationCmd extends AbsCommand {
    private static final byte ID = 0x38;
    private static final byte RESP_ID = 0x39;
    private static final byte RESP_DATA_SUCCESS = 0x01;

    public TempStopStationCmd() {
        super(ID);
        super.responseID = RESP_ID;
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
                return msg != null && RESP_DATA_SUCCESS == msg.getBody()[0];
            }
        };
    }

}