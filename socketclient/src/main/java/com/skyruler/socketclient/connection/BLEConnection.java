package com.skyruler.socketclient.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.intf.IBleStateListener;
import com.skyruler.socketclient.intf.IConnection;
import com.skyruler.socketclient.intf.IMessageListener;
import com.skyruler.socketclient.message.Message;

import java.util.List;
import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
class BLEConnection implements IConnection {
    private static final String TAG = "BLEConnection";
    private static final long SCAN_PERIOD = 10000L;
    private boolean mScanning = false;
    private boolean mConnected = false;
    private String mLastBluetooth;
    private PacketReader mReader;
    private PacketWriter mWriter;
    private BluetoothGatt mBluetoothGatt;
    private IBleStateListener stateListener;
    private BluetoothAdapter mBluetoothAdapter;
    private final PacketRouter packetRouter;

    BLEConnection(IBleStateListener stateListener) {
        this.stateListener = stateListener;
        this.packetRouter = new PacketRouter();
    }

    private void setConnected(boolean isConnect) {
        this.mConnected = isConnect;
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
    public void connect(Context context, ConnectionOption option) {
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }

        String address = null;
        BluetoothDevice device = null;
        BleConnectOption bleConnectOption = (BleConnectOption) option;
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
            if (mBluetoothAdapter != null && bleConnectOption != null) {
                address = bleConnectOption.getDevice().getAddress();
                device = mBluetoothAdapter.getRemoteDevice(address);
            }
        }
        if (mBluetoothAdapter == null || device == null || address == null) {
            setConnected(false);
            throw new IllegalArgumentException("connect failed !!argument=null");
        }

        if (this.mBluetoothGatt == null) {
            BluetoothGattCallback gattCallback = new BleConnectionCallback(bleConnectOption);
            this.mBluetoothGatt = device.connectGatt(context, false, gattCallback);
        } else if (address.equals(mLastBluetooth)) {
            this.mBluetoothGatt.connect();
        }

        mReader = new PacketReader(packetRouter);
        mWriter = new PacketWriter(mBluetoothGatt);
        this.mLastBluetooth = address;
        mWriter.startup();
        mReader.startup();
        setConnected(true);
    }

    @Override
    public void disconnect() {
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
        if (this.mBluetoothGatt != null) {
            this.mBluetoothGatt.disconnect();
        }
    }

    @Override
    public void scanLeDevice(Context mContext, boolean enable) {
        BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            List<BluetoothDevice> connectedDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
            for (BluetoothDevice connectedDevice : connectedDevices) {
                stateListener.onScanResult(connectedDevice, true);
            }
        }

        if (!enable) {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            return;
        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }, SCAN_PERIOD);
        mScanning = true;
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    @Override
    public void stopDevice() {
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
    public void sendMessage(Message msgDataBean) {
        if (!mConnected || msgDataBean == null) {
            return;
        }
        mWriter.sendMessage(msgDataBean);
    }

    @Override
    public Message sendSyncMessage(Message msgDataBean, MessageFilter filter, long timeout) {
        if (!mConnected || msgDataBean == null) {
            return null;
        }
        mWriter.sendMessage(msgDataBean);
        MessageCollector collector = packetRouter.createMessageCollector(filter);
        Message retMsg = collector.nextResult(timeout);
        collector.cancel();
        return retMsg;
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            stateListener.onScanResult(bluetoothDevice, false);
        }
    };

    class BleConnectionCallback extends BluetoothGattCallback {
        private BleConnectOption bleConnectOption;

        BleConnectionCallback(BleConnectOption bleConnectOption) {
            this.bleConnectOption = bleConnectOption;
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == 2) {
                stateListener.onConnect(gatt);
            } else if (newState == 0) {
                stateListener.onDisconnect(gatt);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            displayGattServices(gatt, bleConnectOption);
            if (status == 0) {
                stateListener.onServiceDiscover(gatt);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            mReader.onDataReceive(characteristic);
        }
    }


    private void displayGattServices(BluetoothGatt gatt, final BleConnectOption bleConnectOption) {
        final BluetoothGattService gattService = gatt.getService(bleConnectOption.getUuidService());
        if (gattService != null) {
            BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(bleConnectOption.getUuidNotify());
            setCharacteristicNotification(characteristic, bleConnectOption.getClientUUidConfig());
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(bleConnectOption.getUuidWrite());
                    if (characteristic != null) {
                        readCharacteristic(characteristic);
                    }
                }
            }, 500L);
            BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(bleConnectOption.getUuidWrite());
            mWriter.setWriteCharacteristic(gattCharacteristic);
        }
    }

    private void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, UUID clientUUidConfig) {
        if (this.mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        int charaProp = characteristic.getProperties();
        if ((charaProp & 16) > 0) {
            this.mBluetoothGatt.setCharacteristicNotification(characteristic, true);
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(clientUUidConfig);
            if (descriptor != null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            }
            boolean status = this.mBluetoothGatt.writeDescriptor(descriptor);
            Log.d(TAG, "Characteristic set notification is " + status);
        }
    }

    private void readCharacteristic(BluetoothGattCharacteristic characteristic) {
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
