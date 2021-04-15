package com.skyruler.gonavin.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.skyruler.gonavin.R;
import com.skyruler.middleware.parser.BaseStation;

import java.util.ArrayList;
import java.util.List;

public class StationAdapter extends BaseAdapter {
    private final ArrayList<BaseStation> stations = new ArrayList<>();
    private final ArrayList<BaseStation> selects = new ArrayList<>();
    private final LayoutInflater layoutInflater;
    private final boolean mutiSelect;

    StationAdapter(Context context, boolean mutiSelect) {
        this.mutiSelect = mutiSelect;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return stations.size();
    }

    @Override
    public BaseStation getItem(int pos) {
        return stations.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        View view = layoutInflater.inflate(R.layout.item_list_station, parent, false);
        TextView tvSid = view.findViewById(R.id.tv_station_id);
        TextView tvName = view.findViewById(R.id.tv_station_name);
        CheckBox cbStation = view.findViewById(R.id.cb_station_checkbox);

        BaseStation station = getItem(position);
        tvSid.setText(String.valueOf(station.getSid()));
        cbStation.setChecked(isChecked(station));
        tvName.setText(station.getName());
        cbStation.setVisibility(mutiSelect ? View.VISIBLE : View.GONE);
        cbStation.setOnCheckedChangeListener((buttonView, isChecked) -> setChecked(isChecked, station));

        return view;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        @SuppressLint("ViewHolder")
        View view = layoutInflater.inflate(R.layout.item_list_station, parent, false);
        view.findViewById(R.id.tv_station_id).setVisibility(View.GONE);
        view.findViewById(R.id.cb_station_checkbox).setVisibility(View.GONE);
        TextView tvName = view.findViewById(R.id.tv_station_name);

        BaseStation station = getItem(position);
        tvName.setText(station.getName());
        return view;
    }

    byte[] getSelectedStationIds() {
        byte[] ids = new byte[selects.size()];
        for (int i = 0; i < selects.size(); i++) {
            byte sid = selects.get(i).getSid();
            ids[i] = sid;
        }
        return ids;
    }

    private void setChecked(boolean isChecked, BaseStation station) {
        if (isChecked) {
            selects.add(station);
        } else {
            selects.remove(station);
        }
    }

    private boolean isChecked(BaseStation station) {
        return selects.contains(station);
    }

    void setData(List<? extends BaseStation> stations) {
        this.stations.clear();
        this.stations.addAll(stations);
        notifyDataSetChanged();
    }

}
