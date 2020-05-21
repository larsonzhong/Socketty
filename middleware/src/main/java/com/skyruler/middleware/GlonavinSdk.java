package com.skyruler.middleware;

import android.content.Context;
import android.util.Log;

import com.skyruler.middleware.connection.GlonavinConnectOption;
import com.skyruler.middleware.message.WrappedMessage;
import com.skyruler.socketclient.SocketClient;
import com.skyruler.socketclient.filter.MessageIdFilter;
import com.skyruler.socketclient.message.IWrappedMessage.AckMode;

public class GlonavinSdk {
    private static final String TAG = "GlonavinSdk";
    private SocketClient socketClient;

    public void chooseMode() {
        WrappedMessage message = new WrappedMessage
                .Builder((byte) 0x30)
                .body(new byte[]{0x00})
                .ackMode(AckMode.MESSAGE)
                .filter(new MessageIdFilter((byte) 0x31))
                .limitBodyLength(14)
                .timeout(5000)
                .build();
        sendMessage(message);
    }

    void startTest() {
        WrappedMessage message = new WrappedMessage
                .Builder((byte) 0x32)
                .body(new byte[]{0x00})
                .ackMode(AckMode.MESSAGE)
                .filter(new MessageIdFilter((byte) 0x33))
                .limitBodyLength(14)
                .timeout(5000)
                .build();
        sendMessage(message);
    }

    void setTestDirection(byte startIndex, byte endIndex) {
        WrappedMessage message = new WrappedMessage
                .Builder((byte) 0x34)
                .body(new byte[]{startIndex, endIndex})
                .ackMode(AckMode.MESSAGE)
                .filter(new MessageIdFilter((byte) 0x35))
                .limitBodyLength(14)
                .timeout(5000)
                .build();
        sendMessage(message);
    }

    public void setup(Context context) {
        socketClient = new SocketClient();
        socketClient.setup(context);
    }

    public void connect(GlonavinConnectOption option) {
        socketClient.connect(option);
    }

    public void onDestroy() {
        socketClient.onDestroy();
    }

    public void scanDevice(boolean isScan) {
        socketClient.scanDevice(isScan);
    }

    private void sendMessage(WrappedMessage message) {
        try {
            socketClient.sendMessage(message);
        } catch (InterruptedException e) {
            Log.e(TAG, "sendMessage failed :" + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }


}
