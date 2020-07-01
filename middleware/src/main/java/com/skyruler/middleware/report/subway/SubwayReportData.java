package com.skyruler.middleware.report.subway;

import android.location.Location;

import com.skyruler.middleware.report.BaseReportData;
import com.skyruler.socketclient.message.IMessage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SubwayReportData extends BaseReportData {
    public static final byte REPORT_ID = 0x40;

    public static final byte STATE_SPEED_STILL = 0x00;
    private static final byte STATE_SPEED_DOWN = 0x01;
    private static final byte STATE_SPEED_AVG = 0x02;
    private static final byte STATE_SPEED_UP = 0x03;
    public static final byte STATE_START_UP = 0x04;

    private static final byte LOCATION_DATA_VALID = 0X01;
    private static final byte LOCATION_DATA_ERROR = 0x00;

    private static final String LOC_PROVIDER_NAME = "Glonavin_Subway";

    private final short seqNum;
    private final byte siteID;
    private final boolean isValidLoc;
    private final byte accState;
    private final float longitude;
    private final float latitude;
    private final byte battery;

    public SubwayReportData(IMessage msg) {
        byte[] raw = msg.getBody();
        ByteBuffer buffer = ByteBuffer.wrap(raw);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.rewind();

        this.seqNum = buffer.getShort();
        this.siteID = buffer.get();
        this.isValidLoc = buffer.get() == LOCATION_DATA_VALID;
        this.accState = buffer.get();
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

    public boolean isValidLoc() {
        return isValidLoc;
    }

    public byte getAccState() {
        return accState;
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
        return "SubwayReportData{" +
                ", seqNum=" + seqNum +
                ", siteID=" + siteID +
                ", isValidLoc=" + isValidLoc +
                ", accState=" + getAccStateStr() +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", battery=" + battery +
                '}';
    }

    public String getAccStateStr() {
        switch (accState) {
            case STATE_SPEED_STILL:
                return "静止";
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

    public Location getLocation() {
        Location loc = new Location(LOC_PROVIDER_NAME);
        loc.setTime(System.currentTimeMillis());
        loc.setLatitude(latitude);
        loc.setLongitude(longitude);
        return loc;
    }


}
