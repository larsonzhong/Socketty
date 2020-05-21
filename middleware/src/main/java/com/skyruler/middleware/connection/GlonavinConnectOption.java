package com.skyruler.middleware.connection;

import android.bluetooth.BluetoothDevice;

import com.skyruler.middleware.message.Message;
import com.skyruler.middleware.packet.Packet;
import com.skyruler.socketclient.connection.option.BLEConnectOption;
import com.skyruler.socketclient.message.IMessageConstructor;
import com.skyruler.socketclient.message.IPacketConstructor;

import java.util.UUID;

public class GlonavinConnectOption extends BLEConnectOption {
    private static final UUID UUID_NOTIFY = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_WRITE = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_SERVICE = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public GlonavinConnectOption(BluetoothDevice device) {
        super(device, UUID_NOTIFY, UUID_WRITE, UUID_SERVICE, CLIENT_CHARACTERISTIC_CONFIG);
    }

    @Override
    public IPacketConstructor getPacketConstructor() {
        return new Packet.Constructor();
    }

    @Override
    public IMessageConstructor getMessageConstructor() {
        return new Message.Constructor();
    }

}
