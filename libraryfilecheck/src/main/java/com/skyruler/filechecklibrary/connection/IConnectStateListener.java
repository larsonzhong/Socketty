package com.skyruler.filechecklibrary.connection;

import com.skyruler.filechecklibrary.command.result.Session;

public interface IConnectStateListener {

    void onConnect();

    void onConnectFailed(String reason);

    void onDisconnect();

    void onLogged(Session session);

    void onLoginTimeout();

    void onLogout();
}
