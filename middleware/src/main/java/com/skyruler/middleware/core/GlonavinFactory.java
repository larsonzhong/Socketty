package com.skyruler.middleware.core;

import android.content.Context;

public class GlonavinFactory {

    public static final int MODE_NULL = -1;
    public static final int BLUETOOTH_TYPE_INDOOR = 0;
    public static final int BLUETOOTH_TYPE_SUBWAY = 1;
    public static final int BLUETOOTH_TYPE_RAILWAY = 2;
    private static final String[] MODES = new String[]{"室内模式", "地铁模式", "高铁模式"};

    private static AbsManager INSTANCE;

    private GlonavinFactory() {
    }

    public static void setupMode(Context context,int type) {
        if (INSTANCE != null) {
            INSTANCE.onDestroy();
            INSTANCE = null;
        }
        switch (type) {
            case BLUETOOTH_TYPE_RAILWAY:
                INSTANCE = new RailManager(context);
                break;
            case BLUETOOTH_TYPE_SUBWAY:
                INSTANCE = new SubwayManager(context);
                break;
            case BLUETOOTH_TYPE_INDOOR:
                INSTANCE = new IndoorManager(context);
                break;
            default:
                throw new IllegalArgumentException("非法的模式" + type);
        }
    }

    public static AbsManager getManagerInstance() {
        return INSTANCE;
    }

    public static String[] getModeStrings(){
        return MODES;
    }
}
