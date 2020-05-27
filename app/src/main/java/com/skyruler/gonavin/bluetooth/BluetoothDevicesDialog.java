package com.skyruler.gonavin.bluetooth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.skyruler.android.logger.Log;
import com.skyruler.gonavin.R;
import com.skyruler.middleware.GlonavinSdk;
import com.skyruler.middleware.connection.BluetoothAccess;
import com.skyruler.middleware.connection.GlonavinConnectOption;
import com.skyruler.middleware.connection.IBleStateListener;

import java.util.ArrayList;
import java.util.List;

/**
 * ......................-~~~~~~~~~-._       _.-~~~~~~~~~-.
 * ............... _ _.'              ~.   .~              `.__
 * ..............'//     NO           \./      BUG         \\`.
 * ............'//                     |                     \\`.
 * ..........'// .-~"""""""~~~~-._     |     _,-~~~~"""""""~-. \\`.
 * ........'//.-"                 `-.  |  .-'                 "-.\\`.
 * ......'//______.============-..   \ | /   ..-============.______\\`.
 * ....'______________________________\|/______________________________`.
 * ..larsonzhong@163.com      created in 2018/7/6     @author : larsonzhong
 * <p>
 * * * * * * * * * 加速度惯导设备连接对话框* * * * * * * * * * * * * * * * *
 */
public class BluetoothDevicesDialog extends AlertDialog implements View.OnClickListener,
        AdapterView.OnItemClickListener, DialogInterface.OnClickListener {
    private static final String GLONAVIN_DEVICE_NAME = "FootSensor";
    private static final int SCAN_PERIOD = 10000;
    private Handler mHandler;
    private Button btSearch;
    private ProgressBar progressBar;
    private List<BluetoothAccess> mListDevices;
    private BluetoothAccess mSelectedDevice;
    private ScanAdapter mScanAdapter;
    private GlonavinSdk glonavinSdk;
    private IBleStateListener listener = new IBleStateListener() {
        @Override
        public void onScanResult(BluetoothDevice bluetoothDevice, boolean isConnected) {
            for (BluetoothAccess listDevice : mListDevices) {
                if (listDevice.getDeviceAddress().equals(bluetoothDevice.getAddress())) {
                    listDevice.setConnected(isConnected);
                    return;
                }
            }
            if (TextUtils.isEmpty(bluetoothDevice.getName())) {
                return;
            }
            BluetoothAccess bluetoothAccess = new BluetoothAccess(bluetoothDevice,isConnected);
            mListDevices.add(bluetoothAccess);
            mScanAdapter.notifyDataSetChanged();
        }

        @Override
        public void onConnected(BluetoothDevice bluetoothDevice) {
            updateDeviceState(bluetoothDevice, true);
        }

        @Override
        public void onDisconnect(BluetoothDevice bluetoothDevice) {
            updateDeviceState(bluetoothDevice, false);
        }
    };

    private void updateDeviceState(final BluetoothDevice bluetoothDevice, final boolean connected) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mSelectedDevice == null) {
                    Log.e("无法更新连接状态，mSelectedDevice == null");
                    return;
                }
                for (BluetoothAccess device : mListDevices) {
                    if (device.getDeviceAddress().equals(bluetoothDevice.getAddress())) {
                        device.setConnected(connected);
                    }
                }
                mScanAdapter.notifyDataSetChanged();
                showProgressBar(false);
                //dismiss();
            }
        });
    }

    private BluetoothDevicesDialog(Context mContext) {
        super(mContext);
    }

    public BluetoothDevicesDialog(Context mContext, GlonavinSdk glonavinSdk) {
        this(mContext);
        this.glonavinSdk = glonavinSdk;
        this.mHandler = new Handler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("dialog onCreate");
        View mView = getLayoutInflater().inflate(R.layout.devices_dialog, null);
        initView(mView);
        super.setView(mView);
        super.onCreate(savedInstanceState);
    }

    private void initView(View mView) {
        btSearch = mView.findViewById(R.id.btSearch);
        btSearch.setOnClickListener(this);
        progressBar = mView.findViewById(R.id.progressBar);

        mListDevices = new ArrayList<>();
        mScanAdapter = new ScanAdapter(getContext(), mListDevices);
        ListView mLvDevices = mView.findViewById(R.id.lvDevices);
        mLvDevices.setAdapter(mScanAdapter);
        mLvDevices.setOnItemClickListener(this);

        setButton(Dialog.BUTTON_NEGATIVE, getContext().getString(R.string.cancel), this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        glonavinSdk.addBleStateListener(listener);
        scanBleDevices();
    }

    @Override
    protected void onStop() {
        super.onStop();
        glonavinSdk.removeConnectListener(listener);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btSearch) {
            scanBleDevices();
        }
    }

    private void scanBleDevices() {
        Log.d("dialog onStart");
        showProgressBar(true);
        scanBluetooth(true);
        stopScanDelay();
    }

    private void showProgressBar(boolean isShow) {
        if (progressBar != null) {
            progressBar.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
    }

    private void scanBluetooth(boolean enable) {
        if (enable) {
            mListDevices.clear();
            mScanAdapter.notifyDataSetChanged();
        }
        if (glonavinSdk != null) {
            glonavinSdk.scanDevice(enable);
            btSearch.setEnabled(false);
        }
    }

    private void stopScanDelay() {
        btSearch.setEnabled(false);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showProgressBar(false);
                scanBluetooth(false);
                btSearch.setEnabled(true);
            }
        }, SCAN_PERIOD);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        mSelectedDevice = mListDevices.get(position);
        if (mSelectedDevice.isConnected()) {
            glonavinSdk.disconnect();
        } else {
            BluetoothDevice device = mSelectedDevice.getBluetoothDevice();
            GlonavinConnectOption option = new GlonavinConnectOption(device);
            glonavinSdk.connect(option);
        }
        showProgressBar(true);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        dismiss();
    }


}
