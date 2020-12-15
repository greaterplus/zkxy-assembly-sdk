package com.zkxy.assembly.sdk.assembly;

/**
 * @Description : 组装不同传感器的数据
 * @Author : ChangHao
 * @Date: 2020-07-28 14:06
 */
public interface SignalAssembly {

    /**
     * 根据不同的传感器类型进行不同组装包的逻辑
     * @param collectData
     */
    void assembly(String collectData);

    void clearCache();
}
