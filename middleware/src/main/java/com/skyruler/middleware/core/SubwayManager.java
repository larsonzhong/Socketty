package com.skyruler.middleware.core;

import android.util.Log;

import com.skyruler.middleware.command.subway.DeviceModeCmd;
import com.skyruler.middleware.command.subway.MetroLineCmd;
import com.skyruler.middleware.command.subway.SkipStationCmd;
import com.skyruler.middleware.command.subway.TempStopStationCmd;
import com.skyruler.middleware.report.IDataReporter;
import com.skyruler.middleware.report.subway.SubwayReportData;
import com.skyruler.middleware.xml.model.City;
import com.skyruler.middleware.xml.model.MetroData;
import com.skyruler.middleware.xml.model.MetroLine;
import com.skyruler.middleware.xml.parser.MetroParser;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static com.skyruler.middleware.command.subway.DeviceModeCmd.MODE_SUBWAY;

public class SubwayManager extends AbsManager {
    private static final String TAG = "SubwayManager";
    public static final String DEVICE_NAME = "FootSensor";
    private static final byte REPORT_ID_SUBWAY = 0x40;

    public void listenerForReport(final IDataReporter reporter) {
        super.addMessageListener(new IMessageListener() {
            @Override
            public void processMessage(IMessage msg) {
                SubwayReportData subwayReportData = new SubwayReportData(msg);
                reporter.report(subwayReportData);
            }
        }, REPORT_ID_SUBWAY);
    }

    public void stopSubwayReport() {
        super.removeMsgListener(REPORT_ID_SUBWAY);
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
            throw new IllegalStateException("xml file parse failed,metroData is null");
        }
        // 为了方便只取第一个city
        return metroData.getCities().get(0);
    }


}
