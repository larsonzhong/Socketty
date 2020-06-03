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

class PacketRouter {
    private static final String TAG = "PacketRouter";
    private final Collection<MessageCollector> mCollectors = new ConcurrentLinkedQueue<>();
    private final Map<MessageFilter, ListenerWrapper> mRcvListeners = new ConcurrentHashMap<>();
    private IPacketStrategy packetConstructor;
    private IMessageStrategy messageConstructor;

    MessageCollector createMessageCollector(MessageFilter filter) {
        MessageCollector collector = new MessageCollector(this, filter);
        mCollectors.add(collector);
        return collector;
    }

    void setPacketConstructor(IPacketStrategy packetConstructor) {
        this.packetConstructor = packetConstructor;
    }

    void setMessageConstructor(IMessageStrategy messageConstructor) {
        this.messageConstructor = messageConstructor;
    }

    void removeMessageCollector(MessageCollector collector) {
        mCollectors.remove(collector);
    }

    void addRcvListener(MessageFilter filter, IMessageListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Message listener is null.");
        }
        ListenerWrapper wrapper = new ListenerWrapper(filter, listener);
        mRcvListeners.put(filter, wrapper);
    }

    void removeRcvListener(MessageFilter filter) {
        mRcvListeners.remove(filter);
    }

    void onDataReceive(byte[] data) {
        Log.d(TAG,  "read packet>>>" + ArrayUtils.bytesToHex(data));
        IPacket packet = packetConstructor.parse(data);
        if (packet == null) {
            Log.e(TAG, "error,invalid packet :" + ArrayUtils.bytesToHex(data));
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
        for (MessageCollector collector : mCollectors) {
            collector.processMessage(message);
        }

        for (ListenerWrapper wrapper : mRcvListeners.values()) {
            wrapper.notifyListener(message);
        }
    }

    public static class ListenerWrapper {
        private IMessageListener listener;
        private MessageFilter filter;

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
}
