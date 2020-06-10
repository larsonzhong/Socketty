package com.skyruler.socketclient.connection.ble;

import android.bluetooth.BluetoothDevice;

import com.skyruler.socketclient.connection.intf.IConnectOption;

import java.util.UUID;

public abstract class BLEConnectOption implements IConnectOption {
    private final ConnectionType type;
    private BluetoothDevice device;
    private UUID uuidRead;
    private UUID uuidWrite;
    private UUID uuidService;
    private UUID uuidDescriptor;

    public BLEConnectOption(BluetoothDevice device, UUID uuidRead, UUID uuidWrite, UUID uuidService, UUID uuidDescriptor) {
        this.type = ConnectionType.BLE;
        this.device = device;
        this.uuidRead = uuidRead;
        this.uuidWrite = uuidWrite;
        this.uuidService = uuidService;
        this.uuidDescriptor = uuidDescriptor;
    }

    @Override
    public ConnectionType getType() {
        return type;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public UUID getUuidRead() {
        return uuidRead;
    }

    public UUID getUuidWrite() {
        return uuidWrite;
    }

    public UUID getUuidService() {
        return uuidService;
    }

    public UUID getUuidDescriptor() {
        return uuidDescriptor;
    }


}
