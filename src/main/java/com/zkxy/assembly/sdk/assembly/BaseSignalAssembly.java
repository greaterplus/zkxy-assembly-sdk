package com.zkxy.assembly.sdk.assembly;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.zkxy.assembly.sdk.model.Device;
import com.zkxy.assembly.sdk.utils.AssemblyUtils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description : 组包父类
 * @Author : ChangHao
 * @Date: 2020-09-30 16:19
 */
public abstract class BaseSignalAssembly implements SignalAssembly {
    protected Device device;
    protected final Map<String, List<Double>> cache = new HashMap<>();
    private static final String DIGITS = "0";
    /**
     * 两个字节包含16个二进制
     */
    private static final int TWO_BYTES_BIT_COUNT = 16;

    /**
     * 位数二进制长度
     */
    private static final int MANTISSA_COUNT = 12;

    /**
     * 二进制底数
     */
    private static final int TWO_RADIX = 2;

    /**
     * 十进制底数
     */
    private static final int TEN_RADIX = 10;


    @Override
    public void clearCache() {
        cache.clear();
    }

    /**
     * 是否符合采样率的数据
     */
    abstract boolean isFit(List<Double> list);


    /**
     * 解析数据并返回解析出来的数据
     */
    protected List<Double> resolveData(String dataValue) {

        if (StrUtil.isBlank(dataValue)) {
            return null;
        }
        Map<String, Object> resolveMap = AssemblyUtils.baseResolve(dataValue);
        String collectTime = (String) resolveMap.get(AssemblyUtils.COLLECT_TIME_KEY);
        ByteBuffer wrap = (ByteBuffer) resolveMap.get(AssemblyUtils.WRAP);
        int afterThreeInt = AssemblyUtils.resolveCollectLengthAndCount(wrap);
        if (afterThreeInt <= 0) {
            return null;
        }
        List<Double> dataList = new ArrayList<>();


        defaultResolve(afterThreeInt, wrap, dataList);

        List<Double> doubleList = cache.get(collectTime);
        if (CollUtil.isEmpty(doubleList)) {
            doubleList = new ArrayList<>();
            cache.put(collectTime, doubleList);
        }
        doubleList.addAll(dataList);

        if (isFit(doubleList)) {
            cache.remove(collectTime);
            if (cache.size() > 10) {
                cache.clear();
            }
            return doubleList;
        } else {
            return null;
        }
    }

    /**
     * 默认解析数据
     */
    void defaultResolve(int afterThreeInt, ByteBuffer wrap, List<Double> dataList) {
        for (int i = 0; i < afterThreeInt; i++) {
            Float value = wrap.getFloat();
            double doubleValue = value.doubleValue();
            dataList.add(doubleValue);
        }
    }


    /**
     * byte数组转二进制字符串
     *
     * @param bytes bytes数组
     */
    String byteArrToBinaryStr(byte[] bytes) {
        return new BigInteger(1, bytes).toString(BaseSignalAssembly.TWO_RADIX);
        // 这里的1代表正数
    }

    /**
     * 字节数组转换成二进制字符串，不足位数使用0补齐
     *
     * @param bytes 字节数组
     */
    String digitsBinary(byte[] bytes) {
        int length = bytes.length * Byte.SIZE;
        String binary = byteArrToBinaryStr(bytes);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = binary.length(); i < length; i++) {
            stringBuilder.append(DIGITS);
        }
        stringBuilder.append(binary);
        return stringBuilder.toString();
    }

    /**
     * 二进制转浮点数 只能解析16位二进制数据，两个字节
     */
    double binaryToFloat(String binary, int flag) {
        if (binary.length() != TWO_BYTES_BIT_COUNT) {
            return 0;
        }

        String exp = binary.substring(1, 4);
        Integer expValue = Integer.valueOf(exp, TWO_RADIX);
        String base = binary.substring(4);
        float floatValue = 0.0F;
        for (int i = 0; i < MANTISSA_COUNT; i++) {
            floatValue += (base.charAt(i) == '1' ? Math.pow(TWO_RADIX, -(i + 1)) : 0);
        }
        return flag * floatValue * Math.pow(TEN_RADIX, expValue);
    }

}
