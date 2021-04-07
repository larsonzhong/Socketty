package com.skyruler.gonavin.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skyruler.gonavin.R;
import com.skyruler.middleware.parser.xml.model.MetroLine;

class LineAdapter extends ArrayAdapter<MetroLine> {
    private final int mResourceId;

    LineAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.mResourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return makeView(position);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return makeView(position);
    }

    private View makeView(int position) {
        View view = LayoutInflater.from(getContext()).inflate(mResourceId, null);
        view.findViewById(R.id.cb_station_checkbox).setVisibility(View.GONE);
        view.findViewById(R.id.tv_station_id).setVisibility(View.GONE);
        TextView textView = view.findViewById(R.id.tv_station_name);

        MetroLine line = getItem(position);
        if (line != null) {
            textView.setText(line.getName());
        }
        return view;
    }
}