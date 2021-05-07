package com.skyruler.filechecklibrary.command;

import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.MessageSnBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataUploadCommand extends AbsCommand {
    private static final String COMMAND = "JsonData";

    /*文件创建*/
    public static final String TAG_FILE_CREATE = "first";
    /*文件切换*/
    public static final String TAG_FILE_SWITCH = "last";
    /*语音主叫开始*/
    public static final String TAG_MO_START = "moStart";
    /*语音主叫结束*/
    public static final String TAG_MO_END = "moEnd";
    /*语音被叫开始*/
    public static final String TAG_MT_START = "mtStart";
    /*语音被叫结束*/
    public static final String TAG_MT_END = "mtEnd";
    /*FTP下载开始*/
    public static final String TAG_FTPDL_START = "ftpdlStart";
    /*FTP下载结束*/
    public static final String TAG_FTPDL_END = "ftpdlEnd";
    /*FTP上传开始*/
    public static final String TAG_FTPUP_START = "ftpupStart";
    /*FTP上传结束*/
    public static final String TAG_FTPUP_END = "ftpupEnd";
    /*视频业务开始*/
    public static final String TAG_VIDEO_START = "videoStart";
    /*视频业务结束*/
    public static final String TAG_VIDEO_END = "videoEnd";
    /*UDP上传业务开始*/
    public static final String TAG_UD_PUP_START = "UDPupStart";
    /*UDP上传业务结束*/
    public static final String TAG_UP_DUP_END = "UPDupEnd";
    /*百度云盘下载开始*/
    public static final String TAG_BAIDU_PAN_START = "baiduPanStart";
    /* 百度云盘下载结束*/
    public static final String TAG_BAIDU_PAN_END = "baiduPanEnd";
    /*王者荣耀业务开始*/
    public static final String TAG_KINGGAME_START = "kinggameStart";
    /* 王者荣耀业务结束*/
    public static final String TAG_KINGGAME_END = "kinggameEnd";

    protected DataUploadCommand(Builder builder) {
        super(builder.commandStr, builder.dataStr);
    }

    @Override
    public MessageFilter getMessageFilter() {
        return new MessageFilter() {
            @Override
            public boolean accept(IMessage msg) {
                String command = readCommandHeader(msg);
                return COMMAND.equals(command);
            }
        };
    }

    @Override
    public MessageFilter getResultHandler() {
        return new MessageFilter() {
            @Override
            public boolean accept(IMessage msg) {
                // 暂时不需要处理返回结果，一律返回true
                return msg != null;
            }
        };
    }


    public static class Builder {
        private String session;
        long index;
        /**
         * 标记通信数据包类型，对于新生成的数据（包括触发数据切割规则产生的新数据），需要在此处添加“first”标签；
         */
        String tag;
        double time;
        List<String> fileList;
        double latitude;
        double longitude;

        String commandStr;
        String dataStr;

        /*是否最后一个包*/
        boolean isLastReport;
        double maxLat;
        double minLat;
        double minLng;
        double maxLng;
        List<FileInfo> fileInfo = new ArrayList<>();

        public Builder(String tag, String session) {
            this.tag = tag;
            this.session = session;
            this.index = MessageSnBuilder.getInstance(session).getNextSn();
            this.isLastReport = TAG_FILE_SWITCH.equals(tag);
        }

        public Builder time(double time) {
            this.time = time;
            return this;
        }

        public Builder fileList(List<String> fileList) {
            this.fileList = fileList;
            return this;
        }

        public Builder location(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
            return this;
        }

        public Builder location(double minLat, double minLng, double maxLat, double maxLng) {
            this.minLat = minLat;
            this.minLng = minLng;
            this.maxLat = maxLat;
            this.maxLng = maxLng;
            return this;
        }

        public Builder fileInfo(List<FileInfo> fileList) {
            this.fileInfo = fileList;
            return this;
        }

        public DataUploadCommand build() {
            try {
                // files
                JSONArray jsonFiles = new JSONArray();
                for (String file : fileList) {
                    jsonFiles.put(file);
                }

                // location
                JSONObject jsonLocation = new JSONObject();
                if (isLastReport) {
                    // max loc
                    JSONObject maxLoc = new JSONObject();
                    maxLoc.put("lat", maxLat);
                    maxLoc.put("lon", maxLng);
                    jsonLocation.put("max", maxLoc);
                    // min loc
                    JSONObject minLoc = new JSONObject();
                    minLoc.put("lat", minLat);
                    minLoc.put("lon", minLng);
                    jsonLocation.put("min", minLoc);
                } else {
                    jsonLocation.put("lat", latitude);
                    jsonLocation.put("lon", longitude);
                }

                // file info
                JSONArray fileInfoArr = new JSONArray();
                for (FileInfo file : fileInfo) {
                    JSONObject infoObj = new JSONObject();
                    infoObj.put("file_name", file.fileName);
                    infoObj.put("file_size", file.fileSize);
                    infoObj.put("md5", file.md5);
                    fileInfoArr.put(infoObj);
                }

                // jsonData
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("index", index);
                jsonObj.put("tag", tag);
                jsonObj.put("time", time);
                jsonObj.put("file_name", jsonFiles);
                jsonObj.put("location", jsonLocation);
                if (isLastReport) {
                    jsonObj.put("file_info", fileInfoArr);
                }

                // 打包成json Data之后外面还要包一层spevent(文档是这样的)
                JSONObject jsonWrap = new JSONObject();
                jsonWrap.put("spevent", jsonObj);
                String jsonData = jsonWrap.toString();

                //start combine
                commandStr = "Command=" + COMMAND + "\r\n" +
                        "Session=" + session + "\r\n" +
                        "Data=" + jsonData + "\r\n";
                dataStr = "";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return new DataUploadCommand(this);
        }

    }

    public static class FileInfo {
        String fileName;
        long fileSize;
        String md5;

        public FileInfo(String fileName, long fileSize, String md5) {
            this.fileName = fileName;
            this.fileSize = fileSize;
            this.md5 = md5;
        }
    }
}
