package com.zkxy.assembly.sdk.assembly;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.zkxy.assembly.sdk.model.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @Description : 检测传感器选择合适的方式进入组包逻辑
 * @Author : ChangHao
 * @Date: 2020-07-28 14:06
 */
@SuppressWarnings("all")
public class CheckSignalAndDistribute {

    private static Logger log = LoggerFactory.getLogger(CheckSignalAndDistribute.class);

    private Device device;

    private final Map<String, SignalAssembly> signalAssemblyMap;

    public CheckSignalAndDistribute(Device device) {
        this.device = device;
        SignalAssemblyFactory signalAssemblyFactory = new SignalAssemblyFactory(device);
        signalAssemblyMap = signalAssemblyFactory.create();
    }

    /**
     * 检测出传感器类型然后执行不同的组包逻辑
     *
     * @param collectData
     */
    public void checkSignalAndAssembly(String dataValue) {
        if (StrUtil.isBlank(dataValue) || dataValue.length() < 5) {
            return;
        }
        String signalType = dataValue.substring(4, 6).toLowerCase();
        SignalAssembly signalAssembly = signalAssemblyMap.get(signalType);
        if (ObjectUtil.isEmpty(signalAssembly)) {
            log.warn("目前只能处理ppg和gsr和ecg和nineAxis数据");
            return;
        }

        signalAssembly.assembly(dataValue);
    }

    public void clearAssemblyCache() {
        for (SignalAssembly sa : signalAssemblyMap.values()) {
            sa.clearCache();
        }
    }
}
