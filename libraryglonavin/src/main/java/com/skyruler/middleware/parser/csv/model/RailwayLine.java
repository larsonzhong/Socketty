package com.skyruler.middleware.parser.csv.model;

import java.util.ArrayList;
import java.util.List;

public class RailwayLine {
    private String name;
    private final List<RailwayStation> stations = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RailwayStation> getStations() {
        return stations;
    }

    public void addStation(RailwayStation station) {
        stations.add(station);
    }
}
