package com.skyruler.filechecklibrary.connection;

import com.skyruler.filechecklibrary.command.result.Session;

public interface IConnectStateListener {

    void onConnect(String host,boolean reconnect);

    void onConnectFailed(String host, String reason);

    void onDisconnect(String host);

    void onLogged(String host, Session session);

    void onLoginTimeout(String host);

    void onLogout(String host);
}
