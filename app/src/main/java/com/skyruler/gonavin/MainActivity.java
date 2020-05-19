package com.skyruler.gonavin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.skyruler.socketclient.SocketClient;
import com.skyruler.socketclient.connection.BleConnectOption;
import com.skyruler.socketclient.connection.ConnectionOption;

public class MainActivity extends AppCompatActivity {
    GlonavinSdk glonavinSdk = new GlonavinSdk();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        glonavinSdk.setup(getApplicationContext());
        glonavinSdk.scanDevice(true);

        ConnectionOption option = new BleConnectOption
                .Builder(ConnectionOption.ConnectionType.BLE)
                .uuidService(BleConnectOption.UUID_SERVICE)
                .uuidWrite(BleConnectOption.UUID_WRITE)
                .uuidNotify(BleConnectOption.UUID_NOTIFY)
                .clientUUidConfig(BleConnectOption.CLIENT_CHARACTERISTIC_CONFIG)
                .build();
        SocketClient client = SocketClient.getInstance();
        client.setup(getApplicationContext());
        client.connect(option);

        glonavinSdk.chooseMode();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        glonavinSdk.scanDevice(false);
        glonavinSdk.onDestroy();
    }
}
