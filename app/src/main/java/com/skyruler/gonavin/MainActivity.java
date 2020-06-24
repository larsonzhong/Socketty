package com.skyruler.gonavin;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.skyruler.android.logger.Log;
import com.skyruler.glonavin.GlonavinSdk;
import com.skyruler.glonavin.command.TestControlCmd;
import com.skyruler.glonavin.connection.IBleStateListener;
import com.skyruler.glonavin.report.BaseReportData;
import com.skyruler.glonavin.report.IDataReporter;
import com.skyruler.glonavin.report.SubwayReportData;
import com.skyruler.glonavin.xml.model.Station;
import com.skyruler.gonavin.bluetooth.BluetoothDevicesDialog;
import com.skyruler.gonavin.bluetooth.DeviceSetupDialog;

import java.util.List;

public class MainActivity extends AppCompatActivity implements IDataReporter, View.OnClickListener {
    private static final String TAG = "MainActivity";
    private GlonavinSdk glonavinSdk = GlonavinSdk.getInstance();
    private BluetoothDevicesDialog mDeviceDialog;

    private TextView tvHardVersionName;
    private TextView tvSoftVersionName;
    private TextView tvProtocolVersionName;
    private TextView tvDtState;
    private TextView tvSeqNum;
    private TextView tvLatitude;
    private TextView tvLongitude;
    private TextView tvValidLoc;
    private TextView tvSiteID;
    private TextView tvBattery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGlonavin();
        initView();
    }

    private void initView() {
        tvHardVersionName = findViewById(R.id.tvHardVersionName);
        tvSoftVersionName = findViewById(R.id.tvSoftVersionName);
        tvProtocolVersionName = findViewById(R.id.tvProtocolVersionName);

        tvDtState = findViewById(R.id.tvDtState);
        tvSeqNum = findViewById(R.id.tvSeqNum);
        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);

        tvValidLoc = findViewById(R.id.tvValidLoc);
        tvSiteID = findViewById(R.id.tvSiteID);
        tvBattery = findViewById(R.id.tvBattery);

        findViewById(R.id.btnSkipStation).setOnClickListener(this);
        findViewById(R.id.btnTempStop).setOnClickListener(this);
        findViewById(R.id.btnGetEdition).setOnClickListener(this);
    }

    private void initGlonavin() {
        glonavinSdk.setup(getApplicationContext());
        glonavinSdk.addConnectStateListener(new IBleStateListener() {
            @Override
            public void onScanResult(BluetoothDevice bluetoothDevice, boolean isConnected) {

            }

            @Override
            public void onConnected(BluetoothDevice bluetoothDevice) {
                glonavinSdk.listenerForReport(MainActivity.this);
            }

            @Override
            public void onDisconnect(BluetoothDevice bluetoothDevice) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        glonavinSdk.stopSubwayReport();
        glonavinSdk.scanDevice(false);
        glonavinSdk.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int icon = glonavinSdk.isConnected() ? R.mipmap.bluetooth_connected : R.mipmap.bluetooth_disabled;
        menu.findItem(R.id.action_connect_device).setIcon(icon);

        int iconConfig = glonavinSdk.isConnected() ? R.mipmap.config_enable : R.mipmap.config_disable;
        menu.findItem(R.id.action_config_test).setIcon(iconConfig);
        menu.findItem(R.id.action_config_test).setCheckable(glonavinSdk.isConnected());

        String testState = glonavinSdk.isTestStart() ? getString(R.string.action_stop_test) : getString(R.string.action_start_test);
        menu.findItem(R.id.action_start_test).setTitle(testState);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_connect_device:
                showBleDeviceDialog();
                break;
            case R.id.action_config_test:
                new DeviceSetupDialog(this).show();
                break;
            case R.id.action_start_test:
                startOrStop();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startOrStop() {
        boolean isStart = glonavinSdk.isTestStart();
        boolean success = glonavinSdk.startTest(new TestControlCmd(!isStart));
        //开启测试才记录数据
        showToast((isStart ? getString(R.string.action_stop_test) : getString(R.string.action_start_test)) + " 发送" + success);
        invalidateOptionsMenu();
    }

    private void showBleDeviceDialog() {
        if (mDeviceDialog == null) {
            mDeviceDialog = new BluetoothDevicesDialog(this);
        }
        mDeviceDialog.show();
    }

    private void showToast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
    }

    @Override
    public void report(final BaseReportData baseReportData) {
        if (baseReportData instanceof SubwayReportData) {
            SubwayReportData data = (SubwayReportData) baseReportData;
            int index = getSubwayStationIndex(data);

            Log.d(TAG, index + "收到定位上报>>>" + data.toString());
            showText(tvDtState, data.getAccStateStr());
            showText(tvSeqNum, data.getSeqNum() + "");
            showText(tvLatitude, String.valueOf(data.getLatitude()));
            showText(tvLongitude, String.valueOf(data.getLongitude()));

            showText(tvValidLoc, getString(R.string.loc_valid, data.isValidLoc() + ""));
            showText(tvSiteID, getString(R.string.site_id, parseSiteID(data.getSiteID())));
            showText(tvBattery, getString(R.string.battery, data.getBattery()));
        }
    }

    private String parseSiteID(byte siteID) {
        List<Station> stations = SubwayDataHolder.getInstance().getMetroLine().getStations();
        for (Station station : stations) {
            if (station.getSid() == siteID - 1) {
                return siteID + station.getName();
            }
        }
        return "解析异常";
    }

    private int getSubwayStationIndex(SubwayReportData data) {
        // 文档上说是Sid+1 = getSiteID();
        int siteID = data.getSiteID() - 1;
        List<Station> stations = SubwayDataHolder.getInstance().getMetroLine().getStations();
        for (int index = 0; index < stations.size(); index++) {
            if (stations.get(index).getSid() == siteID) {
                return index;
            }
        }
        return -1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSkipStation:
                boolean success = glonavinSdk.skipStation();
                showToast("跳过站点发送" + (success ? "成功" : "失败"));
                break;
            case R.id.btnTempStop:
                boolean isSuccess = glonavinSdk.tempStopStation();
                showToast("临时停车发送" + (isSuccess ? "成功" : "失败"));
                break;
            case R.id.btnGetEdition:
                glonavinSdk.getEdition(edition -> {
                    showText(tvHardVersionName, edition.getHardVersionName());
                    showText(tvSoftVersionName, edition.getSoftVersionName());
                    showText(tvProtocolVersionName, edition.getPortoVersionName());
                });
                break;
            default:
        }
    }

    private void showText(final TextView view, final String text) {
        runOnUiThread(() -> view.setText(text));
    }
}
