package com.skyruler.socketclient.connection.intf;

import com.skyruler.socketclient.exception.ConnectionException;
import com.skyruler.socketclient.exception.UnFormatMessageException;
import com.skyruler.socketclient.filter.MessageFilter;
import com.skyruler.socketclient.message.IMessage;
import com.skyruler.socketclient.message.IMessageListener;

public interface IConnectionManager {

    /**
     * 开始连接
     *
     * @param iConnectOption 连接选项
     */
    void connect(IConnectOption iConnectOption);

    /**
     * 是否已连接。true：连接，false：断开
     *
     * @return 连接状态
     */
    boolean isConnected();

    /**
     * 断开连接
     */
    void disConnect();

    /**
     * 销毁连接和回收相关资源
     */
    void onDestroy();

    /**
     * 发送消息
     *
     * @param msgDataBean 封装好的消息
     */
    void sendMessage(IMessage msgDataBean);

    /**
     * 发送同步消息，并返回服务端回复的消息，如果服务端回复超时则返回null
     *
     * @param msgDataBean 封装好的同步消息
     * @param filter      消息过滤器，用来拦截服务器返回的消息
     * @param timeout     拦截超时时间，超过改时间返回null
     * @return 服务器回复的消息
     * @throws ConnectionException      连接异常
     * @throws UnFormatMessageException 消息组装格式错误
     */
    IMessage sendSyncMessage(IMessage msgDataBean, MessageFilter filter, long timeout) throws ConnectionException, UnFormatMessageException;

    /**
     * 在调用后的给定时间里等待服务器返回的消息，如果服务器未返回则返回null
     *
     * @param filter  消息过滤器
     * @param timeout 超时时间
     * @return 服务器发送的消息
     * @throws UnFormatMessageException 消息组装错误
     * @throws ConnectionException      连接异常
     */
    IMessage waitForMessage(MessageFilter filter, long timeout) throws UnFormatMessageException, ConnectionException;

    /**
     * 添加消息监听，可以添加多个监听
     *
     * @param listener 监听器
     * @param filter   消息过滤器
     */
    void addMessageListener(IMessageListener listener, MessageFilter filter);

    /**
     * 移除消息监听
     *
     * @param filter 过滤器
     */
    void removeMessageListener(MessageFilter filter);

}
