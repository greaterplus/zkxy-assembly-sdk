package com.zkxy.assembly.sdk.sensor;


import com.zkxy.assembly.sdk.model.Device;
import com.zkxy.assembly.sdk.type.SignalType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fc
 * @date 2020/11/3 15:03
 */
public class SensorFactory {

    private final Device owner;

    public SensorFactory(Device owner) {
        this.owner = owner;
    }

    public Map<SignalType, BaseSensor> create() {
        Map<SignalType, BaseSensor> map = new HashMap<>();
        map.put(SignalType.ENV, new EnvSensor(owner));
        map.put(SignalType.PPG, new PpgSensor(owner));
        map.put(SignalType.GSR, new GsrSensor(owner));
        map.put(SignalType.ACC, new AccSensor(owner));
        map.put(SignalType.GYRO, new GyroSensor(owner));
        return map;
    }
}
