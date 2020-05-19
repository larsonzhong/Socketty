package com.skyruler.gonavin;

import android.content.Context;

import com.skyruler.socketclient.SocketClient;
import com.skyruler.socketclient.filter.MessageIdFilter;
import com.skyruler.socketclient.message.WrappedMessage;

class GlonavinSdk {

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
        SocketClient.getInstance().setup(context);
    }

    void onDestroy() {
        SocketClient.getInstance().onDestroy();
    }

    void scanDevice(boolean isScan) {
        SocketClient.getInstance().scanDevice(isScan);
    }

    private void sendMessage(WrappedMessage message) {
        try {
            SocketClient.getInstance().sendMessage(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
