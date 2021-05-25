package com.skyruler.socketclient;

import android.content.Context;

import com.skyruler.socketclient.connection.intf.IConnectOption;
import com.skyruler.socketclient.connection.intf.IStateListener;
import com.skyruler.socketclient.exception.ConnectionException;
import com.skyruler.socketclient.exception.UnFormatMessageException;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.IMessageListener;
import com.skyruler.socketclient.message.IWrappedMessage;

public interface ISocketClient {
    /**
     * 执行初始化，传入上下文和连接监听
     *
     * @param context  上下文，非蓝牙连接不需要
     * @param listener 连接监听
     */
    void setup(Context context, IStateListener listener);

    /**
     * 开始连接Socket/蓝牙/LocalSocket
     *
     * @param option 连接选项
     */
    void connect(IConnectOption option);

    /**
     * 断开所有连接
     */
    void disConnect();

    /**
     * 销毁连接和相关资源
     */
    void onDestroy();

    /**
     * 判断连接是否建立
     *
     * @return 连接建立
     */
    boolean isConnected();

    /**
     * 发送一条消息，返回发送结果
     *
     * @param msgDataBean 需要发送的消息
     * @return 是否发送成功
     * @throws ConnectionException      连接异常抛出
     * @throws UnFormatMessageException 消息组装错误抛出
     */
    boolean sendMessage(IWrappedMessage msgDataBean) throws ConnectionException, UnFormatMessageException;

    /**
     * 添加消息监听器
     *
     * @param listener 监听器
     * @param filter   过滤器
     */
    void addMessageListener(IMessageListener listener, MessageFilter filter);

    /**
     * 移除消息监听
     *
     * @param filter 过滤器
     */
    void removeMessageListener(MessageFilter filter);
}
