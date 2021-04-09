package com.skyruler.socketclient.connection.socket.remote;

import android.content.Context;

import com.skyruler.socketclient.connection.intf.IConnectOption;
import com.skyruler.socketclient.connection.intf.IStateListener;
import com.skyruler.socketclient.connection.socket.BaseSocketConnection;
import com.skyruler.socketclient.connection.socket.PacketWriter;
import com.skyruler.socketclient.connection.socket.PacketReader;
import com.skyruler.socketclient.filter.MessageFilter;

import java.io.IOException;
import java.net.Socket;

public class RemoteSocketConnection extends BaseSocketConnection {
    private static final String TAG = "LocalSocketConnection";

    private RemoteSocketConnectOption mConfig;

    private Socket mSocket;

    public RemoteSocketConnection(IConnectOption cfg) {
        mConfig = (RemoteSocketConnectOption) cfg;
    }

    @Override
    public void connect(Context mContext, IStateListener listener) {
        this.connListener = listener;

        try {
            mSocket = new Socket(mConfig.getHost(), mConfig.getPort());
            mSocket.setSoTimeout(3000);
            // Set the input stream and output stream instance variables
            mInputStream = mSocket.getInputStream();
            mOutputStream = mSocket.getOutputStream();
        } catch (IOException ioe) {
            // An exception occurred in setting up the connection. Make sure we shut down the input
            // stream and output stream and close the socket
            disconnect();
            setConnected(false);
            listener.onSocketDisconnect();
        }

        mWriter = new PacketWriter(this);
        mReader = new PacketReader(this, packetRouter);

        // Start the message writer
        mWriter.startup();
        // Start the message reader, the startup() method will block until we get a packet from server
        mReader.startup();

        // Make note of the fact that we're now connected
        setConnected(true);

        if (connListener != null) {
            connListener.onSocketConnected();
        }

        addCustomerReceiveListeners();
    }

    @Override
    public void disconnect() {
        super.disconnect();
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                // Ignore
            }
            mSocket = null;
        }
    }

    /**
     * 连接成功才添加自定义Listener
     */
    private void addCustomerReceiveListeners() {
        for (MessageFilter filter : mConfig.getWrappers().keySet()) {
            packetRouter.addRcvListener(filter, mConfig.getWrappers().get(filter));
        }
    }
}
