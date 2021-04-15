package com.skyruler.middleware.connection;

import android.bluetooth.BluetoothDevice;

/**
 * Created by dell on 2018/5/31.
 */
public class BluetoothAccess {
    private BluetoothDevice bluetoothDevice;
    private int rssi;
    private byte[] scanRecord;
    private String deviceName;
    private int deviceType;
    private String deviceAddress;
    private boolean connected;

    public BluetoothAccess(BluetoothDevice device, int rssi, byte[] scanRecord, boolean isConnected) {
        bluetoothDevice = device;
        this.rssi = rssi;
        this.scanRecord = scanRecord;
        connected = isConnected;
        if (device != null) {
            deviceAddress = device.getAddress();
            deviceName = device.getName();
            deviceType = device.getType();
        } else {
            deviceAddress = null;
            deviceName = null;
            deviceType = 0;
        }
    }
    public BluetoothAccess(BluetoothDevice device, boolean isConnected){
        this(device, 0, null, isConnected);
    }

    public BluetoothAccess(BluetoothDevice device){
        this(device, false);
    }


    public void setBluetoothDevice(BluetoothDevice device) {
        this.bluetoothDevice = device;
        if (device != null) {
            deviceAddress = device.getAddress();
            deviceName = device.getName();
            deviceType = device.getType();
        } else {
            deviceAddress = null;
            deviceName = null;
            deviceType = 0;
        }
    }

    public void update(BluetoothAccess bluetoothAccess){
        setBluetoothDevice(bluetoothAccess.getBluetoothDevice());
        setRssi(bluetoothAccess.getRssi());
        setScanRecord(bluetoothAccess.getScanRecord());
        setConnected(bluetoothAccess.isConnected());
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public byte[] getScanRecord() {
        return scanRecord;
    }

    public void setScanRecord(byte[] scanRecord) {
        this.scanRecord = scanRecord;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    @Override
    public String toString() {
        return "type: " + deviceType +
                " name: " + deviceName +
                " address: " + deviceAddress +
                " rssi: " + rssi;
    }
}
