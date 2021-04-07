package com.skyruler.middleware.core;

import android.content.Context;
import android.util.Log;

import com.skyruler.middleware.command.subway.DeviceModeCmd;
import com.skyruler.middleware.command.subway.MetroLineCmd;
import com.skyruler.middleware.command.subway.SkipStationCmd;
import com.skyruler.middleware.command.subway.TempStopStationCmd;
import com.skyruler.middleware.parser.xml.MetroParser;
import com.skyruler.middleware.parser.xml.model.City;
import com.skyruler.middleware.parser.xml.model.MetroData;
import com.skyruler.middleware.parser.xml.model.MetroLine;
import com.skyruler.middleware.parser.xml.model.Station;
import com.skyruler.middleware.report.IDataReporter;
import com.skyruler.middleware.report.subway.SubwayReportData;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import static com.skyruler.middleware.command.subway.DeviceModeCmd.MODE_SUBWAY;

public class SubwayManager extends BaseManager {
    private static final String TAG = "SubwayManager";
    public static final String DEVICE_NAME = "FootSensor";
    private MetroLine currentMetroLine;

    SubwayManager(Context context) {
        super(context);
    }

    @Override
    public int getMode() {
        return GlonavinFactory.BLUETOOTH_TYPE_SUBWAY;
    }

    @Override
    public String getDeviceName() {
        return DEVICE_NAME;
    }

    public void listenerForReport(final IDataReporter reporter) {
        super.addMessageListener(new IMessageListener() {
            @Override
            public void processMessage(IMessage msg) {
                SubwayReportData subwayReportData = new SubwayReportData(msg);
                reporter.report(subwayReportData);
            }
        }, SubwayReportData.REPORT_ID);
    }

    public void stopReport() {
        super.removeMsgListener(SubwayReportData.REPORT_ID);
    }

    public boolean chooseMode() {
        DeviceModeCmd cmd = new DeviceModeCmd(MODE_SUBWAY);
        boolean success = super.sendMessage(cmd);
        Log.d(TAG, "choose mode :" + cmd.toString() + "," + success);
        return success;
    }

    public boolean sendMetroLine(MetroLine mMetroLine) {
        MetroLineCmd cmd = new MetroLineCmd(mMetroLine);
        boolean success = super.sendMessage(cmd);
        if (success) {
            currentMetroLine = mMetroLine;
        }
        Log.d(TAG, "send subway line :" + cmd.toString() + "," + success);
        return success;
    }

    public boolean skipStation() {
        if (!isTestStart()) {
            Log.d(TAG, "test did not start yet!!");
            return false;
        }
        SkipStationCmd cmd = new SkipStationCmd();
        boolean success = super.sendMessage(cmd);
        Log.d(TAG, "skip subway station :" + cmd.toString() + "," + success);
        return success;
    }

    public boolean tempStopStation() {
        if (!isTestStart()) {
            Log.d(TAG, "test did not start yet!!");
            return false;
        }
        TempStopStationCmd cmd = new TempStopStationCmd();
        boolean success = super.sendMessage(cmd);
        Log.d(TAG, "temp stop station :" + cmd.toString() + "," + success);
        return success;
    }

    public City readSubwayLineFromXmlFile(String path) throws Exception {
        InputStream is = new FileInputStream(new File(path));
        MetroParser parser = new MetroParser();
        MetroData metroData = parser.parse(is);
        is.close();
        if (metroData != null) {
            Log.d(TAG, metroData.toString());
        }
        if (metroData == null || metroData.getCities() == null) {
            throw new IllegalStateException("xml file parseLine failed,metroData is null");
        }
        // 为了方便只取第一个city
        return metroData.getCities().get(0);
    }

    public Station getStation(int siteID) {
        if (this.currentMetroLine == null) {
            return null;
        }
        // 文档中的神逻辑，xml文件中的sid节点：sid+1 = siteID(上报);
        int sid = siteID - 1;
        List<Station> stations = this.currentMetroLine.getStations();
        for (int i = 0; i < stations.size(); i++) {
            if (stations.get(i).getSid() == sid) {
                return stations.get(i);
            }
        }
        return null;
    }
}
