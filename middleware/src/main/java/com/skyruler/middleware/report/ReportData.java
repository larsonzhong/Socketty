package com.skyruler.middleware.report;

import com.skyruler.socketclient.message.IMessage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ReportData {
    private static final byte STATE_SPEED_DOWN = 0x01;
    private static final byte STATE_SPEED_AVG = 0x02;
    private static final byte STATE_SPEED_UP = 0x03;
    private static final byte STATE_START_UP = 0x04;

    private static final byte LOCATION_DATA_VALID = 0X01;
    private static final byte LOCATION_DATA_ERROR = 0x00;

    private final short seqNum;
    private final byte siteID;
    private final boolean isValidLoc;
    private final byte gpsState;
    private final float longitude;
    private final float latitude;
    private final byte battery;

    public ReportData(IMessage msg) {
        byte[] raw = msg.getBody();
        ByteBuffer buffer = ByteBuffer.wrap(raw);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.rewind();

        this.seqNum = buffer.getShort();
        this.siteID = buffer.get();
        this.isValidLoc = buffer.get() == LOCATION_DATA_VALID;
        this.gpsState = buffer.get();
        this.longitude = buffer.getFloat();
        this.latitude = buffer.getFloat();
        this.battery = buffer.get();
    }


    public short getSeqNum() {
        return seqNum;
    }

    public byte getSiteID() {
        return siteID;
    }

    public boolean getIsValidLoc() {
        return isValidLoc;
    }

    public byte getGpsState() {
        return gpsState;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public byte getBattery() {
        return battery;
    }

    @Override
    public String toString() {
        return "ReportData{" +
                ", seqNum=" + seqNum +
                ", siteID=" + siteID +
                ", isValidLoc=" + isValidLoc +
                ", gpsState=" + gpsState +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", battery=" + battery +
                '}';
    }

    public String getGpsStateStr() {
        switch (gpsState) {
            case STATE_SPEED_DOWN:
                return "减速";
            case STATE_SPEED_AVG:
                return "匀速";
            case STATE_SPEED_UP:
                return "加速";
            case STATE_START_UP:
                return "启动";
            default:
                return "";
        }
    }
}
