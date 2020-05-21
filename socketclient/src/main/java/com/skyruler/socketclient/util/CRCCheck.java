package com.skyruler.socketclient.util;

/**
 * CRC校验类，摘抄自网络，不保证结果准确性
 */
public class CRCCheck {
    private CRCCheck() {
    }

    public static byte checkSumCrc8(byte[] data, int len) {
        byte[] ch = new byte[8];
        byte crc = -1;

        for (int i = 0; i < len; ++i) {
            byte ch1 = data[i];

            for (int j = 0; j < 8; ++j) {
                ch[j] = (byte) (ch1 & 1);
                ch1 = (byte) (ch1 >> 1);
            }

            for (int k = 0; k < 8; ++k) {
                ch[7 - k] = (byte) (ch[7 - k] << 7);
                if (((crc ^ ch[7 - k]) & -128) != 0) {
                    crc = (byte) (crc << 1 ^ 29);
                } else {
                    crc = (byte) (crc << 1);
                }
            }
        }

        crc = (byte) (crc ^ 255);
        return crc;
    }

}