package com.skyruler.glonavin.xml.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Rony
 * @email: luojun@skyruler.cn
 * @date: Created 2020/5/19 14:59
 */
public class MetroLine implements ByteSerializable {
    public static final int BYTES = 5;
    private String mName;
    private byte mLid;
    private short mAvgSpeed;
    private short mMaxSpeed;
    private String mStartTime;
    private String mEndTime;
    private List<Station> mStations;

    public MetroLine() {
        mStations = new ArrayList<>();
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public byte getLid() {
        return mLid;
    }

    public void setLid(byte lid) {
        this.mLid = lid;
    }

    public short getMaxSpeed() {
        return mMaxSpeed;
    }

    public void setMaxSpeed(short maxSpeed) {
        this.mMaxSpeed = maxSpeed;
    }

    public short getAvgSpeed() {
        return mAvgSpeed;
    }

    public void setAvgSpeed(short avgSpeed) {
        this.mAvgSpeed = avgSpeed;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(String startTime) {
        this.mStartTime = startTime;
    }

    public String getEndTime() {
        return mEndTime;
    }

    public void setEndTime(String endTime) {
        this.mEndTime = endTime;
    }

    public List<Station> getStations() {
        return mStations;
    }

    public void setStations(List<Station> stations) {
        this.mStations = stations;
    }

    private int getBytesSize() {
        int stationSize = mStations == null ? 0 : mStations.size();
        int size = 0;
        for (int i = 0; i < stationSize; i++) {
            size += mStations.get(i).getBytesSize();
        }
        return size + BYTES;
    }

    @Override
    public byte[] toBytes() {
        int stationSize = mStations == null ? 0 : mStations.size();
        ByteBuffer buffer = ByteBuffer.allocate(getBytesSize());
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(mAvgSpeed)
                .putShort(mMaxSpeed)
                .put((byte) stationSize);
        for (int i = 0; i < stationSize; i++) {
            buffer.put(mStations.get(i).toBytes());
        }
        return buffer.array();
    }

    @Override
    public String toString() {
        return "MetroLine{" +
                "mName='" + mName + '\'' +
                ", mLid=" + mLid +
                ", mMaxSpeed=" + mMaxSpeed +
                ", mAvgSpeed=" + mAvgSpeed +
                ", mStartTime='" + mStartTime + '\'' +
                ", mEndTime='" + mEndTime + '\'' +
                ", mStations=" + mStations +
                '}';
    }

}
