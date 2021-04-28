package com.skyruler.socketclient.connection.socket;

public enum ConnectState {
    CONNECT_SUCCESSFUL(1, "连接成功"),
    CLOSE_UNEXPECTED(2, "异常断开"),
    CLOSE_SUCCESSFUL(3, "断开成功"),
    CONNECT_TIMEOUT(4, "连接超时"),
    RECONNECT_TIMEOUT(5, "重连超时"),
    RECONNECT_LIMIT(6, "达到重连次数");

    final int stateCode;
    final String hint;

    ConnectState(int stateCode, String hint) {
        this.stateCode = stateCode;
        this.hint = hint;
    }
}
