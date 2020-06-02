package com.skyruler.gonavin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.skyruler.gonavin.bluetooth.BluetoothDevicesDialog;
import com.skyruler.gonavin.bluetooth.DeviceSetupDialog;
import com.skyruler.gonavin.chart.DynamicLineChartManager;
import com.skyruler.middleware.GlonavinSdk;
import com.skyruler.middleware.command.TestControlCmd;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private boolean isDataReviewing;
    private GlonavinSdk glonavinSdk = new GlonavinSdk();
    private DynamicLineChartManager dynamicLineChartManager;
    private BluetoothDevicesDialog mDeviceDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initGlonavin();
        //init();
    }

    private void initGlonavin() {
        glonavinSdk.setup(getApplicationContext());
        /*GlonavinConnectOption option = new GlonavinConnectOption(null);
        glonavinSdk.setup(getApplicationContext());
        glonavinSdk.connect(option);
        glonavinSdk.chooseMode();*/
    }

    private void initView() {
        //init MpChart
        LineChart mChart = findViewById(R.id.chart);
        List<Integer> colour = new ArrayList<>();
        colour.add(Color.BLACK);
        colour.add(Color.CYAN);
        List<String> names = new ArrayList<>();
        names.add("地铁状态");
        names.add("加速度X");
        dynamicLineChartManager = new DynamicLineChartManager(mChart, names, colour);
        dynamicLineChartManager.setYAxis((float) 0.4, (float) -0.4, 20);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                new DeviceSetupDialog(this, glonavinSdk).show();
                break;
            case R.id.action_start_test:
                startOrStop();
                break;
            case R.id.action_start_review:
                // startDataReview();
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
        showToast(isStart ? getString(R.string.action_stop_test) : getString(R.string.action_start_test)+" 发送" + success);
        invalidateOptionsMenu();
    }

    private void showBleDeviceDialog() {
        if (mDeviceDialog == null) {
            mDeviceDialog = new BluetoothDevicesDialog(this, glonavinSdk);
        }
        mDeviceDialog.show();
    }

    private void showToast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
    }
}
