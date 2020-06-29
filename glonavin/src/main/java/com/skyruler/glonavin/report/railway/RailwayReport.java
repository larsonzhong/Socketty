package com.skyruler.glonavin.report.railway;

import android.location.Location;

import com.skyruler.glonavin.report.BaseReportData;
import com.skyruler.socketclient.message.IMessage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class RailwayReport extends BaseReportData {
    private static final String LOC_PROVIDER_NAME = "Glonavin_Railway";
    private static final byte ID = 0x40;

    private final short seqNum;
    private final byte siteID;
    private final byte saliteliteNum;
    private final float longitude;
    private final float latitude;
    private final byte battery;
    private final State state;

    public RailwayReport(IMessage msg) {
        byte[] raw = msg.getBody();
        ByteBuffer buffer = ByteBuffer.wrap(raw);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.rewind();

        this.seqNum = buffer.getShort();
        this.siteID = buffer.get();
        this.state = parseStateInfo(buffer.get());
        this.saliteliteNum = buffer.get();
        this.longitude = buffer.getFloat();
        this.latitude = buffer.getFloat();
        this.battery = buffer.get();
    }

    /**
     * Bit0： 0 表示未定位， 1 表示定位成功
     * Bit1,Bit2： 固定为 0
     * Bit3： 0 表示推算未达线路最后一个站点， 1 表示推算到线路最后一个站点(之后输出的位置是纯 GNSS 定位结果)
     * Bit4： 0 表示线路位置推算正常， 1 表示线路位置推算异常
     * Bit5： 0 组合导航位置推算正常， 1 组合导航位置推算异常
     * Bit6： 0 线路数据文件加载正常， 1 线路数据文件加载异常
     * Bit7： 0 模块内存正常， 1 模块内存异常
     */
    private State parseStateInfo(byte stateByte) {
        boolean locValid = ((stateByte) & 0x1) == 1;
        boolean reachLastStation = ((stateByte >> 3) & 0x1) == 1;
        boolean lineLocError = ((stateByte >> 4) & 0x1) == 1;
        boolean navLocError = ((stateByte >> 5) & 0x1) == 1;
        boolean lineDataError = ((stateByte >> 6) & 0x1) == 1;
        boolean memoryError = ((stateByte >> 7) & 0x1) == 1;

        State state = new State();
        state.setLocValid(locValid);
        state.setReachLastStation(reachLastStation);
        state.setLineLocError(lineLocError);
        state.setNavLocError(navLocError);
        state.setLineDataError(lineDataError);
        state.setMemoryError(memoryError);
        return state;
    }

    public short getSeqNum() {
        return seqNum;
    }

    public byte getSiteID() {
        return siteID;
    }

    public byte getSaliteliteNum() {
        return saliteliteNum;
    }

    public byte getBattery() {
        return battery;
    }

    public State getState() {
        return state;
    }

    public Location getLocation() {
        Location loc = new Location(LOC_PROVIDER_NAME);
        loc.setTime(System.currentTimeMillis());
        loc.setLatitude(latitude);
        loc.setLongitude(longitude);
        return loc;
    }

    public class State {
        boolean locValid;
        boolean reachLastStation;
        boolean lineLocError;
        boolean navLocError;
        boolean lineDataError;
        boolean memoryError;

        public boolean isLocValid() {
            return locValid;
        }

        public void setLocValid(boolean locValid) {
            this.locValid = locValid;
        }

        public boolean isReachLastStation() {
            return reachLastStation;
        }

        public void setReachLastStation(boolean reachLastStation) {
            this.reachLastStation = reachLastStation;
        }

        public boolean isLineLocError() {
            return lineLocError;
        }

        public void setLineLocError(boolean lineLocError) {
            this.lineLocError = lineLocError;
        }

        public boolean isNavLocError() {
            return navLocError;
        }

        public void setNavLocError(boolean navLocError) {
            this.navLocError = navLocError;
        }

        public boolean isLineDataError() {
            return lineDataError;
        }

        public void setLineDataError(boolean lineDataError) {
            this.lineDataError = lineDataError;
        }

        public boolean isMemoryError() {
            return memoryError;
        }

        public void setMemoryError(boolean memoryError) {
            this.memoryError = memoryError;
        }
    }
}
