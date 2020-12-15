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
 * @Description : gsr传感器数据组包逻辑
 * @Author : ChangHao
 * @Date: 2020-07-28 14:08
 */
@SuppressWarnings("all")
public class GSRSignalAssembly extends BaseSignalAssembly {
    private static final Logger LOG = LoggerFactory.getLogger(GSRSignalAssembly.class);

    public GSRSignalAssembly(Device device) {
        this.device = device;
    }

    @Override
    public void assembly(String collectData) {
        List<Double> doubleList = resolveData(collectData);
        if (CollUtil.isEmpty(doubleList)) {
            return;
        }

        BaseSensor sensor = device.getSensor(SignalType.GSR);
        DataFrame dataFrame = new DataFrame(device.getDeviceConfig().getGsrColumn());
        dataFrame.setData(doubleList);
        sensor.offerDataFrame(dataFrame);
    }

    /**
     * 是否符合采样率的数据
     */
    @Override
    boolean isFit(List<Double> list) {
        if (CollUtil.isEmpty(list)) {
            return false;
        }
        return list.size() == (int) (device.getDeviceConfig().getGsrRate() * device.getDeviceConfig().getGsrColumn());
    }

}
