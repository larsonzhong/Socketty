package com.skyruler.socketclient.util;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Operations on arrays, primitive arrays (like {@code byte[]} and primitive wrapper arrays (like
 * {@code Byte[]}).
 * <p>
 * This class tries to handle {@code null} input gracefully. An exception will not be thrown for a
 * {@code null}  array input. Each method documents its behavior.
 *
 * @author larsonzhong (larsonzhong@163.com)
 */
public class ArrayUtils {

    /**
     * An empty immutable byte array.
     */
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    /**
     * Checks if an array of primitive bytes is null or empty.
     *
     * @param arr the array to test
     * @return {@code true} if the array is null or empty
     */
    private static boolean isEmpty(byte[] arr) {
        return arr == null || arr.length <= 0;
    }

    /**
     * davide message to send
     *
     * @param entire the large message body
     * @param len    max length
     * @return the split message list
     */
    public static List<byte[]> divide(byte[] entire, int len) {
        List<byte[]> result = new LinkedList<>();

        if (isEmpty(entire)) {
            result.add(EMPTY_BYTE_ARRAY);
            return result;
        }
        if (len <= 0 || len >= entire.length) {
            result.add(entire);
            return result;
        }

        int head = 0;
        while (head + len < entire.length) {
            result.add(Arrays.copyOfRange(entire, head, head += len));
        }
        result.add(Arrays.copyOfRange(entire, head, entire.length));

        return result;
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

    /**
     * 多个数组合并
     *
     * @param first 开始数组
     * @param rest  须要合并的数组集
     * @return 合并的数组
     */
    public static byte[] concatBytes(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    /**
     * 把bytes转为字符串的bit
     */
    public static String bytesToBitString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append("")
                    .append((byte) ((b >> 7) & 0x1))
                    .append((byte) ((b >> 6) & 0x1))
                    .append((byte) ((b >> 5) & 0x1))
                    .append((byte) ((b >> 4) & 0x1))
                    .append((byte) ((b >> 3) & 0x1))
                    .append((byte) ((b >> 2) & 0x1))
                    .append((byte) ((b >> 1) & 0x1))
                    .append((byte) ((b) & 0x1));
        }
        return stringBuilder.toString();
    }

    /**
     * byte数组转hex
     */
    public static String bytesToHex(byte[] bytes){
        String strHex;
        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < bytes.length; n++) {
            strHex = Integer.toHexString(bytes[n] & 0xFF);
            //sb.append("0x");
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex); // 每个字节由两个字符表示，位数不够，高位补0
            sb.append(", ");
        }
        return sb.toString().trim();
    }

}
