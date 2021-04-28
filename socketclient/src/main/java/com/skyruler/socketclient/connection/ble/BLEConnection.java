package com.skyruler.socketclient.connection.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import com.skyruler.socketclient.connection.MessageCollector;
import com.skyruler.socketclient.connection.PacketRouter;
import com.skyruler.socketclient.connection.intf.IConnectOption;
import com.skyruler.socketclient.connection.intf.IConnection;
import com.skyruler.socketclient.connection.intf.IStateListener;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageListener;

import java.util.UUID;

public class BLEConnection implements IConnection {
    private static final String TAG = "BLEConnection";

    private final BLEConnectOption bleOption;
    private boolean mConnected = false;
    private String mLastBluetooth;
    private BlePacketReader mReader;
    private BlePacketWriter mWriter;
    private BluetoothGatt mBluetoothGatt;
    private IStateListener stateListener;
    private final PacketRouter packetRouter;

    public BLEConnection(IConnectOption option) {
        this.bleOption = (BLEConnectOption) option;
        this.packetRouter = new PacketRouter();
    }


    @Override
    public void addMsgListener(IMessageListener listener, MessageFilter filter) {
        packetRouter.addRcvListener(filter, listener);
    }

    @Override
    public void removeMsgListener(MessageFilter filter) {
        packetRouter.removeRcvListener(filter);
    }

    @Override
    public void connect(Context context, IStateListener stateListener) {
        this.stateListener = stateListener;

        try {
            packetRouter.setPacketConstructor(bleOption.getPacketConstructor());
            packetRouter.setMessageConstructor(bleOption.getMessageConstructor());

            BluetoothDevice device = bleOption.getDevice();
            String address = device.getAddress();

            if (this.mBluetoothGatt == null) {
                BluetoothGattCallback gattCallback = new BleConnectionCallback(bleOption);
                this.mBluetoothGatt = device.connectGatt(context, false, gattCallback);
            } else if (address.equals(mLastBluetooth)) {
                this.mBluetoothGatt.connect();
            }

            mReader = new BlePacketReader(packetRouter);
            mWriter = new BlePacketWriter(mBluetoothGatt);
            this.mLastBluetooth = address;
            mWriter.startup();
            mReader.startup();
            setConnected(true);
        } catch (Exception e) {
            e.printStackTrace();
            stateListener.onConnectFailed(e.toString());
        }
    }

    @Override
    public void disconnect() {
        if (this.mBluetoothGatt != null) {
            this.mBluetoothGatt.disconnect();
        }
        setConnected(false);
    }

    private void setConnected(boolean isConnect) {
        this.mConnected = isConnect;
    }

    @Override
    public boolean isConnected() {
        return mConnected;
    }

    @Override
    public void onDestroy() {
        if (this.mBluetoothGatt != null) {
            this.mBluetoothGatt.close();
            this.mBluetoothGatt = null;
        }
        if (mWriter != null) {
            mWriter.shutdown();
            mWriter = null;
        }
        if (mReader != null) {
            mReader.shutdown();
            mReader = null;
        }
    }

    @Override
    public void sendMessage(IMessage msgDataBean) {
        if (!mConnected || msgDataBean == null) {
            return;
        }
        mWriter.sendMessage(msgDataBean);
    }

    @Override
    public IMessage sendSyncMessage(IMessage msgDataBean, long timeout) throws IllegalAccessException {
        throw new IllegalAccessException("不支持的操作");
    }

    @Override
    public IMessage sendSyncMessage(IMessage msgDataBean, MessageFilter filter, long timeout) {
        if (!mConnected || msgDataBean == null) {
            return null;
        }
        mWriter.sendMessage(msgDataBean);
        MessageCollector collector = packetRouter.createMessageCollector(filter);
        IMessage retMsg = collector.nextResult(timeout);
        collector.cancel();
        return retMsg;
    }


    class BleConnectionCallback extends BluetoothGattCallback {
        private final BLEConnectOption bleConnectOption;

        BleConnectionCallback(BLEConnectOption bleConnectOption) {
            this.bleConnectOption = bleConnectOption;
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == 2) {
                stateListener.onConnected(gatt);
                try {
                    Thread.sleep(500);
                    mBluetoothGatt.discoverServices();
                    Log.i(TAG, "Attempting to start service discovery");
                } catch (InterruptedException e) {
                    Log.e(TAG, e.toString());
                }
            } else if (newState == 0) {
                stateListener.onDisconnect(gatt);
                setConnected(false);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            // 发现GATT服务
            displayGattServices(gatt, bleConnectOption);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // 收到数据
            mReader.onBleDataReceive(characteristic);
        }

    }


    private void displayGattServices(BluetoothGatt gatt, final BLEConnectOption bleConnectOption) {
        final BluetoothGattService gattService = gatt.getService(bleConnectOption.getUuidService());
        if (gattService != null) {
            // notify
            BluetoothGattCharacteristic notifyCharacter = gattService.getCharacteristic(bleConnectOption.getUuidRead());
            setNotification(notifyCharacter, bleConnectOption.getUuidDescriptor());
            // write
            BluetoothGattCharacteristic writeCharacter = gattService.getCharacteristic(bleConnectOption.getUuidWrite());
            mWriter.setWriteCharacteristic(writeCharacter);
            // read
            /*BluetoothGattCharacteristic readCharacter = gattService.getCharacteristic(bleConnectOption.getUuidWrite());
            setReadCharacteristic(readCharacter);*/
        }
    }

    private void setNotification(BluetoothGattCharacteristic characteristic, UUID clientUUidConfig) {
        if (this.mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        int charaProp = characteristic.getProperties();
        if ((charaProp & 16) > 0) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(clientUUidConfig);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            boolean status = this.mBluetoothGatt.writeDescriptor(descriptor);
            if (status) {
                this.mBluetoothGatt.setCharacteristicNotification(characteristic, true);
            }
            Log.d(TAG, "Characteristic set notification is " + status);
        }
    }

    private void setReadCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (this.mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        int charaProp = characteristic.getProperties();
        if ((charaProp & 2) > 0) {
            this.mBluetoothGatt.readCharacteristic(characteristic);
            this.mBluetoothGatt.setCharacteristicNotification(characteristic, false);
        }
    }

}
