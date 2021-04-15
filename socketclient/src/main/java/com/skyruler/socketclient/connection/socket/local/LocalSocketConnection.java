package com.skyruler.socketclient.connection.socket.local;

import android.content.Context;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;

import com.skyruler.socketclient.connection.intf.IConnectOption;
import com.skyruler.socketclient.connection.intf.IStateListener;
import com.skyruler.socketclient.connection.socket.BaseSocketConnection;
import com.skyruler.socketclient.connection.socket.PacketReader;
import com.skyruler.socketclient.connection.socket.PacketWriter;
import com.skyruler.socketclient.connection.socket.conf.SocketConnectOption;
import com.skyruler.socketclient.filter.MessageFilter;

import java.io.IOException;


/**
 * Creates a socket connection to  skyRuler server.
 * <p>
 * SocketConnection can be reused between connections. This means that a SocketConnection may be connected,
 * disconnected, and then connected again. Listeners of the SocketConnection will be retained across
 * connections.
 * <p>
 * If a connected SocketConnection gets disconnected abruptly and automatic reconnection is enabled
 * ({@link SocketConnectOption#isReconnectAllowed()} , the default), then it will try to
 * reconnect again. To stop the reconnection process, . Once stopped you
 * can use {@link #connect(Context, IStateListener)} to manually connect to the server.
 *
 * @author Larsonzhong (larsonzhong@163.com)
 * @since 2017-12-07 11:14
 */
public class LocalSocketConnection extends BaseSocketConnection {
    private static final String TAG = "LocalSocketConnection";

    /**
     * connect to server config
     */
    private final LocalSocketConnectOption mConfig;
    /**
     * The socket which is used for this connection
     */
    private LocalSocket mSocket;

    /**
     * Creates a new connection using the specified connection configuration.
     * <p>
     * Note that SocketConnection constructors do not establish a connection to the server and you must call
     * {@link #connect(Context, IStateListener)}.
     *
     * @param cfg the configuration which is used to establish the connection
     */
    public LocalSocketConnection(IConnectOption cfg) {
        mConfig = (LocalSocketConnectOption) cfg;
    }


    /**
     * Establishes a connection to the JT/T808 server and performs an automatic login only if the
     * previous connection state was logged (authenticated). It basically creates and maintains a
     * connection to the server.
     * <p>
     * Listeners will be preserved from a previous connection.
     */
    @Override
    public void connect(Context context, IStateListener listener) {
        this.connListener = listener;
        mSocket = new LocalSocket();

        try {
            //int timeout =  mConfig.getSkSocketOption().getConnectTimeoutSecond()*1000;
            mSocket.connect(new LocalSocketAddress(mConfig.getSocketName()));
            mSocket.setSoTimeout(3000);
            // Set the input stream and output stream instance variables
            mInputStream = mSocket.getInputStream();
            mOutputStream = mSocket.getOutputStream();
        } catch (IOException ioe) {
            // An exception occurred in setting up the connection. Make sure we shut down the input
            // stream and output stream and close the socket
            disconnect();
            setConnected(false);
            listener.onDeviceDisconnect(null);
        }

        mWriter = new PacketWriter(this);
        mReader = new PacketReader(this, packetRouter);

        // Start the message writer
        mWriter.startup();
        // Start the message reader, the startup() method will block until we get a packet from server
        mReader.startup();

        mWriter.keepAlive(mConfig.getSkSocketOption().getPulseFrequency(), mConfig.getHeartBeat());

        // Make note of the fact that we're now connected
        setConnected(true);

        if (connListener != null) {
            connListener.onDeviceConnect(null);
        }

        addCustomerReceiveListeners();
    }

    /**
     * 登录成功才添加自定义Listener
     */
    private void addCustomerReceiveListeners() {
        for (MessageFilter filter : mConfig.getWrappers().keySet()) {
            packetRouter.addRcvListener(filter, mConfig.getWrappers().get(filter));
        }
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

}
