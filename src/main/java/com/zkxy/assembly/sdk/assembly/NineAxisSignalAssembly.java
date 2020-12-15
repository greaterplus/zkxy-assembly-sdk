package com.zkxy.assembly.sdk.assembly;

import cn.hutool.core.collection.CollUtil;
import com.zkxy.assembly.sdk.model.DataFrame;
import com.zkxy.assembly.sdk.model.Device;
import com.zkxy.assembly.sdk.sensor.BaseSensor;
import com.zkxy.assembly.sdk.type.SignalType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description : 九轴组包逻辑
 * @Author : ChangHao
 * @Date: 2020-09-07 15:40
 */
@SuppressWarnings("all")
public class NineAxisSignalAssembly extends BaseSignalAssembly {
    private static final Logger LOG = LoggerFactory.getLogger(NineAxisSignalAssembly.class);

    public NineAxisSignalAssembly(Device device) {
        this.device = device;
    }

    @Override
    public void assembly(String collectData) {

        List<Double> doubleList = resolveData(collectData);
        if (CollUtil.isEmpty(doubleList)) {
            return;
        }

        List<Double> gravityList = new ArrayList<>();
        List<Double> magneticList = new ArrayList<>();
        List<Double> gyroscopeList = new ArrayList<>();

        /**
         * 分片数据 20hz采样率 每个分片有九个数据
         */
        List<List<Double>> sliceList = averageAssign(doubleList, device.getDeviceConfig().getNineAxisRate());
        for (List<Double> singleSlice : sliceList) {
            // 每个分片中的三个传感器数据
            List<List<Double>> signalList = averageAssign(singleSlice, 2);
            gyroscopeList.addAll(signalList.get(0));
            gravityList.addAll(signalList.get(1));
        }

        // 2重力加速度队列
        BaseSensor accSensor = device.getSensor(SignalType.ACC);
        // 1陀螺仪队列
        BaseSensor gyroSensor = device.getSensor(SignalType.GYRO);

        DataFrame accDataFrame = new DataFrame(device.getDeviceConfig().getNineAxisColumn());
        DataFrame gyroscopeDataFrame = new DataFrame(device.getDeviceConfig().getNineAxisColumn());

        gyroscopeDataFrame.setData(gyroscopeList);
        accDataFrame.setData(gravityList);

        gyroSensor.offerDataFrame(gyroscopeDataFrame);
        accSensor.offerDataFrame(accDataFrame);
    }

    /**
     * 九轴数据解析
     */
    @Override
    void defaultResolve(int afterThreeInt, ByteBuffer wrap, List<Double> dataList) {
        afterThreeInt = afterThreeInt<<1;
        for (int i = 0; i < afterThreeInt; i++) {
            int flag = 1;
            short shortValue = wrap.getShort();
            if (shortValue < 0) {
                flag = -1;
            }
            short value = (short) (0x7fff & shortValue);

            ByteBuffer buffer = ByteBuffer.allocate(2);
            buffer.asShortBuffer().put(value);
            byte[] byteArray = buffer.array();
            String binaryStr = digitsBinary(byteArray);
            double doubleValue = binaryToFloat(binaryStr, flag);
            dataList.add(doubleValue);
        }
    }


    /**
     * list集合均等分片
     */
    private static <T> List<List<T>> averageAssign(List<T> source, int n) {
        List<List<T>> result = new ArrayList<List<T>>();
        int remaider = source.size() % n;
        int number = source.size() / n;
        int offset = 0;
        for (int i = 0; i < n; i++) {
            List<T> value = null;
            if (remaider > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remaider--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }

    /**
     * 是否符合采样率的数据
     */
    @Override
    boolean isFit(List<Double> list) {
        if (CollUtil.isEmpty(list)) {
            return false;
        }
        return list.size() == (int) (device.getDeviceConfig().getNineAxisRate() * device.getDeviceConfig().getNineAxisColumn());
    }
}
