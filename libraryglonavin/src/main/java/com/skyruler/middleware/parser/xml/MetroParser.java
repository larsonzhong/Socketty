package com.skyruler.middleware.parser.xml;

import android.util.Xml;

import com.skyruler.middleware.parser.xml.model.City;
import com.skyruler.middleware.parser.xml.model.MetroData;
import com.skyruler.middleware.parser.xml.model.MetroLine;
import com.skyruler.middleware.parser.xml.model.Station;
import com.skyruler.middleware.parser.xml.model.SubItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Rony
 * @email: luojun@skyruler.cn
 * @date: Created 2020/5/19 15:10
 */
public class MetroParser implements XmlParser<MetroData> {

    private void parseMetroData(XmlPullParser parser, MetroData metroData) {
        switch (parser.getName()) {
            case "City":
                parseCity(parser, metroData);
                break;
            case "MetroLine":
                parseMetroLine(parser, metroData);
                break;
            case "Station":
                parseStation(parser, metroData);
                break;
            case "SubItems":
                parseSubItems(parser, metroData);
                break;
            case "SubItem":
                parseSubItem(parser, metroData);
                break;
            default:
                break;
        }
    }

    private void parseCity(XmlPullParser parser, MetroData metroData) {
        int count = parser.getAttributeCount();
        List<City> cities = metroData.getCities();
        if (cities == null) {
            cities = new ArrayList<>();
            metroData.setCities(cities);
        }
        City city = new City();
        for (int i = 0; i < count; i++) {
            if ("Name".equals(parser.getAttributeName(i))) {
                city.setName(parser.getAttributeValue(i));
                cities.add(city);
            }
        }
    }

    private void parseMetroLine(XmlPullParser parser, MetroData metroData) {
        List<City> cities = metroData.getCities();
        if (cities == null || cities.isEmpty()) {
            return;
        }
        City city = cities.get(cities.size() - 1);
        List<MetroLine> metroLines = city.getMetroLines();
        if (metroLines == null) {
            metroLines = new ArrayList<>();
            city.setMetroLines(metroLines);
        }
        int count = parser.getAttributeCount();
        MetroLine metroLine = new MetroLine();
        for (int i = 0; i < count; i++) {
            switch (parser.getAttributeName(i)) {
                case "Name":
                    metroLine.setName(parser.getAttributeValue(i));
                    break;
                case "LID":
                    metroLine.setLid(Byte.parseByte(parser.getAttributeValue(i)));
                    break;
                case "StartTime":
                    metroLine.setStartTime(parser.getAttributeValue(i));
                    break;
                case "MaxSpeed_KMperH":
                    metroLine.setMaxSpeed(Short.parseShort(parser.getAttributeValue(i)));
                    break;
                case "Speed_KMperH":
                    metroLine.setAvgSpeed(Short.parseShort(parser.getAttributeValue(i)));
                    break;
                case "EndTime":
                    metroLine.setEndTime(parser.getAttributeValue(i));
                    break;
                default:
                    break;
            }
        }
        metroLines.add(metroLine);
    }

    private void parseStation(XmlPullParser parser, MetroData metroData) {
        List<City> cities = metroData.getCities();
        if (cities == null || cities.isEmpty()) {
            return;
        }
        City city = cities.get(cities.size() - 1);
        List<MetroLine> metroLines = city.getMetroLines();
        if (metroLines == null || metroLines.isEmpty()) {
            return;
        }
        MetroLine metroLine = metroLines.get(metroLines.size() - 1);
        List<Station> stations = metroLine.getStations();
        if (stations == null) {
            stations = new ArrayList<>();
            metroLine.setStations(stations);
        }
        int count = parser.getAttributeCount();
        Station station = new Station();
        for (int i = 0; i < count; i++) {
            switch (parser.getAttributeName(i)) {
                case "Name":
                    station.setName(parser.getAttributeValue(i));
                    break;
                case "SID":
                    station.setSid(Byte.parseByte(parser.getAttributeValue(i)));
                    break;
                case "Latitude":
                    station.setLatitude(Float.parseFloat(parser.getAttributeValue(i)));
                    break;
                case "Longitude":
                    station.setLongitude(Float.parseFloat(parser.getAttributeValue(i)));
                    break;
                case "StationTime":
                    station.setStationTime(parser.getAttributeValue(i));
                    break;
                default:
                    break;
            }
        }
        stations.add(station);
    }

    private void parseSubItems(XmlPullParser parser, MetroData metroData) {
        List<City> cities = metroData.getCities();
        if (cities == null || cities.isEmpty()) {
            return;
        }
        City city = cities.get(cities.size() - 1);
        List<MetroLine> metroLines = city.getMetroLines();
        if (metroLines == null || metroLines.isEmpty()) {
            return;
        }
        MetroLine metroLine = metroLines.get(metroLines.size() - 1);
        List<Station> stations = metroLine.getStations();
        if (stations == null || stations.isEmpty()) {
            return;
        }
        Station station = stations.get(stations.size() - 1);
        List<SubItem> subItems = station.getSubItems();
        if (subItems == null) {
            subItems = new ArrayList<>();
            station.setSubItems(subItems);
        }
    }

    private void parseSubItem(XmlPullParser parser, MetroData metroData) {
        List<City> cities = metroData.getCities();
        if (cities == null || cities.isEmpty()) {
            return;
        }
        City city = cities.get(cities.size() - 1);
        List<MetroLine> metroLines = city.getMetroLines();
        if (metroLines == null || metroLines.isEmpty()) {
            return;
        }
        MetroLine metroLine = metroLines.get(metroLines.size() - 1);
        List<Station> stations = metroLine.getStations();
        if (stations == null || stations.isEmpty()) {
            return;
        }
        Station station = stations.get(stations.size() - 1);
        List<SubItem> subItems = station.getSubItems();
        if (subItems == null) {
            return;
        }
        SubItem subItem = new SubItem();
        int count = parser.getAttributeCount();
        for (int i = 0; i < count; i++) {
            switch (parser.getAttributeName(i)) {
                case "Latitude":
                    subItem.setLatitude(Float.parseFloat(parser.getAttributeValue(i)));
                    break;
                case "Longitude":
                    subItem.setLongitude(Float.parseFloat(parser.getAttributeValue(i)));
                    break;
                default:
                    break;
            }
        }
        subItems.add(subItem);
    }

    @Override
    public MetroData parse(InputStream is) throws Exception {
        MetroData metroData = null;

        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is, "UTF-8");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if ("MetroDataManager".equals(parser.getName())) {
                    metroData = new MetroData();
                } else if (metroData != null) {
                    parseMetroData(parser, metroData);
                }
            }
            eventType = parser.next();
        }
        return metroData;
    }

    @Override
    public String serialize(MetroData metroData) throws Exception {
        StringWriter stringWriter = new StringWriter();

        // 1.初始化并生成头部节点数据
        XmlSerializer xs = XmlPullParserFactory.newInstance().newSerializer();
        xs.setOutput(stringWriter);
        xs.startDocument("UTF-8", true);
        xs.startTag(null, "MetroDataManager");

        // 2. 初始化对象数据及子属性
        List<City> cities = metroData.getCities();
        serializeCities(cities, xs);

        // 3. 结束节点
        xs.endTag(null, "MetroDataManager");
        xs.endDocument();
        return xs.toString();
    }

    private void serializeCities(List<City> cities, XmlSerializer xs) throws IOException {
        for (City city : cities) {
            xs.startTag(null, "City")
                    .attribute(null, "Name", city.getName());
            List<MetroLine> metroLines = city.getMetroLines();
            serializeMetroLines(metroLines, xs);
            xs.endTag(null, "City");
        }
    }

    private void serializeMetroLines(List<MetroLine> metroLines, XmlSerializer xs) throws IOException {
        for (MetroLine line : metroLines) {
            xs.startTag(null, "MetroLine")
                    .attribute(null, "EndTime", line.getEndTime())
                    .attribute(null, "LID", "" + line.getLid())
                    .attribute(null, "MaxSpeed_KMperH", "" + line.getMaxSpeed())
                    .attribute(null, "Name", line.getName())
                    .attribute(null, "Speed_KMperH", "" + line.getAvgSpeed())
                    .attribute(null, "StartTime", line.getStartTime());

            List<Station> stations = line.getStations();
            for (Station station : stations) {
                xs.startTag(null, "Station")
                        .attribute(null, "Latitude", "" + station.getLatitude())
                        .attribute(null, "Longitude", "" + station.getLongitude())
                        .attribute(null, "Name", station.getName())
                        .attribute(null, "SID", "" + station.getSid())
                        .attribute(null, "StationTime", "" + station.getStationTime())
                        .endTag(null, "Station");

                List<SubItem> subItems = station.getSubItems();
                xs.startTag(null, "SubItems");
                for (SubItem subItem : subItems) {
                    xs.startTag(null, "SubItem")
                            .attribute(null, "Latitude", "" + subItem.getLatitude())
                            .attribute(null, "Longitude", "" + subItem.getLongitude())
                            .endTag(null, "SubItem");
                }
                xs.endTag(null, "SubItems");
            }

            xs.endTag(null, "MetroLine");
        }

    }
}
