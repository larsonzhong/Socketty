package com.skyruler.glonavin.xml.parser;

import java.io.InputStream;

/**
 * @author: Rony
 * @email: luojun@skyruler.cn
 * @date: Created 2020/5/19 15:11
 */
public interface XmlParser<E> {
    /**
     * 解析输入流 得到Book对象集合
     * @param is 输入流
     * @return 解析集合
     * @throws Exception 异常
     */
    E parse(InputStream is) throws Exception;

    /**
     * 序列化E对象集合 得到XML形式的字符串
     * @param e E对象集合
     * @return xml形式字符串
     * @throws Exception 异常
     */
    String serialize(E e) throws Exception;
}
