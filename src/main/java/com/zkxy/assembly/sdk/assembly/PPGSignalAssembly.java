package com.zkxy.assembly.sdk.assembly;

import cn.hutool.core.collection.CollUtil;
import com.zkxy.assembly.sdk.model.DataFrame;
import com.zkxy.assembly.sdk.model.Device;
import com.zkxy.assembly.sdk.sensor.BaseSensor;
import com.zkxy.assembly.sdk.type.SignalType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @Description : ppg传感器数据组包逻辑
 * @Author : ChangHao
 * @Date: 2020-07-28 14:08
 */
@SuppressWarnings("all")
public class PPGSignalAssembly extends BaseSignalAssembly {
    private static final Logger LOG = LoggerFactory.getLogger(PPGSignalAssembly.class);

    public PPGSignalAssembly(Device device) {
        this.device = device;
    }

    @Override
    public void assembly(String collectData) {

        List<Double> doubleList = resolveData(collectData);
        if (CollUtil.isEmpty(doubleList)) {
            return;
        }

        BaseSensor sensor = device.getSensor(SignalType.PPG);
        DataFrame dataFrame = new DataFrame(device.getDeviceConfig().getPpgColumn());
        dataFrame.setData(doubleList);
        sensor.offerDataFrame(dataFrame);
    }

    /**
     * 解析ppg数据
     */
    @Override
    void defaultResolve(int afterThreeInt, ByteBuffer wrap, List<Double> dataList) {
        double prefix = 0.0D;
        double doubleValue;
        afterThreeInt = afterThreeInt << 1;
        for (int i = 0; i < afterThreeInt; i++) {
            if (i == 0) {
                Integer firstValue = wrap.getInt();
                doubleValue = firstValue.doubleValue();

            } else {
                short shortValue = wrap.getShort();
                doubleValue = prefix + shortValue;

            }
            prefix = doubleValue;
            dataList.add(doubleValue);

        }
    }

    /**
     * 是否符合采样率的数据
     */
    @Override
    boolean isFit(List<Double> list) {
        if (CollUtil.isEmpty(list)) {
            return false;
        }
        return list.size() == (int) (device.getDeviceConfig().getPpgRate() * device.getDeviceConfig().getPpgColumn());
    }
}
