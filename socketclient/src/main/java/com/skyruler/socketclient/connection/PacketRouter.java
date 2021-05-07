package com.skyruler.socketclient.connection;

import android.util.Log;

import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageListener;
import com.skyruler.socketclient.message.IMessageStrategy;
import com.skyruler.socketclient.message.IPacket;
import com.skyruler.socketclient.message.IPacketStrategy;
import com.skyruler.socketclient.util.ArrayUtils;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PacketRouter {
    private static final String TAG = "PacketRouter";
    private final Collection<MessageCollector> mCollectors = new ConcurrentLinkedQueue<>();
    private final Map<MessageFilter, ListenerWrapper> mRcvListeners = new ConcurrentHashMap<>();
    private IPacketStrategy packetConstructor;
    private IMessageStrategy messageConstructor;

    public MessageCollector createMessageCollector(MessageFilter filter) {
        MessageCollector collector = new MessageCollector(this, filter);
        mCollectors.add(collector);
        return collector;
    }

    public void setPacketConstructor(IPacketStrategy packetConstructor) {
        this.packetConstructor = packetConstructor;
    }

    public void setMessageConstructor(IMessageStrategy messageConstructor) {
        this.messageConstructor = messageConstructor;
    }

    void removeMessageCollector(MessageCollector collector) {
        mCollectors.remove(collector);
    }

    public void addRcvListener(MessageFilter filter, IMessageListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Message listener is null.");
        }
        ListenerWrapper wrapper = new ListenerWrapper(filter, listener);
        mRcvListeners.put(filter, wrapper);
    }

    public void removeRcvListener(MessageFilter filter) {
        mRcvListeners.remove(filter);
    }

    public void onDataReceive(byte[] data) {
        IPacket packet = packetConstructor.parse(data);
        if (packet == null) {
            Log.e(TAG, "packet parser <<< error,invalid packet :" + ArrayUtils.bytesToHex(data));
            return;
        }
        IMessage message = messageConstructor.parse(packet);
        if (message == null) {
            Log.e(TAG, "error,invalid message :" + ArrayUtils.bytesToHex(data));
            return;
        }
        handlerMessage(message);
    }

    private void handlerMessage(IMessage message) {
        Log.i("larson", mCollectors.size() + "read regular packet << " + new String(message.getPackets()[0].getData()));
        for (MessageCollector collector : mCollectors) {
            collector.processMessage(message);
        }

        for (ListenerWrapper wrapper : mRcvListeners.values()) {
            wrapper.notifyListener(message);
        }
    }

    public static class ListenerWrapper {
        private final IMessageListener listener;
        private final MessageFilter filter;

        ListenerWrapper(MessageFilter filter, IMessageListener listener) {
            this.listener = listener;
            this.filter = filter;
        }

        void notifyListener(IMessage msg) {
            if (this.filter == null || this.filter.accept(msg)) {
                listener.processMessage(msg);
            }
        }
    }

    /**
     * 清理资源
     */
    public void clear() {
        mCollectors.clear();
        mRcvListeners.clear();
    }

    /*this, new PacketReader.OnCallbackListener() {
        @Override
        public void onDataReceive(byte[] data, int len) {
                .onDataReceive(data, len);
        }
    }*/
}
