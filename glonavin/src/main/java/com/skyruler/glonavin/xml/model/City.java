package com.skyruler.glonavin.xml.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Rony
 * @email: luojun@skyruler.cn
 * @date: Created 2020/5/19 14:50
 */
public class City {
    private String mName;
    private List<MetroLine> mMetroLines;

    public City() {
        mMetroLines = new ArrayList<>();
    }

    public City(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public List<MetroLine> getMetroLines() {
        return mMetroLines;
    }

    public void setMetroLines(List<MetroLine> metroLines) {
        this.mMetroLines = metroLines;
    }

    @Override
    public String toString() {
        return "City{" +
                "mName='" + mName + '\'' +
                ", mMetroLines=" + mMetroLines +
                '}';
    }
}
