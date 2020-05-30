package com.skyruler.middleware.command;

import com.skyruler.middleware.xml.model.MetroLine;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.filter.MessageIdFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IWrappedMessage;

public class MetroLineCmd extends AbsCommand {
    private static final byte ID = 0x20;
    private static final byte RESP_ID = 0x21;
    private static final byte RESP_DATA_SUCCESS = 0x01;
    private final String lineName;

    public MetroLineCmd(MetroLine metroLine) {
        super(ID);
        super.responseID = RESP_ID;
        super.body = metroLine.toBytes();
        super.ackMode = IWrappedMessage.AckMode.PACKET;
        this.lineName = metroLine.getName();
    }

    @Override
    public int getTimeout() {
        return 1000;
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
                return RESP_DATA_SUCCESS == msg.getBody()[0];
            }
        };
    }

    @Override
    public String toString() {
        return "MetroLineCmd{" +
                "lineName='" + lineName + '\'' +
                '}';
    }
}
