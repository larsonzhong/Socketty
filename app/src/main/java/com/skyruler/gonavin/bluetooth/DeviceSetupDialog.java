package com.skyruler.gonavin.bluetooth;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.skyruler.android.logger.Log;
import com.skyruler.gonavin.R;
import com.skyruler.middleware.GlonavinSdk;
import com.skyruler.middleware.xml.model.City;
import com.skyruler.middleware.xml.model.MetroData;
import com.skyruler.middleware.xml.model.MetroLine;
import com.skyruler.middleware.xml.model.Station;
import com.skyruler.middleware.xml.parser.MetroParser;

import java.io.InputStream;


public class DeviceSetupDialog extends AlertDialog implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "DeviceSetupDialog";
    private GlonavinSdk glonavinSdk;
    private String[] mModes;
    private String mDeviceMode;
    private MetroLine mMetroLine;
    private Station mStartStation;
    private Station mEndStation;
    private City city;
    private ArrayAdapter<Station> selectStartAdapter;
    private ArrayAdapter<Station> selectEndAdapter;
    private ArrayAdapter<MetroLine> selectLineAdapter;


    public DeviceSetupDialog(Context context, GlonavinSdk glonavinSdk) {
        super(context);
        this.glonavinSdk = glonavinSdk;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View mView = getLayoutInflater().inflate(R.layout.device_setup, null);
        initView(mView);
        super.setView(mView);
        super.onCreate(savedInstanceState);
    }

    private void initView(View mView) {
        mModes = getContext().getResources().getStringArray(R.array.device_mode);
        Spinner deviceModeSpinner = mView.findViewById(R.id.spinner_device_mode);
        ArrayAdapter<String> selectModeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, mModes);
        deviceModeSpinner.setAdapter(selectModeAdapter);
        deviceModeSpinner.setOnItemSelectedListener(this);

        Spinner selectStartSpinner = mView.findViewById(R.id.spinner_select_start);
        selectStartAdapter = new NameAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        selectStartSpinner.setAdapter(selectStartAdapter);
        selectStartSpinner.setOnItemSelectedListener(this);

        Spinner selectEndSpinner = mView.findViewById(R.id.spinner_select_end);
        selectEndAdapter = new NameAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        selectEndSpinner.setAdapter(selectEndAdapter);
        selectEndSpinner.setOnItemSelectedListener(this);

        Spinner selectLineSpinner = mView.findViewById(R.id.spinner_select_line);
        selectLineAdapter = new NameAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        selectLineSpinner.setAdapter(selectLineAdapter);
        selectLineSpinner.setOnItemSelectedListener(this);

        mView.findViewById(R.id.btn_select_xml).setOnClickListener(this);
        mView.findViewById(R.id.btn_send_mode).setOnClickListener(this);
        mView.findViewById(R.id.btn_send_start_end).setOnClickListener(this);
        mView.findViewById(R.id.btn_start_test).setOnClickListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        switch (view.getId()) {
            case R.id.spinner_device_mode:
                mDeviceMode = mModes[pos];
                break;
            case R.id.spinner_select_line:
                mMetroLine = city.getMetroLines().get(pos);
                selectStartAdapter.clear();
                selectStartAdapter.addAll(mMetroLine.getStations());
                selectStartAdapter.notifyDataSetChanged();
                selectEndAdapter.clear();
                selectEndAdapter.addAll(mMetroLine.getStations());
                selectEndAdapter.notifyDataSetChanged();
                break;
            case R.id.spinner_select_start:
                mStartStation = selectStartAdapter.getItem(pos);
                break;
            case R.id.spinner_select_end:
                mEndStation = selectEndAdapter.getItem(pos);
                break;
            default:
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.d("nothing select .");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send_mode:
                sendTestMode();
                break;
            case R.id.btn_select_xml:
                try {
                    readSubwayXml();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
                break;
            case R.id.btn_send_xml:
                sendMetroLine();
                break;
            case R.id.btn_send_start_end:
                sendStartEndStation();
                break;
            default:
        }
    }

    private void sendStartEndStation() {
        glonavinSdk.sendStartEndStation(mStartStation, mEndStation);
    }

    private void sendTestMode() {
        glonavinSdk.selectDeviceMode(mDeviceMode);
    }

    private void sendMetroLine() {
        glonavinSdk.sendMetroLine(mMetroLine);
    }

    private void readSubwayXml() throws Exception {
        InputStream is = getContext().getAssets().open("subway.xml");
        MetroParser parser = new MetroParser();
        MetroData metroData = parser.parse(is);
        is.close();
        if (metroData != null) {
            Log.d(TAG, metroData.toString());
        }
        if (metroData == null || metroData.getCities() == null) {
            return;
        }

        // 为了方便只取第一个city
        City city = metroData.getCities().get(0);
        selectLineAdapter.clear();
        selectLineAdapter.addAll(city.getMetroLines());
        selectLineAdapter.notifyDataSetChanged();
    }

    class NameAdapter<T> extends ArrayAdapter<T> {
        private int mResourceId;

        NameAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            this.mResourceId = textViewResourceId;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            String name = "";
            Object obj = getItem(position);
            if (obj instanceof MetroLine) {
                name = ((MetroLine) obj).getName();
            } else if (obj instanceof Station) {
                name = ((Station) obj).getName();
            }
            View view = getLayoutInflater().inflate(mResourceId, null);
            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(name);
            return view;
        }
    }

}
