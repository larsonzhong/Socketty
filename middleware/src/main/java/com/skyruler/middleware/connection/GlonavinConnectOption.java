package com.skyruler.middleware.connection;

import android.bluetooth.BluetoothDevice;

import com.skyruler.middleware.message.MessageStrategy;
import com.skyruler.middleware.packet.PacketStrategy;
import com.skyruler.socketclient.connection.option.BLEConnectOption;
import com.skyruler.socketclient.message.IMessageStrategy;
import com.skyruler.socketclient.message.IPacketStrategy;

import java.util.UUID;

public class GlonavinConnectOption extends BLEConnectOption {
    //0000fff0-0000-1000-8000-00805f9b34fb  service
    //0000fff1-0000-1000-8000-00805f9b34fb
    //0000fff2-0000-1000-8000-00805f9b34fb
    //0000fff3-0000-1000-8000-00805f9b34fb
    //0000fff4-0000-1000-8000-00805f9b34fb
    //0000fff5-0000-1000-8000-00805f9b34fb
    // 服务标识
    private static final UUID UUID_SERVICE = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    // 特征标识（发送数据）
    private static final UUID UUID_WRITE = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    // 特征标识（读取数据）
    private static final UUID UUID_READ = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");
    // 描述标识
    private static final UUID UUID_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public GlonavinConnectOption(BluetoothDevice device) {
        super(device, UUID_READ, UUID_WRITE, UUID_SERVICE, UUID_DESCRIPTOR);
    }

    @Override
    public IPacketStrategy getPacketConstructor() {
        return new PacketStrategy();
    }

    @Override
    public IMessageStrategy getMessageConstructor() {
        return new MessageStrategy();
    }

}
