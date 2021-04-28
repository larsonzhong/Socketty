package com.skyruler.socketclient.connection.socket;

import android.util.Log;

import com.skyruler.socketclient.connection.socket.conf.SocketConnectOption;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ......................-~~~~~~~~~-._       _.-~~~~~~~~~-.
 * ............... _ _.'              ~.   .~              `.__
 * ..............'//     NO           \./      BUG         \\`.
 * ............'//                     |                     \\`.
 * ..........'// .-~"""""""~~~~-._     |     _,-~~~~"""""""~-. \\`.
 * ........'//.-"                 `-.  |  .-'                 "-.\\`.
 * ......'//______.============-..   \ | /   ..-============.______\\`.
 * ....'______________________________\|/______________________________`.
 * ..larsonzhong@163.com      created in 2018/8/15     @author : larsonzhong
 */
public class ReconnectManager {
    private static final String TAG = "ReconnectManager >>>";

    /*是否处理connection的状态变化*/
    private AtomicBoolean isAttach;

    /*当前连接失败次数,不包括断开异常*/
    private int mCurrentTimes;
    /*是否需要重连管理器*/
    private boolean isReconnectEnable;
    /* 重连间隔*/
    private long reconnectInterval = SocketConnectOption.DEFAULT_RECONNECT_INTERVAL;
    /*最大重连次数*/
    private int mMaxConnectionFailedTimes = SocketConnectOption.DEFAULT_RECONNECT_MAX_ATTEMPT;
    /*当前连接*/
    private BaseSocketConnection connection;
    /* 重连定时器 */
    private Timer timer = new Timer();

    public ReconnectManager(SocketConnectOption connectOption) {
        if (connectOption != null) {
            this.mMaxConnectionFailedTimes = connectOption.getReconnectMaxAttemptTimes();
            this.reconnectInterval = connectOption.getReconnectInterval();
            this.isReconnectEnable = connectOption.isReconnectAllowed();
        }
        this.isAttach = new AtomicBoolean();
        this.reset();
    }

    /**
     * 关联到某一个连接管理器
     *
     * @param connection 当前连接
     */
    public void attach(BaseSocketConnection connection) {
        this.isAttach.set(true);
        this.connection = connection;
    }

    /**
     * Unlink the current connection manager
     * 解除连接当前的连接管理器
     */
    public void detach() {
        Log.i(TAG, "detach");
        reset();
        isAttach.set(false);
        connection = null;
        isReconnectEnable = false;
        mMaxConnectionFailedTimes = 0;
        reconnectInterval = 0;
    }

    /**
     * The reset is introduced because there is no need to continue reconnecting after the connection is successful,
     * then all the states of the reconnect manager need to be reset to handle the new reconnection operation.
     * 引入reset是因为当连接成功后就不需要继续重连，则需要把重连管理器所有状态重置以处理新的重连操作
     */
    private void reset() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        mCurrentTimes = 0;
    }

    private void reconnectDelay() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "Handler reconnect ..." + mCurrentTimes);
                if (!connection.isConnected()) {
                    connection.performConnect(true);
                }
            }
        }, reconnectInterval);
        Log.i(TAG, "Reconnect after " + reconnectInterval + " mills ...");
    }

    public void onConnectStateChange(ConnectState stateCode, Exception e) {
        Log.d(TAG, "onConnectStateChange ,state= " + stateCode.hint);
        if (!isAttach.get() || !isReconnectEnable) {
            Log.w(TAG, "reconnect is not enable  ,isAttach=" + isAttach);
            return;
        }

        switch (stateCode) {
            //主动断开或初次连接不成功,不需要重连
            case CONNECT_TIMEOUT:
            case CLOSE_SUCCESSFUL:
                detach();
                break;
            case CONNECT_SUCCESSFUL:
                reset();
                break;
            case RECONNECT_TIMEOUT:
            case CLOSE_UNEXPECTED:
                Log.i(TAG, "reconnect start : " + mCurrentTimes);
                mCurrentTimes++;
                if (mCurrentTimes > mMaxConnectionFailedTimes) {
                    connection.onMaxReconnectTimeReached(e);
                    return;
                }
                reconnectDelay();
                break;
            default:
                break;
        }
    }
}
