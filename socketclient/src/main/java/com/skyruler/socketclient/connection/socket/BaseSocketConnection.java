package com.skyruler.socketclient.connection.socket;

import android.util.Log;

import com.skyruler.socketclient.connection.MessageCollector;
import com.skyruler.socketclient.connection.PacketRouter;
import com.skyruler.socketclient.connection.intf.ISocketConnection;
import com.skyruler.socketclient.connection.intf.IStateListener;
import com.skyruler.socketclient.connection.socket.conf.SocketConnectOption;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.filter.MessageIdFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.skyruler.socketclient.connection.socket.ConnectState.RECONNECT_LIMIT;

public abstract class BaseSocketConnection implements ISocketConnection {

    private static final String TAG = "BaseSocketConnection";

    private boolean mConnected = false;

    protected InputStream mInputStream;
    protected OutputStream mOutputStream;
    protected PacketReader mReader;
    protected PacketWriter mWriter;
    protected IStateListener connListener;

    private final ReconnectManager mReconnectManager;
    /**
     * The packet distributor, because the control end will receive the packets sent by
     * different clients, these packets need to be sub-packaged and routed.
     * In order to avoid code bloat, a new type is opened to handle packet routing.
     */
    protected PacketRouter packetRouter;

    public BaseSocketConnection(SocketConnectOption connectOption) {
        this.packetRouter = new PacketRouter();
        this.mReconnectManager = new ReconnectManager(connectOption);

        mReconnectManager.attach(this);
        Log.d(TAG, "BaseSocketConnection construct end .");
    }

    protected abstract void performConnect(boolean reconnect);

    public void releaseSocketResource() {
        if (mWriter != null) {
            mWriter.shutdown();
            mWriter = null;
        }
        if (mReader != null) {
            mReader.shutdown();
            mReader = null;
        }
        if (mInputStream != null) {
            try {
                mInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mInputStream = null;
        }
        if (mOutputStream != null) {
            try {
                mOutputStream.close();
            } catch (IOException e) {
                // Ignore
            }
            mOutputStream = null;
        }
    }

    protected void onConnectStateChange(ConnectState state, Exception e) {
        if (connListener == null) {
            return;
        }
        if (state == ConnectState.CLOSE_UNEXPECTED) {
            setConnected(false);
            connListener.onDisconnect(e);
        } else if (state == ConnectState.CLOSE_SUCCESSFUL) {
            connListener.onDisconnect(null);
            setConnected(false);
        } else if (state == ConnectState.CONNECT_SUCCESSFUL) {
            // Make note of the fact that we're now connected
            setConnected(true);
            connListener.onConnected(null);
        } else if (state == ConnectState.CONNECT_TIMEOUT) {
            setConnected(false);
            connListener.onConnectFailed("Connect Timeout");
        } else if (state == ConnectState.RECONNECT_TIMEOUT) {
            setConnected(false);
        } else if (state == ConnectState.RECONNECT_LIMIT) {
            setConnected(false);
            connListener.onConnectFailed("达到最大重连次数，放弃重连");
            mReconnectManager.detach();
            return;
        }

        // Abnormal disconnection needs to notify the reconnection manager
        mReconnectManager.onConnectStateChange(state, e);
    }

    @Override
    public void onDestroy() {
        mReconnectManager.detach();
        releaseSocketResource();
        if (packetRouter != null) {
            packetRouter.clear();
            packetRouter = null;
        }
    }

    public InputStream getInputStream() {
        return mInputStream;
    }

    public OutputStream getOutputStream() {
        return mOutputStream;
    }

    public boolean isConnected() {
        return mConnected;
    }

    protected void setConnected(boolean isConnect) {
        this.mConnected = isConnect;
    }


    @Override
    public void onSocketCloseUnexpected(Exception e) {
        onConnectStateChange(ConnectState.CLOSE_UNEXPECTED, e);
        // Abnormal disconnection, supposedly should stop the connection
        releaseSocketResource();
    }

    public void onMaxReconnectTimeReached(Exception e) {
        onConnectStateChange(RECONNECT_LIMIT, e);
        // Abnormal disconnection, supposedly should stop the connection
        releaseSocketResource();
    }

    /**
     * Write the message through the writer
     * write message to server
     *
     * @param msg Message to send
     */
    @Override
    public void sendMessage(IMessage msg) {
        if (!isConnected()) {
            Log.e(TAG, "Not connected to server...");
            return;
        }
        if (msg == null) {
            Log.e(TAG, "Message is null.");
            return;
        }
        mWriter.sendMessage(msg);
    }


    public IMessage sendSyncMessage(final IMessage msg, long timeOut) {
        if (!isConnected()) {
            Log.e(TAG, "Not connected to server...");
            return null;
        }
        if (msg == null) {
            Log.e(TAG, "Message is null.");
            return null;
        }

        mWriter.sendMessage(msg);

        //Create a filter to filter messages that do not belong to the ClientID
        MessageFilter idFilter = new MessageIdFilter((byte) msg.getMsgId());
        MessageCollector collector = packetRouter.createMessageCollector(idFilter);
        IMessage retMsg = collector.nextResult(timeOut);
        collector.cancel();
        return retMsg;
    }

    @Override
    public IMessage sendSyncMessage(final IMessage msg, MessageFilter filter, long timeOut) {
        if (!isConnected()) {
            Log.e(TAG, "Not connected to server...");
            return null;
        }
        if (msg == null) {
            Log.e(TAG, "Message is null.");
            return null;
        }
        mWriter.sendMessage(msg);

        MessageCollector collector = packetRouter.createMessageCollector(filter);
        IMessage retMsg = collector.nextResult(timeOut);
        collector.cancel();
        return retMsg;
    }

    public void addMsgListener(IMessageListener listener, MessageFilter filter) {
        packetRouter.addRcvListener(filter, listener);
    }

    public void removeMsgListener(MessageFilter filter) {
        packetRouter.removeRcvListener(filter);
    }

    /**
     * Add a listener waiting for the message sent by the server
     *
     * @param filter  filter
     * @param timeOut timeout
     * @return msg from server
     */
    public IMessage waitForMessage(MessageFilter filter, long timeOut) {
        if (null == packetRouter) {
            return null;
        }
        MessageCollector collector = packetRouter.createMessageCollector(filter);
        IMessage retMsg = collector.nextResult(timeOut);
        //if (retMsg != null) {
        collector.cancel();
        //}
        return retMsg;
    }

}
