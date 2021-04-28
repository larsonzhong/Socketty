package com.skyruler.socketclient.connection.socket.conf;

import com.skyruler.socketclient.exception.ConnectionException;
import com.skyruler.socketclient.message.MessageSnBuilder;

import java.nio.ByteOrder;


/**
 * ......................-~~~~~~~~~-._       _.-~~~~~~~~~-.
 * ............... _ _.'              ~.   .~              `.__
 * ..............'//     NO           \./      BUG         \\`.
 * ............'//                     |                     \\`.
 * ..........'// .-~"""""""~~~~-._     |     _,-~~~~"""""""~-. \\`.
 * ........'//.-"                 `-.  |  .-'                 "-.\\`.
 * ......'//______.============-..   \ | /   ..-============.______\\`.
 * ....'______________________________\|/______________________________`.
 * ..larsonzhong@163.com      created in 2018/8/13     @author : larsonzhong
 * 可选配置，如果没有配置，使用默认值
 */
public class SocketConnectOption {

    /**
     * Socket默认连接超时时间，当没有配置时使用该值
     */
    public static final int DEFAULT_CONNECT_TIMEOUT = 5000;

    /**
     * Socket默认读取超时时间，当没有配置时使用该值
     */
    public static final int DEFAULT_READ_TIMEOUT = 3000;

    /**
     * Socket默认心跳间隔，当没有配置时使用该值
     */
    public static final int DEFAULT_HEART_INTERVAL = 30000;

    /**
     * Socket默认最小重连间隔时间，当没有配置时使用该值
     */
    public static final int DEFAULT_RECONNECT_INTERVAL = 5000;

    /**
     * Socket默认最大重连次数，当没有配置时使用该值
     */
    public static final int DEFAULT_RECONNECT_MAX_ATTEMPT = 5;

    /**
     * 写入Socket管道中给服务器的字节序
     */
    private final ByteOrder mWriteOrder;

    /**
     * 从Socket管道中读取字节序时的字节序
     */
    private final ByteOrder mReadByteOrder;

    /**
     * 脉搏频率单位是毫秒
     */
    private final long mPulseFrequency;

    /**
     * 重连间隔，单位是毫秒
     */
    private final long reconnectInterval;

    /**
     * 脉搏丢失次数<br>
     * 大于或等于丢失次数时将断开该通道的连接<br>
     * 抛出{@link ConnectionException}
     */
    private final int mReconnectMaxAttemptTimes;

    /**
     * 连接超时时间(毫秒)
     * 设置连接超时可以防止Socket阻塞无法退出
     */
    private final int mConnectTimeout;

    /**
     * 读取超时时间(毫秒)
     * 设置读取超时可以防止Socket阻塞无法退出
     */
    private final int mReadTimeout;

    /**
     * 是否容许重连
     */
    private final boolean isReconnectAllowed;

    private SocketConnectOption(Builder okOptions) {
        mPulseFrequency = okOptions.mPulseFrequency;
        mConnectTimeout = okOptions.mConnectTimeout;
        mReadTimeout = okOptions.mReadTimeout;
        mWriteOrder = okOptions.mWriteOrder;
        mReadByteOrder = okOptions.mReadByteOrder;
        mReconnectMaxAttemptTimes = okOptions.mReconnectMaxAttemptTimes;
        isReconnectAllowed = okOptions.isReconnectAllowed;
        reconnectInterval = okOptions.mReconnectInterval;
    }


    public boolean isReconnectAllowed() {
        return isReconnectAllowed;
    }

    public ByteOrder getWriteOrder() {
        return mWriteOrder;
    }

    public ByteOrder getReadByteOrder() {
        return mReadByteOrder;
    }

    public long getPulseFrequency() {
        return mPulseFrequency > 0 ? mPulseFrequency : DEFAULT_HEART_INTERVAL;
    }

    public long getReconnectInterval() {
        return reconnectInterval > 0 ? reconnectInterval : DEFAULT_RECONNECT_INTERVAL;
    }

    public int getReconnectMaxAttemptTimes() {
        return mReconnectMaxAttemptTimes > 0 ? mReconnectMaxAttemptTimes : DEFAULT_RECONNECT_MAX_ATTEMPT;
    }

    public int getConnectTimeout() {
        return mConnectTimeout > 0 ? mConnectTimeout : DEFAULT_CONNECT_TIMEOUT;
    }

    public int getReadTimeout() {
        return mReadTimeout > 0 ? mReadTimeout : DEFAULT_READ_TIMEOUT;
    }

    public static class Builder {
        /**
         * 脉搏频率单位是毫秒
         */
        private long mPulseFrequency;
        /**
         * 重连间隔单位是毫秒
         */
        private long mReconnectInterval;
        /**
         * 连接超时时间(毫秒)
         */
        private int mConnectTimeout;
        /**
         * 读取超时时间(毫秒)
         */
        private int mReadTimeout;
        /**
         * 写入Socket管道中给服务器的字节序
         */
        private ByteOrder mWriteOrder;
        /**
         * 从Socket管道中读取字节序时的字节序
         */
        private ByteOrder mReadByteOrder;
        /**
         * 最大重连次数，当大于该次数则停止重连
         * 大于或等于丢失次数时将断开该通道的连接<br>
         * 抛出{@link ConnectionException}
         */
        private int mReconnectMaxAttemptTimes;
        /**
         * 是否容许自动重连
         */
        private boolean isReconnectAllowed;

        public Builder() {

        }

        public Builder(String clientID) throws Exception {
            MessageSnBuilder.getInstance().setClientKey(clientID).autoResetNum(10000);
        }

        /**
         * 设置脉搏间隔频率<br>
         * 单位是毫秒<br>
         *
         * @param pulseFrequency 间隔毫秒数
         */

        public Builder setPulseFrequency(long pulseFrequency) {
            mPulseFrequency = pulseFrequency;
            return this;
        }

        /**
         * 设置重连间隔<br>
         *
         * @param interval 间隔毫秒数
         */
        public Builder setReconnectInterval(long interval) {
            mReconnectInterval = interval;
            return this;
        }


        /**
         * 脉搏丢失次数<br>
         * 大于或等于丢失次数时将断开该通道的连接<br>
         * 抛出{@link ConnectionException}<br>
         * 默认是5次
         *
         * @param pulseFeedLoseTimes 丢失心跳ACK的次数,例如5,当丢失3次时,自动断开.
         */
        public Builder setReconnectMaxAttemptTimes(int pulseFeedLoseTimes) {
            mReconnectMaxAttemptTimes = pulseFeedLoseTimes;
            return this;
        }

        /**
         * 设置输出Socket管道中给服务器的字节序<br>
         * 默认是:大端字节序<br>
         *
         * @param writeOrder {@link ByteOrder} 字节序
         * @deprecated 请使用 {@link Builder#setWriteByteOrder(ByteOrder)}
         */
        public Builder setWriteOrder(ByteOrder writeOrder) {
            return setWriteByteOrder(writeOrder);
        }


        /**
         * 设置输出Socket管道中给服务器的字节序<br>
         * 默认是:大端字节序<br>
         *
         * @param writeOrder {@link ByteOrder} 字节序
         */
        public Builder setWriteByteOrder(ByteOrder writeOrder) {
            mWriteOrder = writeOrder;
            return this;
        }

        /**
         * 设置输入Socket管道中读取时的字节序<br>
         * 默认是:大端字节序<br>
         *
         * @param readByteOrder {@link ByteOrder} 字节序
         */
        public Builder setReadByteOrder(ByteOrder readByteOrder) {
            mReadByteOrder = readByteOrder;
            return this;
        }

        /**
         * 设置Socket连接超时时间
         *
         * @param socketTimeout 注意单位是毫秒
         */
        public Builder setConnectTimeout(int socketTimeout) {
            mConnectTimeout = socketTimeout;
            return this;
        }

        /**
         * 设置Socket读取超时时间
         *
         * @param socketTimeout 注意单位是毫秒
         */
        public Builder setReadTimeout(int socketTimeout) {
            mConnectTimeout = socketTimeout;
            return this;
        }

        /**
         * 是否容许自动重连
         *
         * @param isAllow 是否容许
         */
        public Builder setReconnectAllowed(boolean isAllow) {
            isReconnectAllowed = isAllow;
            return this;
        }

        public SocketConnectOption build() {
            return new SocketConnectOption(this);
        }
    }
}
