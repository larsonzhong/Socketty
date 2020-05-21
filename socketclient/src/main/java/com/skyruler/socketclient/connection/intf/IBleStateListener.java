package com.skyruler.socketclient.connection.intf;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

public interface IBleStateListener {

    void onServiceDiscover(BluetoothGatt gatt);

    void onScanResult(BluetoothDevice device, boolean isConnected);

    void onConnect(BluetoothGatt gatt);

    void onDisconnect(BluetoothGatt gatt);
}
