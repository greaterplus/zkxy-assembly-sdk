package com.zkxy.assembly.sdk.utils;

import cn.hutool.core.util.HexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description : 组包相关工具类
 * @Author : ChangHao
 * @Date: 2020-07-28 15:27
 */
@SuppressWarnings("all")
public class AssemblyUtils {

    private static final Logger LOG = LoggerFactory.getLogger(AssemblyUtils.class);

    public static final String COLLECT_TIME_KEY = "collectTime";

    public static final String WRAP = "wrap";

    /**
     * 基本处理数据，解析出版本 数据类型 采集时间
     */
    public static Map<String, Object> baseResolve(String str) {
        byte[] bytes = HexUtil.decodeHex(str);
        ByteBuffer wrap = ByteBuffer.wrap(new byte[bytes.length]);
        wrap.order(ByteOrder.LITTLE_ENDIAN);
        wrap.put(bytes);
        wrap.flip();
        short commandVersion = wrap.getShort();
        short dataType = wrap.getShort();
        int collectTime = wrap.getInt();
        Map<String, Object> resolveMap = new HashMap<>();
        resolveMap.put(COLLECT_TIME_KEY, String.valueOf(collectTime));
        resolveMap.put(WRAP, wrap);
        return resolveMap;
    }

    /**
     * 解析出单次采集长度和本次采集个数
     */
    public static int resolveCollectLengthAndCount(ByteBuffer wrap) {
        // 数据描述 占用四个字节 首字节-单次数据长度 后面三个字节-一共有多少个数据
        int dataDesc = wrap.getInt();

        ByteBuffer dataLengthBuffer = ByteBuffer.wrap(new byte[4]);
        dataLengthBuffer.putInt(dataDesc);
        byte[] array = dataLengthBuffer.array();
        byte[] first = new byte[1];
        byte[] afterThree = new byte[3];
        System.arraycopy(array, 0, first, 0, first.length);
        System.arraycopy(array, 1, afterThree, 0, afterThree.length);

        // 每个数据占用多少字节
        int firstInt = ByteUtil.oneBytesToInt(first);
        // 每组数据有多少个数据
        int afterThreeInt = ByteUtil.threeBytesToInt(afterThree);
        return afterThreeInt;
    }
}
