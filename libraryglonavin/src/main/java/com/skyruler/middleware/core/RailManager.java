package com.skyruler.middleware.core;

import android.content.Context;
import android.util.Log;

import com.skyruler.middleware.command.railway.ChooseLine;
import com.skyruler.middleware.command.railway.DeviceMode;
import com.skyruler.middleware.command.railway.GetLineName;
import com.skyruler.middleware.command.railway.SkipStation;
import com.skyruler.middleware.command.railway.TempStop;
import com.skyruler.middleware.parser.csv.RailwayParser;
import com.skyruler.middleware.parser.csv.model.RailwayLine;
import com.skyruler.middleware.report.IDataReporter;
import com.skyruler.middleware.report.railway.LineFileErrorReport;
import com.skyruler.middleware.report.railway.RailwayReport;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageListener;

import java.io.IOException;

public class RailManager extends BaseManager {
    private static final String TAG = "RailManager";
    public static final String DEVICE_NAME = "FootSensor";
    private RailwayLine railwayLine;

    RailManager(Context context) {
        super(context);
    }

    @Override
    public int getMode() {
        return GlonavinFactory.BLUETOOTH_TYPE_RAILWAY;
    }

    @Override
    public String getDeviceName() {
        return DEVICE_NAME;
    }

    public boolean chooseLine(String lineName) {
        ChooseLine cmd = new ChooseLine(lineName);
        boolean success = super.sendMessage(cmd);
        if (success) {
            //this.lineName = lineName;
        }
        Log.d(TAG, "send railway line :" + cmd.toString() + "," + success);
        return success;
    }

    public boolean chooseMode(DeviceMode.Mode mode) {
        DeviceMode cmd = new DeviceMode(mode);
        boolean success = super.sendMessage(cmd);
        Log.d(TAG, "choose railway mode :" + cmd.toString() + "," + success);
        return success;
    }

    /**
     * 线路文件里面的站点包含铁路线上所有的站点， 如果列车不在此站点停车需
     * 要下发跳站命令， 跳站命令里面应包含列车沿途所有不停车的站点。
     * 注意： 跳站命令需在列车启动前下发
     *
     * @param siteIds 按小到大顺序添加需跳过的站点 ID
     */
    public boolean skipStation(byte[] siteIds) {
        SkipStation cmd = new SkipStation(siteIds);
        boolean success = super.sendMessage(cmd);
        Log.d(TAG, "skip railway station :" + cmd.toString() + "," + success);
        return success;
    }

    /**
     * 如果列车需要在某站临时停车(该站不是列车计划停车的站点)， 需在列车到
     * 达临时停车站的上一个站之前发送“临时停车” 命令 {@link TempStop}
     *
     * @param siteIds 按小到大顺序添加临时停车的站点 ID
     */
    public boolean tempStopStation(byte[] siteIds) {
        TempStop cmd = new TempStop(siteIds);
        boolean success = super.sendMessage(cmd);
        Log.d(TAG, "temp stop station :" + cmd.toString() + "," + success);
        return success;
    }

    public void listenerForReport(final IDataReporter reporter) {
        super.addMessageListener(new IMessageListener() {
            @Override
            public void processMessage(IMessage msg) {
                RailwayReport subwayReportData = new RailwayReport(msg);
                reporter.report(subwayReportData);
            }
        }, RailwayReport.REPORT_ID);
    }

    public void stopReport() {
        super.removeMsgListener(RailwayReport.REPORT_ID);
    }

    public void listenerForFileError(final IDataReporter reporter) {
        super.addMessageListener(new IMessageListener() {
            @Override
            public void processMessage(IMessage msg) {
                LineFileErrorReport reportData = new LineFileErrorReport(msg);
                reporter.report(reportData);
            }
        }, LineFileErrorReport.REPORT_ID);
    }

    void getLineName(GetLineName.LineNameCallBack callBack) {
        GetLineName cmd = new GetLineName(callBack);
        boolean success = sendMessage(cmd);
        Log.d(TAG, "get line name  :" + cmd.toString() + "," + success);
    }

    public RailwayLine readStationLineFile(String path) throws IOException {
        railwayLine = new RailwayParser().parseLine(path);
        return railwayLine;
    }

    public RailwayLine getRailwayLine() {
        return railwayLine;
    }
}
