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
        return 2000;
    }

    @Override
    public int getLimitBodyLength() {
        return 12;
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
                return msg != null && RESP_DATA_SUCCESS == msg.getBody()[2];
            }
        };
    }

    @Override
    public String toString() {
        /*int count = body.length;
        int index = 0;
        while (count > 0) {
            byte[] sub;
            if (count > 170) {
                sub = new byte[170];
            } else {
                sub = new byte[count];
            }
            System.arraycopy(body, index, sub, 0, sub.length);
            String s = ArrayUtils.bytesToHex(sub);
            Log.d("MetroLineCmd", s);
            count -= sub.length;
            index += sub.length;
        }*/
        return "MetroLineCmd{" +
                "lineName='" + lineName + '\'' +
                '}';
    }
}
