package com.skyruler.middleware.connection;

import android.bluetooth.BluetoothDevice;

public interface IBleScanListener {
    void onScanResult(BluetoothDevice bluetoothDevice, boolean isConnected);
}
