package com.skyruler.socketclient.message;

import android.text.TextUtils;

import com.skyruler.socketclient.util.CrcUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Rony
 * @date 2018/8/20
 */

public class MessageSnBuilder {
    private static Map<String, MessageSnBuilder> instance = new HashMap<>();
    private long sn;
    private short mClientKey;
    private int autoResetNo;

    private MessageSnBuilder() {
        sn = 0;
        mClientKey = 0;
    }

    public static MessageSnBuilder getInstance(String id) {
        if (!instance.containsKey(id)) {
            instance.put(id, new MessageSnBuilder());
        }
        return instance.get(id);
    }

    /**
     * 获取命令序列号，如果有设置截断，则到达接断序号从0开始
     * 否则一直自增
     *
     * @return 命令序列号
     */
    public long getNextSn() {
        if (autoResetNo > 0) {
            sn = (sn + 1) % autoResetNo;
            sn = (sn == 0 ? 1 : sn);
        } else {
            sn = sn + 1;
        }
        return sn;
    }

    /**
     * 遇到一些情况需要重置SN
     */
    public void resetSn() {
        sn = 0;
    }

    /**
     * 获取客户端唯一标识，通过Imei+deviceid等字符进行CRC16计算得到一个唯一标识
     *
     * @return 客户端唯一标识
     */
    short getClientKey() throws Exception {
        if (mClientKey == 0) {
            throw new Exception("not client key!");
        }
        return mClientKey;
    }

    /**
     * 生成唯一标识，通过Imei+deviceid等字符进行CRC16计算
     *
     * @param clientKey Imei+deviceid+...
     * @throws Exception 返回异常
     */
    public MessageSnBuilder setClientKey(String clientKey) throws Exception {
        if (TextUtils.isEmpty(clientKey)) {
            throw new Exception("Client key is null!");
        }
        mClientKey = (short) CrcUtils.crc16Ccitt(CrcUtils.CRC16_CCITT, clientKey.getBytes());
        return this;
    }

    public void autoResetNum(int resetNo) {
        this.autoResetNo = resetNo;
    }
}
