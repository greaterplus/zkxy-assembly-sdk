package com.zkxy.assembly.sdk.utils;

@SuppressWarnings("all")
public class ByteUtil {

    /**
     * 把一个16进制字符串转换成byte
     */
    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    /**
     * 把16进制字符串转换成byte数组
     */
    public static byte[] hexToByteArray(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            //奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            //偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }


    /**
     * byte数组转换成为字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 拼接各个byte数组为一个大的byte数组
     */
    public static byte[] joinByteArr(byte[]... bytes) {
        int preLength = 0;
        int totalLength = 0;
        for (byte[] aByte : bytes) {
            totalLength += aByte.length;
        }
        if (totalLength == 0) {
            return new byte[]{};
        }
        byte[] bigByte = new byte[totalLength];
        for (byte[] aByte : bytes) {
            System.arraycopy(aByte, 0, bigByte, preLength, aByte.length);
            preLength += aByte.length;
        }
        return bigByte;
    }

    /**
     * 切换大小端
     */
    public static byte[] changeBytes(byte[] a) {
        byte[] b = new byte[a.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = a[b.length - i - 1];
        }
        return b;
    }

    /**
     * 正常长度
     * 长度为4的字节数组转换成int
     */
    public static int bytesToInt(byte[] bytes) {
        return bytes[0] << 24 | (bytes[1] & 0xff) << 16 | (bytes[2] & 0xff) << 8 | (bytes[3] & 0xff);
    }

    /**
     * 正常长度
     * 长度为3的字节数组转换成int
     */
    public static int threeBytesToInt(byte[] bytes) {
        return bytes[0] << 16 | (bytes[1] & 0xff) << 8 | (bytes[2] & 0xff);
    }

    /**
     * 长度为2的字节数组转换成int
     */
    public static int twoBytesToInt(byte[] bytes) {
        return bytes[0] << 8 | (bytes[1] & 0xff);
    }

    /**
     * 长度为1的字节数组转换成int
     */
    public static int oneBytesToInt(byte[] bytes) {
        return (bytes[0] & 0xff);
    }
}
