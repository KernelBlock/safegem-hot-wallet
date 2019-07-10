package com.bankledger.safegem.utils;

/**
 * Safe公用方法
 * @author zhangmiao
 */
public class SafeUtils {

    //解析预留字段
    private static boolean isSafeReserve(byte[] vReserve) {
        int cursor = 0;
        int length = 4;
        String safeFlag = new String(readBytes(vReserve, cursor, length));
        if (safeFlag.equals("safe") && vReserve.length > 4) {
            cursor = length + 34;
            long appCommand = readUint32(vReserve, cursor);
            if (appCommand == 300) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public static byte[] readBytes(byte[] bytes, int cursor, int length) {
        byte[] b = new byte[length];
        System.arraycopy(bytes, cursor, b, 0, length);
        return b;
    }

    public static long readUint32(byte[] bytes, int offset) {
        return (bytes[offset] & 0xffl) |
                ((bytes[offset + 1] & 0xffl) << 8) |
                ((bytes[offset + 2] & 0xffl) << 16) |
                ((bytes[offset + 3] & 0xffl) << 24);
    }

}
