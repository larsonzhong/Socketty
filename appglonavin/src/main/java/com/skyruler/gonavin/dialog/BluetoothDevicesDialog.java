package com.skyruler.gonavin.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.skyruler.android.logger.Log;
import com.skyruler.gonavin.R;
import com.skyruler.middleware.connection.BluetoothAccess;
import com.skyruler.middleware.connection.IBleStateListener;
import com.skyruler.middleware.core.BaseManager;
import com.skyruler.middleware.core.GlonavinFactory;
import com.skyruler.middleware.core.RailManager;
import com.skyruler.middleware.core.SubwayManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class BluetoothDevicesDialog extends AlertDialog implements View.OnClickListener,
        AdapterView.OnItemClickListener, DialogInterface.OnClickListener {
    private static final int SCAN_PERIOD = 10000;
    private Button btSearch;
    private ProgressBar progressBar;
    private List<BluetoothAccess> mListDevices;
    private BluetoothAccess mSelectedDevice;
    private ScanAdapter mScanAdapter;
    private final Handler mHandler;
    private final BaseManager baseManager;
    private final IBleStateListener listener = new IBleStateListener() {
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
            boolean isSpacialDevice = device.getName() != null && (
                    device.getName().equals(SubwayManager.DEVICE_NAME)
                            || device.getName().equals(RailManager.DEVICE_NAME));
            if (isNewDevice && isSpacialDevice) {
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
        this.baseManager = GlonavinFactory.getManagerInstance();
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

        setButton(Dialog.BUTTON_NEGATIVE, getContext().getString(R.string.device_cancel), this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        baseManager.addConnectStateListener(listener);
        scanBleDevices();
    }

    @Override
    protected void onStop() {
        super.onStop();
        baseManager.removeConnectListener(listener);
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
        if (baseManager != null) {
            baseManager.scanDevice(enable);
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
            baseManager.disconnect();
        } else {
            BluetoothDevice device = mSelectedDevice.getBluetoothDevice();
            baseManager.connect(device);
        }
        showProgressBar(true);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        dismiss();
    }


    public static class ScanAdapter extends BaseAdapter {
        private final Context mContext;
        private final List<BluetoothAccess> mLists;

        ScanAdapter(Context context, List<BluetoothAccess> list) {
            mContext = context;
            mLists = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.devices_item, parent, false);
                viewHolder.image = convertView.findViewById(R.id.itemImage);
                viewHolder.title = convertView.findViewById(R.id.itemTitle);
                viewHolder.summary = convertView.findViewById(R.id.itemSummary);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            BluetoothAccess bluetoothAccess = getItem(position);
            if (bluetoothAccess.isConnected()) {
                viewHolder.image.setVisibility(View.VISIBLE);
            } else {
                viewHolder.image.setVisibility(View.INVISIBLE);
            }
            if (TextUtils.isEmpty(bluetoothAccess.getDeviceName())) {
                viewHolder.title.setText(bluetoothAccess.getDeviceAddress());
                viewHolder.summary.setVisibility(View.GONE);
            } else {
                viewHolder.title.setText(bluetoothAccess.getDeviceName());
                viewHolder.summary.setText(bluetoothAccess.getDeviceAddress());
                viewHolder.summary.setVisibility(View.VISIBLE);
            }
            return convertView;
        }

        @Override
        public int getCount() {
            return mLists.size();
        }

        @Override
        public BluetoothAccess getItem(int position) {
            return mLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class ViewHolder {
            ImageView image;
            TextView title;
            TextView summary;
        }
    }
}
