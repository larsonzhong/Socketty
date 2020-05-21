package com.skyruler.socketclient.connection.intf;

import android.bluetooth.BluetoothGatt;

public interface IBleStateListener {

    void onServiceDiscover(BluetoothGatt gatt);

    void onConnect(BluetoothGatt gatt);

    void onDisconnect(BluetoothGatt gatt);
}
