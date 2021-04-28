package com.skyruler.fileupload;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.skyruler.filechecklibrary.command.DataUploadCommand;
import com.skyruler.filechecklibrary.command.result.Session;
import com.skyruler.filechecklibrary.connection.IConnectStateListener;
import com.skyruler.filechecklibrary.connection.ManagerCore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IConnectStateListener {
    ManagerCore baseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        baseManager = ManagerCore.getInstance();
        baseManager.setup(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        baseManager.onDestroy();
    }

    public void connectServer(View view) {
        String host = "10.168.1.70";
        int port = 60000;
        baseManager.addConnectStateListener(this);
        baseManager.connect(host, port);
    }

    public void disconnectServer(View view) {
        baseManager.disconnect();
        enableViews(false, false, "断开成功");
    }

    @Override
    public void onConnect() {
        enableViews(true, false, "连接成功");
    }

    @Override
    public void onConnectFailed(String reason) {
        enableViews(false, false, reason);
    }

    @Override
    public void onDisconnect() {
        enableViews(false, false, "连接断开");
    }

    @Override
    public void onLogged(Session session) {
        enableViews(true, true, "登陆成功" + session.toString());
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

                DataUploadCommand command = new DataUploadCommand
                        .Builder(DataUploadCommand.TAG_FILE_CREATE)
                        .time(1616132316.5712156)
                        .location(112.9362, 28.2259)
                        .fileList(fileList)
                        .build();
                boolean result = baseManager.sendMessage(command, true);
                showToast("startReportFirst ,result=" + result);
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

                DataUploadCommand command = new DataUploadCommand
                        .Builder(DataUploadCommand.TAG_MO_START)
                        .time(1616132316.5712156)
                        .location(112.9362, 28.2259)
                        .fileList(fileList)
                        .build();
                boolean result = baseManager.sendMessage(command, true);
                showToast("startReportMoStart ,result=" + result);
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

                DataUploadCommand command = new DataUploadCommand
                        .Builder(DataUploadCommand.TAG_MO_END)
                        .time(1616132316.5712156)
                        .location(112.9362, 28.2259)
                        .fileList(fileList)
                        .build();
                boolean result = baseManager.sendMessage(command, true);
                showToast("startReportMoEnd ,result=" + result);
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

                DataUploadCommand command = new DataUploadCommand
                        .Builder(DataUploadCommand.TAG_FILE_SWITCH)
                        .time(1616132316.5712156)
                        .location(112.9362, 28.2259, 112.9362, 28.2259)
                        .fileList(fileList)
                        .fileInfo(infoList)
                        .build();
                boolean result = baseManager.sendMessage(command, true);
                showToast("startReportLast ,result=" + result);
            }
        }).start();
    }

    @Override
    public void onLoginTimeout() {
        showToast("登陆超时，服务器未响应");
    }

    @Override
    public void onLogout() {
        enableViews(true, false, "注销成功");
    }

    private void showToast(final String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, content, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void login(View view) {
        if (baseManager != null) {
            boolean result = baseManager.login(
                    "869107044188467",
                    "Password",
                    "1.0",
                    "2.0");
            showToast("send message ,result=" + result);
        }
    }

    public void logout(View view) {
        if (baseManager != null) {
            boolean result = baseManager.logout();
            showToast("send message ,result=" + result);
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
