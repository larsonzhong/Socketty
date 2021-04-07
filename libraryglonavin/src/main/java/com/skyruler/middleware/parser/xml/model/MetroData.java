package com.skyruler.middleware.parser.xml.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Rony
 * @email: luojun@skyruler.cn
 * @date: Created 2020/5/19 16:05
 */
public class MetroData {
    private List<City> mCities;

    public MetroData() {
        mCities = new ArrayList<>();
    }

    public MetroData(List<City> cities) {
        mCities = cities;
    }

    public List<City> getCities() {
        return mCities;
    }

    public void setCities(List<City> cities) {
        mCities = cities;
    }

    @Override
    public String toString() {
        return "MetroData{" +
                "mCities=" + mCities +
                '}';
    }
}
