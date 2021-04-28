package com.skyruler.middleware.parser.xml.model;

import com.skyruler.middleware.parser.BaseStation;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Rony
 * @email: luojun@skyruler.cn
 * @date: Created 2020/5/19 14:54
 */
public class Station extends BaseStation  {
    private static final int BYTES = 10;
    private float mLatitude;
    private float mLongitude;
    private String mStationTime;

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

    public String getStationTime() {
        return mStationTime;
    }

    public void setStationTime(String stationTime) {
        this.mStationTime = stationTime;
    }

    int getBytesSize() {
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
                .putFloat(mLongitude)
                .putFloat(mLatitude)
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
