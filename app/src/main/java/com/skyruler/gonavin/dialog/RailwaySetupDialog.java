package com.skyruler.gonavin.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.skyruler.android.logger.Log;
import com.skyruler.gonavin.R;
import com.skyruler.middleware.command.railway.DeviceMode;
import com.skyruler.middleware.core.GlonavinFactory;
import com.skyruler.middleware.core.RailManager;
import com.skyruler.middleware.parser.csv.model.RailwayLine;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class RailwaySetupDialog extends AlertDialog implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private String SD_ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    private RailManager glonavinSdk;
    private byte mStartSID;
    private byte mEndSID;
    private StationAdapter selectStartAdapter;
    private StationAdapter selectEndAdapter;
    private StationAdapter selectSkipAdapter;
    private StationAdapter selectStopAdapter;
    private TextView editionTv;
    private TextView selectLineTv;
    private Button btnSendDirection;
    private Button btnSendLine;
    private Button btnSendSkip;
    private Button btnSendStop;

    public RailwaySetupDialog(Context context) {
        super(context);
        this.glonavinSdk = (RailManager) GlonavinFactory.getManagerInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        @SuppressLint("InflateParams")
        View mView = getLayoutInflater().inflate(R.layout.railway_device_setup, null);
        initView(mView);
        super.setView(mView);
        super.onCreate(savedInstanceState);
    }

    private void initView(View mView) {
        editionTv = mView.findViewById(R.id.editionTv);
        selectLineTv = mView.findViewById(R.id.tv_selected_line);
        btnSendDirection = mView.findViewById(R.id.btn_send_start_end);
        btnSendSkip = mView.findViewById(R.id.btn_send_skip);
        btnSendStop = mView.findViewById(R.id.btn_send_stop);
        btnSendLine = mView.findViewById(R.id.btn_send_line);

        Spinner selectStartSpinner = mView.findViewById(R.id.spinner_select_start);
        selectStartAdapter = new StationAdapter(getContext(), false);
        selectStartSpinner.setAdapter(selectStartAdapter);
        selectStartSpinner.setOnItemSelectedListener(this);

        Spinner selectEndSpinner = mView.findViewById(R.id.spinner_select_end);
        selectEndAdapter = new StationAdapter(getContext(), false);
        selectEndSpinner.setAdapter(selectEndAdapter);
        selectEndSpinner.setOnItemSelectedListener(this);

        Spinner selectSkipSpinner = mView.findViewById(R.id.spinner_select_skip);
        selectSkipAdapter = new StationAdapter(getContext(), true);
        selectSkipSpinner.setAdapter(selectSkipAdapter);

        Spinner selectStopSpinner = mView.findViewById(R.id.spinner_select_stop);
        selectStopAdapter = new StationAdapter(getContext(), true);
        selectStopSpinner.setAdapter(selectStopAdapter);

        btnSendSkip.setOnClickListener(this);
        btnSendStop.setOnClickListener(this);
        btnSendLine.setOnClickListener(this);
        btnSendDirection.setOnClickListener(this);
        mView.findViewById(R.id.btn_exit_dialog).setOnClickListener(this);
        mView.findViewById(R.id.btn_send_mode).setOnClickListener(this);
        mView.findViewById(R.id.btn_select_line).setOnClickListener(this);
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
            case R.id.spinner_select_start:
                mStartSID = glonavinSdk.getRailwayLine().getStations().get(pos).getSid();
                break;
            case R.id.spinner_select_end:
                mEndSID = glonavinSdk.getRailwayLine().getStations().get(pos).getSid();
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
            case R.id.btn_select_line:
                selectRailwayLine();
                break;
            case R.id.btn_send_skip:
                sendSkipStation();
                break;
            case R.id.btn_send_stop:
                sendStopStation();
                break;
            case R.id.btn_send_line:
                sendMetroLine();
                break;
            case R.id.btn_send_start_end:
                sendStartEndStation();
                break;
            default:
        }
    }

    private void selectRailwayLine() {
        DialogProperties newTaskProperties = new DialogProperties();
        newTaskProperties.selection_mode = DialogConfigs.SINGLE_MODE;
        newTaskProperties.selection_type = DialogConfigs.FILE_SELECT;
        newTaskProperties.root = new File(SD_ROOT_PATH);
        newTaskProperties.error_dir = new File(SD_ROOT_PATH);
        newTaskProperties.extensions= new String[]{"csv"};

        final FilePickerDialog newTaskDialog = new FilePickerDialog(getContext(), newTaskProperties);
        newTaskDialog.setTitle(getContext().getString(R.string.select_line));
        newTaskDialog.setNegativeBtnName(getContext().getString(R.string.cancel));
        newTaskDialog.setPositiveBtnName(getContext().getString(R.string.select));

        newTaskDialog.setDialogSelectionListener(files -> Observable.empty()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    try {
                        readRailwayFile(files[0]);
                        showToast(getContext().getString(R.string.select_line_success));

                        btnSendLine.setEnabled(true);
                        btnSendSkip.setEnabled(true);
                        btnSendStop.setEnabled(true);
                        btnSendDirection.setEnabled(true);
                    } catch (Exception e) {
                        Log.e(e);
                        showToast(getContext().getString(R.string.select_line_failed));
                    }
                }).subscribe());
        newTaskDialog.show();
    }

    private void sendStartEndStation() {
        boolean success = glonavinSdk.setTestDirection(mStartSID, mEndSID);
        showToast("发送起始点" + success);
        if (success) {
            super.dismiss();
        }
    }

    private void sendTestMode() {
        findViewById(R.id.btn_send_mode).setEnabled(false);
        boolean success = glonavinSdk.chooseMode(DeviceMode.Mode.Railway);
        findViewById(R.id.btn_send_mode).setEnabled(true);
        showToast("发送模式" + success);
    }

    private void showToast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void sendMetroLine() {
        String name = glonavinSdk.getRailwayLine().getName();
        boolean success = glonavinSdk.chooseLine(name);
        showToast("发送地铁路线" + success);
    }

    private void sendStopStation() {
        byte[] ids = selectStopAdapter.getSelectedStationIds();
        boolean success = glonavinSdk.tempStopStation(ids);
        showToast("发送临停" + success);
    }

    private void sendSkipStation() {
        byte[] ids = selectSkipAdapter.getSelectedStationIds();
        boolean success = glonavinSdk.skipStation(ids);
        showToast("发送跳站" + success);
    }

    private void readRailwayFile(String path) throws Exception {
        RailwayLine line = glonavinSdk.readStationLineFile(path);
        selectLineTv.setText(line.getName());
        selectSkipAdapter.setData(line.getStations());
        selectStopAdapter.setData(line.getStations());
        selectStartAdapter.setData(line.getStations());
        selectEndAdapter.setData(line.getStations());
    }


}
