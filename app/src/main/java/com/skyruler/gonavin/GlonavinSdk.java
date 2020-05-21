package com.skyruler.gonavin;

import android.content.Context;

import com.skyruler.socketclient.SocketClient;
import com.skyruler.socketclient.connection.ConnectionOption;
import com.skyruler.socketclient.filter.MessageIdFilter;
import com.skyruler.socketclient.message.WrappedMessage;

class GlonavinSdk {
    private SocketClient socketClient;

    void chooseMode() {
        WrappedMessage message = new WrappedMessage
                .Builder((byte) 0x30)
                .body(new byte[]{0x00})
                .ackMode(WrappedMessage.AckMode.MESSAGE)
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
                .ackMode(WrappedMessage.AckMode.MESSAGE)
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
                .ackMode(WrappedMessage.AckMode.MESSAGE)
                .filter(new MessageIdFilter((byte) 0x35))
                .limitBodyLength(14)
                .timeout(5000)
                .build();
        sendMessage(message);
    }

    void setup(Context context) {
        socketClient = new SocketClient();
        socketClient.setup(context);
    }

    void connect(ConnectionOption option) {
        socketClient.connect(option);
    }

    void onDestroy() {
        socketClient.onDestroy();
    }

    void scanDevice(boolean isScan) {
        socketClient.scanDevice(isScan);
    }

    private void sendMessage(WrappedMessage message) {
        try {
            socketClient.sendMessage(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
