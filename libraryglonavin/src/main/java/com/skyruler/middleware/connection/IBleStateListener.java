package com.skyruler.middleware.connection;

import android.bluetooth.BluetoothDevice;

public interface IBleStateListener {
    void onScanResult(BluetoothDevice bluetoothDevice, boolean isConnected);

    void onConnected(BluetoothDevice bluetoothDevice);

    void onDisconnect(BluetoothDevice bluetoothDevice);
}