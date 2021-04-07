package com.skyruler.middleware.report.railway;

import com.skyruler.middleware.report.BaseReportData;
import com.skyruler.socketclient.message.IMessage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LineFileErrorReport extends BaseReportData {
    public static final byte REPORT_ID = (byte) 0x90;

    private final byte optState;
    private final byte readState;

    public LineFileErrorReport(IMessage msg) {
        byte[] raw = msg.getBody();
        ByteBuffer buffer = ByteBuffer.wrap(raw);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.rewind();

        this.optState = buffer.get();
        this.readState = buffer.get();
    }

    public byte getOptState() {
        return optState;
    }

    public byte getReadState() {
        return readState;
    }
}
