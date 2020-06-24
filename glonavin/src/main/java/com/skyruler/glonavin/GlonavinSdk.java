package com.skyruler.glonavin;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.skyruler.glonavin.command.AbsCommand;
import com.skyruler.glonavin.command.DeviceModeCmd;
import com.skyruler.glonavin.command.EditionCommand;
import com.skyruler.glonavin.command.MetroLineCmd;
import com.skyruler.glonavin.command.SkipStationCmd;
import com.skyruler.glonavin.command.TempStopStationCmd;
import com.skyruler.glonavin.command.TestControlCmd;
import com.skyruler.glonavin.command.TestDirectionCmd;
import com.skyruler.glonavin.connection.GlonavinConnectOption;
import com.skyruler.glonavin.connection.IBleStateListener;
import com.skyruler.glonavin.message.WrappedMessage;
import com.skyruler.glonavin.report.IDataReporter;
import com.skyruler.glonavin.report.SubwayReportData;
import com.skyruler.glonavin.xml.model.City;
import com.skyruler.glonavin.xml.model.MetroData;
import com.skyruler.glonavin.xml.parser.MetroParser;
import com.skyruler.socketclient.SocketClient;
import com.skyruler.socketclient.connection.intf.IStateListener;
import com.skyruler.socketclient.exception.ConnectionException;
import com.skyruler.socketclient.exception.UnFormatMessageException;
import com.skyruler.socketclient.filter.MessageIdFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GlonavinSdk {
    private static final String TAG = "GlonavinSdk";
    public static final String GLONAVIN_DEVICE_NAME = "FootSensor";
    public static final int BLUETOOTH_TYPE_ALL = 3;
    public static final int BLUETOOTH_TYPE_SUBWAY = 4;
    public static final int BLUETOOTH_TYPE_INDOOR = 5;
    private static final byte REPORT_ID_SUBWAY = 0x40;

    private int mBluetoothType = BLUETOOTH_TYPE_ALL;
    private static final long SCAN_PERIOD = 10000L;
    private static GlonavinSdk INSTANCE = new GlonavinSdk();
    private BluetoothManager bluetoothManager;
    private SocketClient socketClient;

    private boolean isTestStart;
    private boolean mScanning = false;

    private CopyOnWriteArrayList<IBleStateListener> connListeners;

    private GlonavinSdk() {
    }

    public static GlonavinSdk getInstance() {
        return INSTANCE;
    }

    public void setup(Context context) {
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
                    }
                }
            }
        });
    }

    public void setBluetoothMode(int bluetoothType) {
        this.mBluetoothType = bluetoothType;
    }

    public boolean isSubwayMode() {
        return mBluetoothType == BLUETOOTH_TYPE_SUBWAY;
    }

    public boolean isIndoorMode() {
        return mBluetoothType == BLUETOOTH_TYPE_INDOOR;
    }

    public void addConnectStateListener(IBleStateListener listener) {
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

    public void listenerForReport(final IDataReporter reporter) {
        this.socketClient.addMessageListener(new IMessageListener() {
            @Override
            public void processMessage(IMessage msg) {
                SubwayReportData subwayReportData = new SubwayReportData(msg);
                reporter.report(subwayReportData);
            }
        }, new MessageIdFilter(REPORT_ID_SUBWAY));
    }

    public void stopSubwayReport() {
        this.socketClient.removeMessageListener(new MessageIdFilter(REPORT_ID_SUBWAY));
    }

    public boolean chooseMode(DeviceModeCmd cmd) {
        boolean success = sendMessage(cmd);
        Log.d(TAG, "choose mode :" + cmd.toString() + "," + success);
        return success;
    }

    public boolean sendMetroLine(MetroLineCmd cmd) {
        boolean success = sendMessage(cmd);
        Log.d(TAG, "send subway line :" + cmd.toString() + "," + success);
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
        if (!isTestStart) {
            Log.d(TAG, "test did not start yet!!");
            return false;
        }
        SkipStationCmd cmd = new SkipStationCmd();
        boolean success = sendMessage(cmd);
        Log.d(TAG, "skip subway station :" + cmd.toString() + "," + success);
        return success;
    }

    public boolean tempStopStation() {
        if (!isTestStart) {
            Log.d(TAG, "test did not start yet!!");
            return false;
        }
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

    public void connect(BluetoothDevice bluetoothDevice) {
        GlonavinConnectOption option = new GlonavinConnectOption(bluetoothDevice);
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

    public void onDestroy() {
        socketClient.onDestroy();
        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
        if (mScanning && bluetoothAdapter != null) {
            bluetoothAdapter.stopLeScan(mLeScanCallback);
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
        } catch (ConnectionException e) {
            Log.e(TAG, "sendMessage failed :" + e.getMessage());
            return false;
        } catch (UnFormatMessageException e) {
            Log.e(TAG, "sendMessage failed :" + e.getMessage());
            return false;
        }
    }

    public void scanDevice(boolean enable) {
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


    public void enableBluetooth(boolean checked) {
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

    public boolean isBluetoothEnable() {
        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
        return bluetoothAdapter.isEnabled();
    }


    public City readSubwayLineFromXmlFile(String path) throws Exception {
        InputStream is = new FileInputStream(new File(path));
        MetroParser parser = new MetroParser();
        MetroData metroData = parser.parse(is);
        is.close();
        if (metroData != null) {
            Log.d(TAG, metroData.toString());
        }
        if (metroData == null || metroData.getCities() == null) {
            throw new IllegalStateException("xml file parse failed,metroData is null");
        }
        // 为了方便只取第一个city
        return metroData.getCities().get(0);
    }

}
