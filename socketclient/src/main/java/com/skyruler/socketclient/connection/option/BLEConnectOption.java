package com.skyruler.socketclient.connection.option;

import android.bluetooth.BluetoothDevice;

import java.util.UUID;

public abstract class BLEConnectOption implements IConnectOption {
    private final ConnectionType type;
    private BluetoothDevice device;
    private UUID uuidNotify;
    private UUID uuidWrite;
    private UUID uuidService;
    private UUID clientUUidConfig;

    public BLEConnectOption(BluetoothDevice device, UUID uuidNotify, UUID uuidWrite, UUID uuidService, UUID clientUUidConfig) {
        this.type = ConnectionType.BLE;
        this.device = device;
        this.uuidNotify = uuidNotify;
        this.uuidWrite = uuidWrite;
        this.uuidService = uuidService;
        this.clientUUidConfig = clientUUidConfig;
    }

    @Override
    public ConnectionType getType() {
        return type;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public UUID getUuidNotify() {
        return uuidNotify;
    }

    public UUID getUuidWrite() {
        return uuidWrite;
    }

    public UUID getUuidService() {
        return uuidService;
    }

    public UUID getClientUUidConfig() {
        return clientUUidConfig;
    }


}
