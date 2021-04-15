package com.skyruler.middleware.parser.csv;

import android.text.TextUtils;
import android.util.Log;

import com.skyruler.middleware.parser.csv.model.RailwayLine;
import com.skyruler.middleware.parser.csv.model.RailwayStation;
import com.skyruler.middleware.parser.xml.model.SubItem;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class RailwayParser {
    private static final String TAG = "RailwayParser";

    public RailwayLine parseLine(String path) throws IOException {
        Log.d(TAG, "start parse railway line file:" + path);

        RailwayLine line = new RailwayLine();
        String fileName = getFileName(path);
        line.setName(fileName);

        DataInputStream in = null;
        CSVParser csvReader = null;
        try {
            in = new DataInputStream(new FileInputStream(new File(path)));
            csvReader = new CSVParser(new InputStreamReader(in, "GBK"));

            List<SubItem> locList = new ArrayList<>();
            String[] next;

            while ((next = csvReader.readNext()) != null) {
                if (next.length == 0) {
                    continue;
                }
                String stationName = next[0];

                if (!TextUtils.isEmpty(stationName)) {
                    locList = new ArrayList<>();

                    RailwayStation station = new RailwayStation();
                    station.setName(stationName);
                    station.setSubItems(locList);
                    // Site id就是序号
                    byte siteID = (byte) (line.getStations().size() + 1);
                    station.setSid(siteID);
                    line.addStation(station);
                    continue;
                }

                SubItem subItem = new SubItem();
                subItem.setLongitude(Float.parseFloat(next[1]));
                subItem.setLatitude(Float.parseFloat(next[2]));
                locList.add(subItem);
            }
        } finally {
            try {
                if (csvReader != null) {
                    csvReader.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "parse railway line end.");
        return line;
    }

    private String getFileName(String path) {
        String fileName = path.substring(path.lastIndexOf("/") + 1);
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

}
