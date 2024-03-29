package com.skyruler.middleware.core;

import android.content.Context;

public class IndoorManager extends BaseManager {
    public static final String DEVICE_NAME = "FootSensor";

    IndoorManager(Context context) {
        super(context);
    }

    @Override
    public int getMode() {
        return GlonavinFactory.BLUETOOTH_TYPE_INDOOR;
    }

    @Override
    public String getDeviceName() {
        return DEVICE_NAME;
    }

}
