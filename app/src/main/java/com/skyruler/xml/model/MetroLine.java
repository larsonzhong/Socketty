package com.skyruler.xml.model;

import java.util.List;

/**
 * @author: Rony
 * @email: luojun@skyruler.cn
 * @date: Created 2020/5/19 14:59
 */
public class MetroLine {
    private String mName;
    private int mLid;
    private int mMaxSpeed;
    private int mAvgSpeed;
    private String mStartTime;
    private String mEndTime;
    private List<Station> mStations;

    public MetroLine() {
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getLid() {
        return mLid;
    }

    public void setLid(int lid) {
        this.mLid = lid;
    }

    public int getMaxSpeed() {
        return mMaxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.mMaxSpeed = maxSpeed;
    }

    public int getAvgSpeed() {
        return mAvgSpeed;
    }

    public void setAvgSpeed(int avgSpeed) {
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
