package com.skyruler.gonavin;

import com.skyruler.middleware.xml.model.MetroLine;

public class SubwayDataHolder {
    private static SubwayDataHolder instance = new SubwayDataHolder();
    private MetroLine metroLine;

    private SubwayDataHolder() {
    }

    public static SubwayDataHolder getInstance() {
        return instance;
    }

    public void setMetroLine(MetroLine metroLine) {
        this.metroLine = metroLine;
    }

    public MetroLine getMetroLine() {
        return metroLine;
    }
}
