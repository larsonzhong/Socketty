package com.skyruler.middleware.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.skyruler.middleware.command.AbsCommand;
import com.skyruler.middleware.command.EditionCommand;
import com.skyruler.middleware.command.TestControlCmd;
import com.skyruler.middleware.command.TestDirectionCmd;
import com.skyruler.middleware.connection.GlonavinConnectOption;
import com.skyruler.middleware.connection.IBleStateListener;
import com.skyruler.middleware.message.WrappedMessage;
import com.skyruler.socketclient.SocketClient;
import com.skyruler.socketclient.connection.intf.IStateListener;
import com.skyruler.socketclient.exception.ConnectionException;
import com.skyruler.socketclient.exception.UnFormatMessageException;
import com.skyruler.socketclient.filter.MessageIdFilter;
import com.skyruler.socketclient.message.IMessageListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class ManagerCore {
    private static final String TAG = "ManagerCore";
    private static final long SCAN_PERIOD = 10000L;
    private BluetoothManager bluetoothManager;
    private SocketClient socketClient;

    private boolean isTestStart;
    private boolean mScanning = false;

    private CopyOnWriteArrayList<IBleStateListener> connListeners;


    void setup(Context context) {
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.socketClient = new SocketClient();
        this.socketClient.setup(context, new IStateListener() {
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
                    } else if (device instanceof Exception) {
                        ((Exception) device).printStackTrace();
                    }
                }
            }
        });
    }


    void addConnectStateListener(IBleStateListener listener) {
        if (connListeners == null) {
            connListeners = new CopyOnWriteArrayList<>();
        }
        if (listener != null) {
            connListeners.add(listener);
        }
    }

    void removeConnectListener(IBleStateListener listener) {
        if (connListeners != null) {
            connListeners.remove(listener);
        }
    }

    void listenerForReport(IMessageListener listener, byte reportID) {
        this.socketClient.addMessageListener(listener, new MessageIdFilter(reportID));
    }

    void removeMsgListener(byte msgID) {
        this.socketClient.removeMessageListener(new MessageIdFilter(msgID));
    }

    boolean setTestDirection(byte startSid, byte endSid) {
        TestDirectionCmd cmd = new TestDirectionCmd(startSid, endSid);
        boolean success = sendMessage(cmd);
        Log.d(TAG, "set test direction :" + cmd.toString() + ", " + success);
        return success;
    }

    boolean startTest(boolean start) {
        TestControlCmd cmd = new TestControlCmd(start);
        boolean success = sendMessage(cmd);
        if (success) {
            this.isTestStart = cmd.isStartTest();
        }
        Log.d(TAG, "send start test :" + cmd.toString() + "," + success);
        return success;
    }

    void getEdition(EditionCommand.EditionCallBack callBack) {
        EditionCommand cmd = new EditionCommand(callBack);
        boolean success = sendMessage(cmd);
        Log.d(TAG, "get edition  :" + cmd.toString() + "," + success);
    }

    void connect(BluetoothDevice bluetoothDevice) {
        GlonavinConnectOption option = new GlonavinConnectOption(bluetoothDevice);
        socketClient.connect(option);
    }

    boolean isConnected() {
        return socketClient.isConnected();
    }

    void disconnect() {
        socketClient.disConnect();
    }

    boolean isTestStart() {
        return isTestStart;
    }

    public void onDestroy() {
        socketClient.onDestroy();
        socketClient = null;
        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
        if (mScanning && bluetoothAdapter != null) {
            bluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
    }

    boolean sendMessage(AbsCommand cmd) {
        try {
            WrappedMessage message = new WrappedMessage
                    .Builder(cmd.getMsgID())
                    .body(cmd.getBody())
                    .ackMode(cmd.getAckMode())
                    .msgFilter(cmd.getResponseFilter())
                    .resultHandler(cmd.getResultHandler())
                    .timeout(cmd.getTimeout())
                    .limitBodyLength(cmd.getLimitBodyLength())
                    .build();
            boolean isSend = socketClient.sendMessage(message);
            Log.d(TAG, "sendMessage state=" + isSend);
            return isSend;
        } catch (ConnectionException e) {
            Log.e(TAG, "sendMessage failed :" + e.getMessage());
            return false;
        } catch (UnFormatMessageException e) {
            Log.e(TAG, "sendMessage failed :" + e.getMessage());
            return false;
        }
    }

    void scanDevice(boolean enable) {
        if (enable) {
            List<BluetoothDevice> connectedDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
            for (BluetoothDevice connectedDevice : connectedDevices) {
                onScanResult(connectedDevice, true);
            }
        }

        final BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
        if (!enable) {
            mScanning = false;
            bluetoothAdapter.stopLeScan(mLeScanCallback);
            return;
        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                mScanning = false;
                bluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }, SCAN_PERIOD);
        mScanning = true;
        bluetoothAdapter.startLeScan(mLeScanCallback);
    }

    private BluetoothAdapter getBluetoothAdapter() {
        return bluetoothManager.getAdapter();
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


    void enableBluetooth(boolean checked) {
        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
        if (bluetoothAdapter == null) {
            return;
        }
        if (checked) {
            bluetoothAdapter.enable();
        } else {
            bluetoothAdapter.disable();
        }
    }

    boolean isBluetoothEnable() {
        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
        return bluetoothAdapter.isEnabled();
    }

}
