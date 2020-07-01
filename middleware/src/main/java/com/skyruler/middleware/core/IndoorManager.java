package com.skyruler.middleware.core;

import android.content.Context;

class IndoorManager extends AbsManager {
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
