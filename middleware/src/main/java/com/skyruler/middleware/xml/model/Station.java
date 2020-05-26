package com.skyruler.middleware.xml.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Rony
 * @email: luojun@skyruler.cn
 * @date: Created 2020/5/19 14:54
 */
public class Station implements ByteSerializable {
    public static final int BYTES = 10;
    private String mName;
    private float mLatitude;
    private float mLongitude;
    private byte mSid;
    private String mStationTime;
    private List<SubItem> mSubItems;

    public Station() {
        mSubItems = new ArrayList<>();
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public float getLatitude() {
        return mLatitude;
    }

    public void setLatitude(float latitude) {
        this.mLatitude = latitude;
    }

    public float getLongitude() {
        return mLongitude;
    }

    public void setLongitude(float longitude) {
        this.mLongitude = longitude;
    }

    public byte getSid() {
        return mSid;
    }

    public void setSid(byte sid) {
        this.mSid = sid;
    }

    public String getStationTime() {
        return mStationTime;
    }

    public void setStationTime(String stationTime) {
        this.mStationTime = stationTime;
    }

    public List<SubItem> getSubItems() {
        return mSubItems;
    }

    public void setSubItems(List<SubItem> subItems) {
        this.mSubItems = subItems;
    }

    public int getBytesSize() {
        int subSize = mSubItems == null ? 0 : mSubItems.size();
        return (SubItem.BYTES + 1) * subSize + BYTES;
    }

    @Override
    public byte[] toBytes() {
        int subSize = mSubItems == null ? 0 : mSubItems.size();
        int size = (SubItem.BYTES + 1) * subSize + BYTES;
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put((byte) (mSid + 1))
                .putFloat(mLatitude)
                .putFloat(mLongitude)
                .put((byte) subSize);
        if (mSubItems != null) {
            for (int i = 0; i < mSubItems.size(); i++) {
                SubItem subItem = mSubItems.get(i);
                buffer.put((byte) (i + 1))
                        .put(subItem.toBytes());
            }
        }
        return buffer.array();
    }

    @Override
    public String toString() {
        return "Station{" +
                "mName='" + mName + '\'' +
                ", mLatitude=" + mLatitude +
                ", mLongitude=" + mLongitude +
                ", mSid=" + mSid +
                ", mStationTime='" + mStationTime + '\'' +
                ", mSubItems=" + mSubItems +
                '}';
    }
}
