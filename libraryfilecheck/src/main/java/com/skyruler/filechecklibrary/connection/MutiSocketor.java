package com.skyruler.filechecklibrary.connection;

import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 多通道连接器，使用方法如下：
 * 1. setup
 * 2. getSocketChanel获取SocketChanel
 * 3. 通过SocketChanel的方法调用其功能
 */
public class MutiSocketor {

    private Map<String, SocketChanel> socketChanels;

    private static MutiSocketor instance = new MutiSocketor();
    private WeakReference<Context> contextRef;
    private IConnectStateListener listener;

    public static MutiSocketor getInstance() {
        return instance;
    }

    private MutiSocketor() {
    }

    public void setup(Context context, IConnectStateListener listener) {
        this.contextRef = new WeakReference<>(context);
        this.listener = listener;
        this.socketChanels = new HashMap<>();
    }

    public SocketChanel getSocketChanel(String ip) {
        if (socketChanels == null) {
            socketChanels = new HashMap<>();
        }
        if (!socketChanels.containsKey(ip)) {
            SocketChanel socketChanel = new SocketChanel(contextRef.get(), ip);
            socketChanel.addConnectStateListener(listener);
            socketChanels.put(ip, socketChanel);
        }
        return socketChanels.get(ip);
    }

    public void onDestroy() {
        for (Map.Entry<String, SocketChanel> entry : socketChanels.entrySet()) {
            SocketChanel chanel = entry.getValue();
            chanel.onDestroy();
        }
        contextRef = null;
        listener = null;
        socketChanels = null;
    }

    public void disconnect() {
        for (Map.Entry<String, SocketChanel> entry : socketChanels.entrySet()) {
            entry.getValue().disconnect();
        }
    }

}
