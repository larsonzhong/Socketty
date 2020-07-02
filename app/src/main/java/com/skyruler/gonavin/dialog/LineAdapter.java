package com.skyruler.gonavin.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.skyruler.gonavin.R;
import com.skyruler.middleware.parser.xml.model.MetroLine;

import java.util.ArrayList;
import java.util.List;

class LineAdapter extends BaseAdapter {
    private List<MetroLine> objectList = new ArrayList<>();
    private LayoutInflater inflate;

    LineAdapter(Context context) {
        inflate = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return objectList.size();
    }

    @Override
    public MetroLine getItem(int pos) {
        return objectList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = inflate.inflate(R.layout.item_list_station, null);
        TextView textView = view.findViewById(R.id.tv_station_name);
        view.findViewById(R.id.tv_station_id).setVisibility(View.GONE);
        view.findViewById(R.id.cb_station_checkbox).setVisibility(View.GONE);

        MetroLine line = getItem(position);
        textView.setText(line.getName());
        return view;
    }

    void setData(List<MetroLine> stations) {
        this.objectList.clear();
        this.objectList.addAll(stations);
    }
}