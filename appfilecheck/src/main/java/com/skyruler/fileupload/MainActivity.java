package com.skyruler.fileupload;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.skyruler.filechecklibrary.command.LoginCommand;
import com.skyruler.filechecklibrary.connection.BaseManager;
import com.skyruler.filechecklibrary.connection.IConnectStateListener;

public class MainActivity extends AppCompatActivity implements IConnectStateListener {
    BaseManager baseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void connectServer(View view) {
        String host = "10.168.1.166";
        int port = 60000;

        baseManager = new BaseManager(this);
        baseManager.addConnectStateListener(this);
        baseManager.connect(host, port);
    }

    @Override
    public void onConnect() {
        showToast("连接成功");
    }

    @Override
    public void onDisconnect() {
        showToast("连接失败");
    }

    private void showToast(final String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, content, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void sendMessage(View view) {
        EditText inputEt = findViewById(R.id.inputEt);
        String content = inputEt.getText().toString();

        LoginCommand command = new LoginCommand.Builder()
                .command("Login")
                .imei("IMEI")
                .pass("Password")
                .sver("Software Version")
                .cver("Configuration Version").build();
        if (baseManager != null) {
            boolean result = baseManager.sendMessage(command);
            showToast("send message ,result=" + result);
        }
    }
}
