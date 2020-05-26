package com.skyruler.gonavin.bluetooth;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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
import java.util.ArrayList;
import java.util.List;


public class DeviceSetupDialog extends AlertDialog implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "DeviceSetupDialog";
    private GlonavinSdk glonavinSdk;
    private String[] mModes;
    private String mDeviceMode;
    private MetroLine mMetroLine;
    private Station mStartStation;
    private Station mEndStation;
    private City city;
    private NameAdapter<Station> selectStartAdapter;
    private NameAdapter<Station> selectEndAdapter;
    private NameAdapter<MetroLine> selectLineAdapter;


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
        selectStartAdapter = new NameAdapter<>();
        selectStartSpinner.setAdapter(selectStartAdapter);
        selectStartSpinner.setOnItemSelectedListener(this);

        Spinner selectEndSpinner = mView.findViewById(R.id.spinner_select_end);
        selectEndAdapter = new NameAdapter<>();
        selectEndSpinner.setAdapter(selectEndAdapter);
        selectEndSpinner.setOnItemSelectedListener(this);

        Spinner selectLineSpinner = mView.findViewById(R.id.spinner_select_line);
        selectLineAdapter = new NameAdapter<>();
        selectLineSpinner.setAdapter(selectLineAdapter);
        selectLineSpinner.setOnItemSelectedListener(this);

        mView.findViewById(R.id.btn_exit_dialog).setOnClickListener(this);
        mView.findViewById(R.id.btn_select_xml).setOnClickListener(this);
        mView.findViewById(R.id.btn_send_mode).setOnClickListener(this);
        mView.findViewById(R.id.btn_send_start_end).setOnClickListener(this);
        mView.findViewById(R.id.btn_start_test).setOnClickListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        switch (adapterView.getId()) {
            case R.id.spinner_device_mode:
                mDeviceMode = mModes[pos];
                break;
            case R.id.spinner_select_line:
                mMetroLine = city.getMetroLines().get(pos);
                selectStartAdapter.setData(mMetroLine.getStations());
                selectStartAdapter.notifyDataSetChanged();
                selectEndAdapter.setData(mMetroLine.getStations());
                selectEndAdapter.notifyDataSetChanged();
                break;
            case R.id.spinner_select_start:
                mStartStation = (Station) selectStartAdapter.getItem(pos);
                break;
            case R.id.spinner_select_end:
                mEndStation = (Station) selectEndAdapter.getItem(pos);
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
            case R.id.btn_exit_dialog:
                dismiss();
                break;
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
        city = metroData.getCities().get(0);
        selectLineAdapter.setData(city.getMetroLines());
        selectLineAdapter.notifyDataSetChanged();
    }

    class NameAdapter<T> extends BaseAdapter {
        private List<T> objectList = new ArrayList<>();

        @Override
        public int getCount() {
            return objectList.size();
        }

        @Override
        public Object getItem(int pos) {
            return objectList.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return pos;
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
            View view = getLayoutInflater().inflate(android.R.layout.simple_spinner_item, null);
            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(name);
            return view;
        }

        void setData(List<T> stations) {
            this.objectList.clear();
            this.objectList.addAll(stations);
        }
    }

}
