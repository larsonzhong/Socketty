package com.skyruler.xml.model;

/**
 * @author: Rony
 * @email: luojun@skyruler.cn
 * @date: Created 2020/5/19 14:52
 */
public class SubItem {
    private float mLatitude;
    private float mLongitude;

    public SubItem() {
        mLatitude = 0;
        mLongitude = 0;
    }

    public SubItem(float latitude, float longitude) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
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
    public String toString() {
        return "SubItem{" +
                "mLatitude=" + mLatitude +
                ", mLongitude=" + mLongitude +
                '}';
    }
}
