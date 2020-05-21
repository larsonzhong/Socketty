package com.skyruler.gonavin;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.skyruler.android.logger.Log;
import com.skyruler.middleware.GlonavinSdk;
import com.skyruler.middleware.connection.GlonavinConnectOption;
import com.skyruler.middleware.connection.IBleScanListener;
import com.skyruler.xml.model.City;
import com.skyruler.xml.model.MetroData;
import com.skyruler.xml.model.MetroLine;
import com.skyruler.xml.parser.MetroParser;

import java.io.InputStream;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private GlonavinSdk glonavinSdk = new GlonavinSdk();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            InputStream is = getAssets().open("subway.xml");
//		    parser = new SaxBookParser();
//			parser = new DomBookParser();
            MetroParser parser = new MetroParser();
            MetroData metroData = parser.parse(is);
            if (metroData != null) {
                Log.d(TAG, metroData.toString());
            }
            is.close();
            if (metroData != null && metroData.getCities() != null) {
                for (City city : metroData.getCities()) {
                    for (MetroLine metroLine : city.getMetroLines()) {
                        byte[] bytes = metroLine.toBytes();
                        Log.d(TAG, "size: " + bytes.length + ", buf" + Arrays.toString(bytes));
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        glonavinSdk.setup(getApplicationContext(),new IBleScanListener(){
            @Override
            public void onScanResult(BluetoothDevice bluetoothDevice, boolean isConnected) {
                //
            }
        });
        glonavinSdk.scanDevice(true);

//        GlonavinConnectOption option = new GlonavinConnectOption(null);
//        glonavinSdk.setup(getApplicationContext());
//        glonavinSdk.connect(option);
//        glonavinSdk.chooseMode();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        glonavinSdk.scanDevice(false);
        glonavinSdk.onDestroy();
    }
}
