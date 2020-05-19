package com.skyruler.xml.model;

import java.util.List;

/**
 * @author: Rony
 * @email: luojun@skyruler.cn
 * @date: Created 2020/5/19 14:54
 */
public class Station {
    private String mName;
    private float mLatitude;
    private float mLongitude;
    private int mSid;
    private String mStationTime;
    private List<SubItem> mSubItems;

    public Station() {
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

    public int getSid() {
        return mSid;
    }

    public void setSid(int sid) {
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
