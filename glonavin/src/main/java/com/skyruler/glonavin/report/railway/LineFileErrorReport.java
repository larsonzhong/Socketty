package com.skyruler.glonavin.report.railway;

import com.skyruler.glonavin.report.BaseReportData;
import com.skyruler.socketclient.message.IMessage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LineFileErrorReport extends BaseReportData {
    private static final String LOC_PROVIDER_NAME = "Glonavin_Railway";
    private static final byte ID = (byte) 0x90;

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
