package com.skyruler.middleware.command.subway;

import com.skyruler.middleware.command.AbsCommand;
import com.skyruler.middleware.parser.xml.model.MetroLine;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.AckMode;
import com.skyruler.socketclient.message.IMessage;

public class MetroLineCmd extends AbsCommand {
    private static final byte ID = 0x20;
    private static final byte RESP_ID = 0x21;
    private static final byte RESP_DATA_SUCCESS = 0x01;
    private final String lineName;
    private final MetroLine metroLine;

    public MetroLineCmd(MetroLine metroLine) {
        super(ID,RESP_ID, AckMode.PACKET);
        super.body = metroLine.toBytes();
        this.lineName = metroLine.getName();
        this.metroLine = metroLine;
    }

    public MetroLine getMetroLine() {
        return metroLine;
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
