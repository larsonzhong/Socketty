package com.skyruler.middleware;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.skyruler.middleware.connection.GlonavinConnectOption;
import com.skyruler.middleware.connection.IBleScanListener;
import com.skyruler.middleware.message.WrappedMessage;
import com.skyruler.middleware.xml.model.MetroLine;
import com.skyruler.middleware.xml.model.Station;
import com.skyruler.socketclient.SocketClient;
import com.skyruler.socketclient.filter.MessageIdFilter;
import com.skyruler.socketclient.message.IWrappedMessage.AckMode;

import java.util.Arrays;
import java.util.List;

public class GlonavinSdk {
    private static final String TAG = "GlonavinSdk";
    private static final long SCAN_PERIOD = 10000L;
    private boolean mScanning = false;
    private SocketClient socketClient;
    private Context mContext;
    private IBleScanListener bleScanListener;
    private BluetoothAdapter mBluetoothAdapter;

    public void chooseMode() {
        WrappedMessage message = new WrappedMessage
                .Builder((byte) 0x30)
                .body(new byte[]{0x00})
                .ackMode(AckMode.MESSAGE)
                .filter(new MessageIdFilter((byte) 0x31))
                .limitBodyLength(14)
                .timeout(5000)
                .build();
        sendMessage(message);
    }

    void startTest() {
        WrappedMessage message = new WrappedMessage
                .Builder((byte) 0x32)
                .body(new byte[]{0x00})
                .ackMode(AckMode.MESSAGE)
                .filter(new MessageIdFilter((byte) 0x33))
                .limitBodyLength(14)
                .timeout(5000)
                .build();
        sendMessage(message);
    }

    void setTestDirection(byte startIndex, byte endIndex) {
        WrappedMessage message = new WrappedMessage
                .Builder((byte) 0x34)
                .body(new byte[]{startIndex, endIndex})
                .ackMode(AckMode.MESSAGE)
                .filter(new MessageIdFilter((byte) 0x35))
                .limitBodyLength(14)
                .timeout(5000)
                .build();
        sendMessage(message);
    }

    public void setup(Context context) {
        this.mContext = context;
        this.socketClient = new SocketClient();
        this.socketClient.setup(context);
    }

    public void selectDeviceMode(String mDeviceMode) {
        //todo 选择设备模式
    }

    public void setBleScanListener(IBleScanListener bleScanListener) {
        this.bleScanListener = bleScanListener;
    }

    public void connect(GlonavinConnectOption option) {
        socketClient.connect(option);
    }

    public boolean isConnected() {
        return socketClient.isConnected();
    }

    public void disconnect() {
        socketClient.disConnect();
    }

    public void onDestroy() {
        socketClient.onDestroy();
        if (mScanning && mBluetoothAdapter != null) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
    }

    private void sendMessage(WrappedMessage message) {
        try {
            socketClient.sendMessage(message);
        } catch (InterruptedException e) {
            Log.e(TAG, "sendMessage failed :" + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public void scanDevice(boolean enable) {
        final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            return;
        }

        mBluetoothAdapter = bluetoothManager.getAdapter();
        List<BluetoothDevice> connectedDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
        for (BluetoothDevice connectedDevice : connectedDevices) {
            bleScanListener.onScanResult(connectedDevice, true);
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

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            bleScanListener.onScanResult(bluetoothDevice, false);
        }
    };

    public void sendMetroLine(MetroLine metroLine) {
        byte[] bytes = metroLine.toBytes();
        Log.d(TAG, "size: " + bytes.length + ", buf" + Arrays.toString(bytes));
    }

    public void sendStartEndStation(Station mStartStation, Station mEndStation) {

    }
}
