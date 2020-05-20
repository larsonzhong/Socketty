package com.skyruler.xml.model;

/**
 * @author: Rony
 * @email: luojun@skyruler.cn
 * @date: Created 2020/5/20 8:25
 */
public interface ByteSerializable {
    /**
     * 对象转为Bytes
     * @return 返回序列化字节
     */
    public byte[] toBytes();
}
