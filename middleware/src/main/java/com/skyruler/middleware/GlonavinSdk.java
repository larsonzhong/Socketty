package com.skyruler.middleware;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.skyruler.middleware.command.AbsCommand;
import com.skyruler.middleware.command.DeviceModeCmd;
import com.skyruler.middleware.command.EditionCommand;
import com.skyruler.middleware.command.MetroLineCmd;
import com.skyruler.middleware.command.SkipStationCmd;
import com.skyruler.middleware.command.TempStopStationCmd;
import com.skyruler.middleware.command.TestControlCmd;
import com.skyruler.middleware.command.TestDirectionCmd;
import com.skyruler.middleware.connection.GlonavinConnectOption;
import com.skyruler.middleware.connection.IBleStateListener;
import com.skyruler.middleware.message.WrappedMessage;
import com.skyruler.middleware.report.IDataReporter;
import com.skyruler.middleware.report.ReportData;
import com.skyruler.middleware.xml.model.MetroLine;
import com.skyruler.socketclient.SocketClient;
import com.skyruler.socketclient.connection.intf.IStateListener;
import com.skyruler.socketclient.filter.MessageIdFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GlonavinSdk {
    private static final String TAG = "GlonavinSdk";
    private static final long SCAN_PERIOD = 10000L;

    private MetroLine currentMetroLine;
    private boolean isTestStart;
    private boolean mScanning = false;
    private SocketClient socketClient;
    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private CopyOnWriteArrayList<IBleStateListener> connListeners;
    private IStateListener connectListener = new IStateListener() {
        @Override
        public void onConnect(Object device) {
            for (IBleStateListener listener : connListeners) {
                if (device instanceof BluetoothGatt) {
                    listener.onConnected(((BluetoothGatt) device).getDevice());
                }
            }
        }

        @Override
        public void onDisconnect(Object device) {
            for (IBleStateListener listener : connListeners) {
                if (device instanceof BluetoothGatt) {
                    listener.onDisconnect(((BluetoothGatt) device).getDevice());
                }
            }
        }
    };

    public void addBleStateListener(IBleStateListener listener) {
        if (connListeners == null) {
            connListeners = new CopyOnWriteArrayList<>();
        }
        if (listener != null) {
            connListeners.add(listener);
        }
    }

    public void removeConnectListener(IBleStateListener listener) {
        if (connListeners != null) {
            connListeners.remove(listener);
        }
    }

    public void listenerForReport(final IDataReporter reporter, byte reportID) {
        this.socketClient.addMessageListener(new IMessageListener() {
            @Override
            public void processMessage(IMessage msg) {
                ReportData reportData = new ReportData(msg);
                reporter.report(reportData);
            }
        }, new MessageIdFilter(reportID));
    }

    public boolean chooseMode(DeviceModeCmd cmd) {
        boolean success = sendMessage(cmd);
        Log.d(TAG, "choose mode :" + cmd.toString() + "," + success);
        return success;
    }

    public boolean sendMetroLine(MetroLineCmd cmd) {
        boolean success = sendMessage(cmd);
        Log.d(TAG, "send subway line :" + cmd.toString() + "," + success);
        if (success) {
            this.currentMetroLine = cmd.getMetroLine();
        }
        return success;
    }

    public boolean setTestDirection(TestDirectionCmd cmd) {
        boolean success = sendMessage(cmd);
        Log.d(TAG, "set test direction :" + cmd.toString() + ", " + success);
        return success;
    }

    public boolean startTest(TestControlCmd cmd) {
        boolean success = sendMessage(cmd);
        if (success) {
            this.isTestStart = cmd.isStartTest();
        }
        Log.d(TAG, "start subway test :" + cmd.toString() + "," + success);
        return success;
    }

    public boolean skipStation() {
        SkipStationCmd cmd = new SkipStationCmd();
        boolean success = sendMessage(cmd);
        Log.d(TAG, "skip subway station :" + cmd.toString() + "," + success);
        return success;
    }

    public boolean tempStopStation() {
        TempStopStationCmd cmd = new TempStopStationCmd();
        boolean success = sendMessage(cmd);
        Log.d(TAG, "temp stop station :" + cmd.toString() + "," + success);
        return success;
    }

    public void getEdition(EditionCommand.EditionCallBack callBack) {
        EditionCommand cmd = new EditionCommand(callBack);
        boolean success = sendMessage(cmd);
        Log.d(TAG, "temp stop station :" + cmd.toString() + "," + success);
    }

    public void setup(Context context) {
        this.mContext = context;
        this.socketClient = new SocketClient();
        this.socketClient.setup(context, connectListener);
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

    public boolean isTestStart() {
        return isTestStart;
    }

    public MetroLine getCurrentMetroLine() {
        return currentMetroLine;
    }

    public void onDestroy() {
        socketClient.onDestroy();
        if (mScanning && mBluetoothAdapter != null) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
    }

    private boolean sendMessage(AbsCommand cmd) {
        try {
            WrappedMessage message = new WrappedMessage
                    .Builder(cmd.getMsgID())
                    .body(cmd.getBody())
                    .ackMode(cmd.getAckMode())
                    .msgFilter(cmd.getMsgFilter())
                    .resultHandler(cmd.getResultHandler())
                    .timeout(cmd.getTimeout())
                    .limitBodyLength(cmd.getLimitBodyLength())
                    .build();
            boolean isSend = socketClient.sendMessage(message);
            Log.d(TAG, "sendMessage state=" + isSend);
            return isSend;
        } catch (InterruptedException e) {
            Log.e(TAG, "sendMessage failed :" + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
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
            onScanResult(connectedDevice, true);
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

    private void onScanResult(BluetoothDevice connectedDevice, boolean isConnected) {
        for (IBleStateListener listener : connListeners) {
            listener.onScanResult(connectedDevice, isConnected);
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            onScanResult(bluetoothDevice, false);
        }
    };
}
