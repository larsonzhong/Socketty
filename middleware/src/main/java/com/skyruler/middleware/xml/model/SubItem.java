package com.skyruler.middleware.xml.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author: Rony
 * @email: luojun@skyruler.cn
 * @date: Created 2020/5/19 14:52
 */
public class SubItem implements ByteSerializable {
    public final static int BYTES = 8;
    private float mLatitude;
    private float mLongitude;

    public SubItem() {
        mLatitude = 0;
        mLongitude = 0;
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

    @Override
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putFloat(mLatitude)
                .putFloat(mLongitude);
        return buffer.array();
    }

    @Override
    public String toString() {
        return "SubItem{" +
                "mLatitude=" + mLatitude +
                ", mLongitude=" + mLongitude +
                '}';
    }
}
