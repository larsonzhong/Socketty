package com.skyruler.socketclient.connection.socket;

import android.util.Log;

import com.skyruler.socketclient.connection.MessageCollector;
import com.skyruler.socketclient.connection.PacketRouter;
import com.skyruler.socketclient.connection.intf.ISocketConnection;
import com.skyruler.socketclient.connection.intf.IStateListener;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.filter.MessageIdFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class BaseSocketConnection implements ISocketConnection {

    private static final String TAG = "BaseSocketConnection";

    private boolean mConnected = false;

    protected InputStream mInputStream;
    protected OutputStream mOutputStream;
    protected SocketPacketReader mReader;
    protected PacketWriter mWriter;
    protected IStateListener connListener;

    /**
     * 包分发器，因为控制端会收到不同的客户端发过来的包，需要对这些包进行分包路由，
     * 为了避免代码臃肿，新开一类专门用来处理packet路由
     */
    protected PacketRouter packetRouter;

    public BaseSocketConnection() {
        packetRouter = new PacketRouter();
        Log.d(TAG, "BaseSocketConnection construct end .");
    }


    @Override
    public void disconnect() {
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

        if (packetRouter != null) {
            packetRouter.clear();
            packetRouter = null;
        }
        connListener = null;
    }

    @Override
    public void onDestroy() {
        disconnect();
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
        if (connListener != null) {
            connListener.onDeviceDisconnect(e);
        }
    }

    /**
     * 通过writer将消息写出去
     * write message to server
     *
     * @param msg 要发送的消息
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

        //创造一个filter过滤不属于该ClientID的消息
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
     * 添加监听等待服务端发过来的消息
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
