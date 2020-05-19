package com.skyruler.socketclient.connection;

import android.bluetooth.BluetoothDevice;

import java.util.UUID;

public class BleConnectOption implements ConnectionOption {
    public static final UUID UUID_SERVICE = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    public  static final UUID UUID_WRITE = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_NOTIFY = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");
    public static final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private final ConnectionType type;
    private BluetoothDevice device;
    private UUID uuidNotify;
    private UUID uuidWrite;
    private UUID uuidService;
    private UUID clientUUidConfig;

    private BleConnectOption(Builder builder) {
        this.type = builder.type;
        this.device = builder.device;
        this.uuidNotify = builder.uuidNotify;
        this.uuidWrite = builder.uuidWrite;
        this.uuidService = builder.uuidService;
        this.clientUUidConfig = builder.clientUUidConfig;
    }

    @Override
    public ConnectionType getType() {
        return type;
    }

    BluetoothDevice getDevice() {
        return device;
    }

    UUID getUuidNotify() {
        return uuidNotify;
    }

    UUID getUuidWrite() {
        return uuidWrite;
    }

    UUID getUuidService() {
        return uuidService;
    }

    UUID getClientUUidConfig() {
        return clientUUidConfig;
    }

    public static class Builder {
        BluetoothDevice device;
        private UUID uuidNotify;
        private UUID uuidWrite;
        private UUID uuidService;
        private UUID clientUUidConfig;
        private ConnectionType type;

        public Builder(ConnectionType type) {
            this.type = type;
        }

        public Builder device(BluetoothDevice device) {
            this.device = device;
            return this;
        }

        public Builder uuidNotify(UUID uuidNotify) {
            this.uuidNotify = uuidNotify;
            return this;
        }

        public Builder uuidWrite(UUID uuidWrite) {
            this.uuidWrite = uuidWrite;
            return this;
        }

        public Builder uuidService(UUID uuidService) {
            this.uuidService = uuidService;
            return this;
        }

        public Builder clientUUidConfig(UUID clientUUidConfig) {
            this.clientUUidConfig = clientUUidConfig;
            return this;
        }

        public BleConnectOption build() {
            return new BleConnectOption(this);
        }
    }
}
