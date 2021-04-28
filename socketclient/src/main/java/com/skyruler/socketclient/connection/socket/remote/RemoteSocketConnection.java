package com.skyruler.socketclient.connection.socket.remote;

import android.content.Context;
import android.util.Log;

import com.skyruler.socketclient.connection.intf.IConnectOption;
import com.skyruler.socketclient.connection.intf.IStateListener;
import com.skyruler.socketclient.connection.socket.BaseSocketConnection;
import com.skyruler.socketclient.connection.socket.ConnectState;
import com.skyruler.socketclient.connection.socket.PacketReader;
import com.skyruler.socketclient.connection.socket.PacketWriter;
import com.skyruler.socketclient.connection.socket.conf.SocketConnectOption;
import com.skyruler.socketclient.filter.MessageFilter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class RemoteSocketConnection extends BaseSocketConnection {
    private static final String TAG = "LocalSocketConnection";

    private final RemoteSocketConnectOption mConfig;

    private Socket mSocket;

    public RemoteSocketConnection(IConnectOption cfg) {
        super(((RemoteSocketConnectOption) cfg).getSkSocketOption());
        mConfig = (RemoteSocketConnectOption) cfg;
    }

    @Override
    public void connect(Context mContext, IStateListener listener) {
        this.connListener = listener;
        packetRouter.setPacketConstructor(mConfig.getPacketConstructor());
        packetRouter.setMessageConstructor(mConfig.getMessageConstructor());

        performConnect(false);

        addCustomerReceiveListeners();
    }

    @Override
    public void performConnect(boolean reconnect) {
        try {
            SocketConnectOption skSocketOption = mConfig.getSkSocketOption();
            int connectTimeout = skSocketOption == null ? SocketConnectOption.DEFAULT_CONNECT_TIMEOUT
                    : skSocketOption.getConnectTimeout();
            int readTimeout = skSocketOption == null ? SocketConnectOption.DEFAULT_READ_TIMEOUT :
                    skSocketOption.getReadTimeout();

            InetAddress addr = InetAddress.getByName(mConfig.getHost());
            SocketAddress sockAddr = new InetSocketAddress(addr, mConfig.getPort());
            // Creates an unconnected socket
            mSocket = new Socket();
            // Connects this socket to the server with a specified timeout value If timeout occurs
            mSocket.connect(sockAddr, connectTimeout);

            // Set Socket read timeout
            mSocket.setSoTimeout(readTimeout);
            mSocket.setKeepAlive(true);
            // Set the input stream and output stream instance variables
            mInputStream = mSocket.getInputStream();
            mOutputStream = mSocket.getOutputStream();
        } catch (Exception ioe) {
            // An exception occurred in setting up the connection. Make sure we shut down the input
            // stream and output stream and close the socket
            releaseSocketResource();
            onConnectStateChange(reconnect ? ConnectState.RECONNECT_TIMEOUT : ConnectState.CONNECT_TIMEOUT, ioe);
            return;
        }

        mWriter = new PacketWriter(this);
        mReader = new PacketReader(this, packetRouter);

        // Start the message writer
        mWriter.startup();
        // Start the message reader, the startup() method will block until we get a packet from server
        mReader.startup();
        // start heart beat thread
        mWriter.keepAlive(mConfig);

        onConnectStateChange(ConnectState.CONNECT_SUCCESSFUL, null);
    }

    @Override
    public void disconnect() {
        releaseSocketResource();
        onConnectStateChange(ConnectState.CLOSE_SUCCESSFUL, null);
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
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
