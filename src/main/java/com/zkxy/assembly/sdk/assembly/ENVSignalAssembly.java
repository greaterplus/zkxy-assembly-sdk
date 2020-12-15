package com.zkxy.assembly.sdk.assembly;

import cn.hutool.core.collection.CollUtil;
import com.zkxy.assembly.sdk.model.DataFrame;
import com.zkxy.assembly.sdk.model.Device;
import com.zkxy.assembly.sdk.sensor.BaseSensor;
import com.zkxy.assembly.sdk.type.SignalType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Description : env传感器组包逻辑
 * @Author : ChangHao
 * @Date: 2020-08-20 15:16
 */
@SuppressWarnings("all")
public class ENVSignalAssembly extends BaseSignalAssembly {
    private static final Logger LOG = LoggerFactory.getLogger(ENVSignalAssembly.class);
    /**
     * 默认皮肤温度37
     */
    private final double DEFAULT_SKIN_TEMPERATURE = 37.0D;

    public ENVSignalAssembly(Device device) {
        this.device = device;
    }

    @Override
    public void assembly(String collectData) {
        List<Double> doubleList = resolveData(collectData);
        if (CollUtil.isEmpty(doubleList)) {
            return;
        }

        BaseSensor sensor = device.getSensor(SignalType.ENV);
        DataFrame dataFrame = new DataFrame(device.getDeviceConfig().getEnvColumn());
        dataFrame.setData(doubleList);
        sensor.offerDataFrame(dataFrame);
    }

    /**
     * env 数据有可能是4个数 或者3个数，所以列数配置时需要配置成为最小的
     * 例如： 如果有两款手环 带皮温 不带皮温 同时参与采集数据，需要设置列数为3
     */
    @Override
    boolean isFit(List<Double> list) {
        if (CollUtil.isEmpty(list)) {
            return false;
        }

        return list.size() == (int) (device.getDeviceConfig().getEnvRate() * device.getDeviceConfig().getEnvColumn());
    }
}
