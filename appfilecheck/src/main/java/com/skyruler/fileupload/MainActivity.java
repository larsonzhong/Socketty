package com.skyruler.fileupload;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.skyruler.android.logger.Log;
import com.skyruler.filechecklibrary.command.DataUploadCommand;
import com.skyruler.filechecklibrary.command.result.Session;
import com.skyruler.filechecklibrary.connection.IConnectStateListener;
import com.skyruler.filechecklibrary.connection.MutiSocketor;
import com.skyruler.filechecklibrary.connection.SocketChanel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IConnectStateListener {
    MutiSocketor mutiSocketor;
    private List<String> ipList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ipList.add("10.168.1.166");
        ipList.add("10.168.1.70");
        mutiSocketor = MutiSocketor.getInstance();
        mutiSocketor.setup(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mutiSocketor.onDestroy();
    }

    public void connectServer(View view) {
        int port = 60000;
        for (String host : ipList) {
            SocketChanel socketChanel = mutiSocketor.getSocketChanel(host);
            socketChanel.addConnectStateListener(this);
            socketChanel.connect(host, port);
        }
    }

    public void disconnectServer(View view) {
        mutiSocketor.disconnect();
        enableViews(false, false, "断开成功");
    }

    @Override
    public void onConnect(String host, boolean reconnect) {
        enableViews(true, false, "连接成功:" + host + ",reconnect=" + reconnect);
    }

    @Override
    public void onConnectFailed(String host, String reason) {
        enableViews(false, false, reason + ":" + host);
    }

    @Override
    public void onDisconnect(String host) {
        enableViews(false, false, "连接断开:" + host);
    }

    @Override
    public void onLogged(String host, Session session) {
        enableViews(true, true, host + "登陆成功" + session.toString());
    }

    private void enableViews(final boolean logEnable, final boolean sendEnable, final String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(content);
                findViewById(R.id.disconnectBtn).setEnabled(logEnable);
                findViewById(R.id.btn_login).setEnabled(logEnable);
                findViewById(R.id.btn_logout).setEnabled(sendEnable);
                findViewById(R.id.btn_report_first).setEnabled(sendEnable);
                findViewById(R.id.btn_ReportLast).setEnabled(sendEnable);
                findViewById(R.id.btn_ReportMoStart).setEnabled(sendEnable);
                findViewById(R.id.startReportMoEnd).setEnabled(sendEnable);
                findViewById(R.id.sendBtn).setEnabled(sendEnable);
            }
        });

    }

    public void startReportFirst(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> fileList = new ArrayList<>();
                fileList.add("20210423.skz");
                fileList.add("20210423.ipx");
                fileList.add("20210423.cu");

                for (String ip : ipList) {
                    SocketChanel socketChanel = mutiSocketor.getSocketChanel(ip);
                    String session = socketChanel.getLoginResult().getSession();
                    DataUploadCommand command = new DataUploadCommand
                            .Builder(DataUploadCommand.TAG_FILE_CREATE, session)
                            .time(1616132316.5712156)
                            .location(112.9362, 28.2259)
                            .fileList(fileList)
                            .build();
                    boolean result = socketChanel.sendMessage(command, true);
                    showToast("startReportFirst ,result=" + result + ",ip=" + ip);
                }
            }
        }).start();
    }

    public void startReportMoStart(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> fileList = new ArrayList<>();
                fileList.add("20210423.skz");
                fileList.add("20210423.ipx");
                fileList.add("20210423.cu");

                for (String ip : ipList) {
                    SocketChanel socketChanel = mutiSocketor.getSocketChanel(ip);
                    String session = socketChanel.getLoginResult().getSession();
                    DataUploadCommand command = new DataUploadCommand
                            .Builder(DataUploadCommand.TAG_MO_START, session)
                            .time(1616132316.5712156)
                            .location(112.9362, 28.2259)
                            .fileList(fileList)
                            .build();
                    boolean result = socketChanel.sendMessage(command, true);
                    showToast("startReportMoStart ,result=" + result + ",ip=" + ip);
                }
            }
        }).start();
    }

    public void startReportMoEnd(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> fileList = new ArrayList<>();
                fileList.add("20210423.skz");
                fileList.add("20210423.ipx");
                fileList.add("20210423.cu");

                for (String ip : ipList) {
                    SocketChanel socketChanel = mutiSocketor.getSocketChanel(ip);
                    String session = socketChanel.getLoginResult().getSession();
                    DataUploadCommand command = new DataUploadCommand
                            .Builder(DataUploadCommand.TAG_MO_END, session)
                            .time(1616132316.5712156)
                            .location(112.9362, 28.2259)
                            .fileList(fileList)
                            .build();
                    boolean result = socketChanel.sendMessage(command, true);
                    showToast("startReportMoEnd ,result=" + result + ",ip=" + ip);
                }
            }
        }).start();
    }

    public void startReportLast(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> fileList = new ArrayList<>();
                fileList.add("20210423.skz");
                fileList.add("20210423.ipx");
                fileList.add("20210423.cu");

                List<DataUploadCommand.FileInfo> infoList = new ArrayList<>();
                infoList.add(new DataUploadCommand.FileInfo("20210423.skz",
                        10054, "fsdajkfhjklsdhf"));
                infoList.add(new DataUploadCommand.FileInfo("20210423.ipx",
                        10054, "fsdajkfhjklsdhf"));
                infoList.add(new DataUploadCommand.FileInfo("20210423.cu",
                        10054, "fsdajkfhjklsdhf"));

                for (String ip : ipList) {
                    SocketChanel socketChanel = mutiSocketor.getSocketChanel(ip);
                    String session = socketChanel.getLoginResult().getSession();
                    DataUploadCommand command = new DataUploadCommand
                            .Builder(DataUploadCommand.TAG_FILE_SWITCH, session)
                            .time(1616132316.5712156)
                            .location(112.9362, 28.2259, 112.9362, 28.2259)
                            .fileList(fileList)
                            .fileInfo(infoList)
                            .build();
                    boolean result = socketChanel.sendMessage(command, true);
                    showToast("startReportLast ,result=" + result + ",ip=" + ip);
                }
            }
        }).start();
    }

    @Override
    public void onLoginTimeout(String host) {
        showToast(host + "登陆超时，服务器未响应");
    }

    @Override
    public void onLogout(String host) {
        enableViews(true, false, host + "注销成功");
    }

    private void showToast(final String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("larson1", content);
                Toast.makeText(MainActivity.this, content, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void login(View view) {
        if (mutiSocketor != null) {
            for (String ip : ipList) {
                boolean result = mutiSocketor.getSocketChanel(ip).login(
                        "869107044188467",
                        "Password",
                        "1.0",
                        "2.0");
                showToast("send message ,result=" + result + ",ip=" + ip);
            }
        }
    }

    public void logout(View view) {
        if (mutiSocketor != null) {
            for (String ip : ipList) {
                boolean result = mutiSocketor.getSocketChanel(ip).logout();
                showToast("send message ,result=" + result + ",ip=" + ip);
            }
        }
    }

    public void sendMsg(View view) {
        EditText inputEt = findViewById(R.id.inputEt);
        String content = inputEt.getText().toString();
        /*boolean result = baseManager.sendMessage("IMEI", "Password",
                "softVersion", "configVersion");
        showToast("send message ,result=" + result);*/
    }
}
