package com.skyruler.gonavin.bluetooth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.skyruler.android.logger.Log;
import com.skyruler.glonavin.GlonavinSdk;
import com.skyruler.glonavin.connection.BluetoothAccess;
import com.skyruler.glonavin.connection.IBleStateListener;
import com.skyruler.gonavin.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.skyruler.glonavin.GlonavinSdk.BLUETOOTH_TYPE_SUBWAY;


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
        public void onScanResult(BluetoothDevice device, boolean isConnected) {
            Log.d(" device=" + device.getName() + " " + device.getAddress() + " " + device.getType());
            boolean isNewDevice = true;
            for (BluetoothAccess access : mListDevices) {
                if (access.getDeviceAddress().equals(device.getAddress())) {
                    access.setConnected(isConnected);
                    isNewDevice = false;
                    break;
                }
            }
            boolean isGlonavinMode = glonavinSdk.isSubwayMode() || glonavinSdk.isIndoorMode();
            boolean isSpacialDevice = device.getName() != null && device.getName().equals(GlonavinSdk.GLONAVIN_DEVICE_NAME);
            if (isNewDevice && isGlonavinMode && isSpacialDevice) {
                mListDevices.add(new BluetoothAccess(device, isConnected));
            }
            refreshDeviceList();
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
        if (mSelectedDevice == null) {
            Log.e("无法更新连接状态，mSelectedDevice == null");
            return;
        }
        for (BluetoothAccess device : mListDevices) {
            if (device.getDeviceAddress().equals(bluetoothDevice.getAddress())) {
                device.setConnected(connected);
            }
        }
        refreshDeviceList();
    }

    private void refreshDeviceList() {
        Observable.empty()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                            Log.d("mBleDevices.size=" + mListDevices.size());
                            showProgressBar(false);
                            mScanAdapter.notifyDataSetChanged();
                        }
                ).subscribe();
    }

    public BluetoothDevicesDialog(Context mContext) {
        super(mContext);
        this.glonavinSdk = GlonavinSdk.getInstance();
        this.mHandler = new Handler();
        this.glonavinSdk.setBluetoothMode(BLUETOOTH_TYPE_SUBWAY);
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

        setButton(Dialog.BUTTON_NEGATIVE, getContext().getString(R.string.device_cancel), this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        glonavinSdk.addConnectStateListener(listener);
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
        mHandler.postDelayed(() -> {
            showProgressBar(false);
            scanBluetooth(false);
            btSearch.setEnabled(true);
        }, SCAN_PERIOD);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        mSelectedDevice = mListDevices.get(position);
        if (mSelectedDevice.isConnected()) {
            glonavinSdk.disconnect();
        } else {
            BluetoothDevice device = mSelectedDevice.getBluetoothDevice();
            glonavinSdk.connect(device);
        }
        showProgressBar(true);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        dismiss();
    }


}
