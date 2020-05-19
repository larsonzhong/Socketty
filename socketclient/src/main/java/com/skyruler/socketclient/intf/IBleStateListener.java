package com.skyruler.socketclient.intf;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

public interface IBleStateListener {
    int STATE_CONNECT_SUCCESSFUL = 1001;
    int STATE_CONNECT_FAILED = 1002;

    void onServiceDiscover(BluetoothGatt gatt);

    void onScanResult(BluetoothDevice device, boolean isConnected);

    void onConnect(BluetoothGatt gatt);

    void onDisconnect(BluetoothGatt gatt);
}
