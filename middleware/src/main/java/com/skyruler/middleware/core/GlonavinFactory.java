package com.skyruler.middleware.core;

public class GlonavinFactory {

    private static final int MODE_NULL = -1;
    public static final int BLUETOOTH_TYPE_INDOOR = 0;
    public static final int BLUETOOTH_TYPE_SUBWAY = 1;
    public static final int BLUETOOTH_TYPE_RAILWAY = 2;
    private static final String[] MODES = new String[]{"室内模式", "地铁模式", "高铁模式"};


    private static AbsManager INSTANCE;

    public static void setupMode(int type) {
        if (INSTANCE != null) {
            INSTANCE.onDestroy();
            INSTANCE = null;
        }
        switch (type) {
            case BLUETOOTH_TYPE_RAILWAY:
                INSTANCE = new RailManager();
                break;
            case BLUETOOTH_TYPE_SUBWAY:
                INSTANCE = new SubwayManager();
                break;
            case BLUETOOTH_TYPE_INDOOR:
                INSTANCE = new IndoorManager();
                break;
            default:
                throw new IllegalArgumentException("非法的模式" + type);
        }
    }

    private GlonavinFactory() {
    }

    public static AbsManager getManagerInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("请先设置模式");
        }
        return INSTANCE;
    }

    public static int getMode() {
        if (INSTANCE instanceof RailManager) {
            return BLUETOOTH_TYPE_RAILWAY;
        } else if (INSTANCE instanceof SubwayManager) {
            return BLUETOOTH_TYPE_SUBWAY;
        } else if (INSTANCE instanceof IndoorManager) {
            return BLUETOOTH_TYPE_INDOOR;
        } else {
            return MODE_NULL;
        }
    }

    public static String[] getModeStrings(){
        return MODES;
    }
}
