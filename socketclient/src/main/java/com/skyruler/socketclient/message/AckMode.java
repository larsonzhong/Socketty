package com.skyruler.socketclient.message;

public enum AckMode {
    /**
     * 发了就不管的模式
     */
    NON,
    /**
     * 失败重复发送模式
     */
    PACKET,
    /**
     * 可能分包发送，发送后等待确认模式
     */
    MESSAGE,
    /**
     * 超时等待消息，规定时间内收到指定消息返回true
     */
    WAIT_MESSAGE
}
