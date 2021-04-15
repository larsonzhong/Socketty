package com.skyruler.middleware.command.railway;

import com.skyruler.middleware.command.AbsCommand;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.AckMode;
import com.skyruler.socketclient.message.IMessage;

import java.nio.charset.Charset;

public class GetLineName extends AbsCommand {

    private static final byte ID = (byte) 0x80;
    private static final byte RESP_ID = (byte) 0x81;
    private final LineNameCallBack callBack;

    public interface LineNameCallBack {
        void handleLineName(String lineName);
    }

    public GetLineName(LineNameCallBack callBack) {
        super(ID, RESP_ID, AckMode.MESSAGE);
        this.callBack = callBack;
        super.body = new byte[]{};
    }

    @Override
    public MessageFilter getResultHandler() {
        return new MessageFilter() {
            @Override
            public boolean accept(IMessage msg) {
                if (msg.getBody() == null || msg.getBody().length <= 0) {
                    return false;
                }
                byte[] raw = msg.getBody();
                String lineName = new String(raw, Charset.forName("GBK"));
                callBack.handleLineName(lineName);
                return true;
            }
        };
    }
}
