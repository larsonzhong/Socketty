package com.skyruler.gonavin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.skyruler.gonavin.bluetooth.BluetoothDevicesDialog;
import com.skyruler.gonavin.bluetooth.DeviceSetupDialog;
import com.skyruler.gonavin.chart.DynamicLineChartManager;
import com.skyruler.middleware.GlonavinSdk;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private boolean isDataReviewing;
    private boolean isTestStart;
    private GlonavinSdk glonavinSdk = new GlonavinSdk();
    private DynamicLineChartManager dynamicLineChartManager;
    private BluetoothDevicesDialog mDeviceDialog;
    private DeviceSetupDialog mSetupDialog;
    private Button btnDtTest;

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
        btnDtTest = findViewById(R.id.btn_dt_state);

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
        int icon = R.mipmap.bluetooth_disabled;
        if (glonavinSdk.isConnected()) {
            icon = R.mipmap.bluetooth_connected;
        }
        menu.findItem(R.id.action_connect_device).setIcon(icon);
        String testState = isTestStart ? getString(R.string.action_stop_test) : getString(R.string.action_start_test);
        menu.findItem(R.id.action_start_test).setTitle(testState);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_connect_device:
                showBleDeviceDialog();
                break;
            case R.id.action_start_test:
                showSetupDialog();
                break;
            case R.id.action_start_review:
                // startDataReview();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSetupDialog() {
        if (mSetupDialog == null) {
            mSetupDialog = new DeviceSetupDialog(this, glonavinSdk);
        }
        mSetupDialog.show();
    }

    private void startSubwayTest() {
        //开启测试才记录数据
        isTestStart = !isTestStart;
        btnDtTest.setClickable(isTestStart);
        showToast(isTestStart ? getString(R.string.action_start_test) : getString(R.string.action_stop_test));
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
