package com.skyruler.gonavin.bluetooth;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.skyruler.gonavin.R;
import com.skyruler.middleware.connection.BluetoothAccess;

import java.util.List;

public class ScanAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<BluetoothAccess> mLists;

    ScanAdapter(Context context, List<BluetoothAccess> list) {
        mContext = context;
        mLists = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.devices_item, null);
            viewHolder.image = convertView.findViewById(R.id.itemImage);
            viewHolder.title = convertView.findViewById(R.id.itemTitle);
            viewHolder.summary = convertView.findViewById(R.id.itemSummary);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        BluetoothAccess bluetoothAccess = getItem(position);
        if (bluetoothAccess.isConnected()) {
            viewHolder.image.setVisibility(View.VISIBLE);
        } else {
            viewHolder.image.setVisibility(View.INVISIBLE);
        }
        if (TextUtils.isEmpty(bluetoothAccess.getDeviceName())) {
            viewHolder.title.setText(bluetoothAccess.getDeviceAddress());
            viewHolder.summary.setVisibility(View.GONE);
        } else {
            viewHolder.title.setText(bluetoothAccess.getDeviceName());
            viewHolder.summary.setText(bluetoothAccess.getDeviceAddress());
            viewHolder.summary.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return mLists.size();
    }

    @Override
    public BluetoothAccess getItem(int position) {
        return mLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        ImageView image;
        TextView title;
        TextView summary;
    }
}
