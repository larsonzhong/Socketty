package com.skyruler.gonavin.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.skyruler.android.logger.Log;
import com.skyruler.glonavin.core.GlonavinFactory;
import com.skyruler.glonavin.core.SubwayManager;
import com.skyruler.glonavin.xml.model.City;
import com.skyruler.glonavin.xml.model.MetroLine;
import com.skyruler.glonavin.xml.model.Station;
import com.skyruler.gonavin.R;
import com.skyruler.gonavin.SubwayDataHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;


public class SubwaySetupDialog extends AlertDialog implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private String SD_ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    private SubwayManager glonavinSdk;
    private MetroLine mMetroLine;
    private byte mStartSID;
    private byte mEndSID;
    private City city;
    private NameAdapter<Station> selectStartAdapter;
    private NameAdapter<Station> selectEndAdapter;
    private NameAdapter<MetroLine> selectLineAdapter;
    private TextView editionTv;
    private Button btnSendDirection;

    public SubwaySetupDialog(Context context) {
        super(context);
        this.glonavinSdk = (SubwayManager) GlonavinFactory.getManagerInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        @SuppressLint("InflateParams")
        View mView = getLayoutInflater().inflate(R.layout.device_setup, null);
        initView(mView);
        super.setView(mView);
        super.onCreate(savedInstanceState);
    }

    private void initView(View mView) {
        editionTv = mView.findViewById(R.id.editionTv);
        btnSendDirection = mView.findViewById(R.id.btn_send_start_end);

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

        btnSendDirection.setOnClickListener(this);
        mView.findViewById(R.id.btn_exit_dialog).setOnClickListener(this);
        mView.findViewById(R.id.btn_send_mode).setOnClickListener(this);
        mView.findViewById(R.id.btn_select_xml).setOnClickListener(this);
        mView.findViewById(R.id.btn_send_xml).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        glonavinSdk.getEdition(edition -> {
            String version = edition.getSoftVersionName();
            Observable.empty()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(() -> editionTv.setText(version)).subscribe();
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        switch (adapterView.getId()) {
            case R.id.spinner_select_line:
                mMetroLine = city.getMetroLines().get(pos);
                selectStartAdapter.setData(mMetroLine.getStations());
                selectStartAdapter.notifyDataSetChanged();
                selectEndAdapter.setData(mMetroLine.getStations());
                selectEndAdapter.notifyDataSetChanged();
                break;
            case R.id.spinner_select_start:
                mStartSID = mMetroLine.getStations().get(pos).getSid();
                break;
            case R.id.spinner_select_end:
                mEndSID = mMetroLine.getStations().get(pos).getSid();
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
                selectSubwayXml();
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

    private void selectSubwayXml() {
        DialogProperties newTaskProperties = new DialogProperties();
        newTaskProperties.selection_mode = DialogConfigs.SINGLE_MODE;
        newTaskProperties.selection_type = DialogConfigs.FILE_SELECT;
        newTaskProperties.root = new File(SD_ROOT_PATH);
        newTaskProperties.error_dir = new File(SD_ROOT_PATH);

        final FilePickerDialog newTaskDialog = new FilePickerDialog(getContext(), newTaskProperties);
        newTaskDialog.setTitle(getContext().getString(R.string.select_xml));
        newTaskDialog.setNegativeBtnName(getContext().getString(R.string.cancel));
        newTaskDialog.setPositiveBtnName(getContext().getString(R.string.select));

        newTaskDialog.setDialogSelectionListener(files -> Observable.empty()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    try {
                        readSubwayXml(files[0]);
                        showToast(getContext().getString(R.string.select_xml_success));
                    } catch (Exception e) {
                        Log.e(e);
                        showToast(getContext().getString(R.string.select_xml_failed));
                    }
                }).subscribe());
        newTaskDialog.show();
    }

    private void sendStartEndStation() {
        boolean success = glonavinSdk.setTestDirection(mStartSID, mEndSID);
        showToast("发送起始点" + success);
        super.dismiss();
    }

    private void sendTestMode() {
        findViewById(R.id.btn_send_mode).setEnabled(false);
        boolean success = glonavinSdk.chooseMode();
        findViewById(R.id.btn_send_mode).setEnabled(true);
        showToast("发送模式" + success);
    }

    private void showToast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void sendMetroLine() {
        boolean success = glonavinSdk.sendMetroLine(mMetroLine);
        showToast("发送地铁路线" + success);
        if (success) {
            SubwayDataHolder.getInstance().setMetroLine(mMetroLine);
            btnSendDirection.setEnabled(true);
        }
    }

    private void readSubwayXml(String path) throws Exception {
        city = glonavinSdk.readSubwayLineFromXmlFile(path);
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
