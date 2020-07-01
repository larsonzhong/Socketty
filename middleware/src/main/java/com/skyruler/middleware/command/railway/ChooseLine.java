package com.skyruler.middleware.command.railway;

import com.skyruler.middleware.command.AbsCommand;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.AckMode;
import com.skyruler.socketclient.message.IMessage;

import java.nio.charset.Charset;

/**
 * 高铁路线选择命令
 */
public class ChooseLine extends AbsCommand {

    private static final byte ID = 0x10;
    private static final byte RESP_ID = 0x11;
    private static final byte RESP_FILE_READ_SUCCESS = 0x01;
    private static final byte RESP_FILE_NOT_EXIST = 0x02;

    public ChooseLine(String lineName) {
        super(ID, RESP_ID, AckMode.MESSAGE);
        super.body = lineName.getBytes(Charset.forName("GBK"));
        if (this.body.length > 32) {
            throw new IllegalArgumentException("协议规定最大32个字节");
        }
    }

    @Override
    public MessageFilter getResultHandler() {
        return new MessageFilter() {
            @Override
            public boolean accept(IMessage msg) {
                return msg != null && RESP_FILE_READ_SUCCESS == msg.getBody()[0];
            }
        };
    }
}
