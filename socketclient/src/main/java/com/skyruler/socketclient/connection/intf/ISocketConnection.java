package com.skyruler.socketclient.connection.intf;

import java.io.OutputStream;

public interface ISocketConnection extends IConnection {

    void onSocketCloseUnexpected(Exception e);

    OutputStream getOutputStream();
}
