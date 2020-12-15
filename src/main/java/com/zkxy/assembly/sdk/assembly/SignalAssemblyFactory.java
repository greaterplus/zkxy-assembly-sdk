package com.zkxy.assembly.sdk.assembly;


import com.zkxy.assembly.sdk.model.Device;

import java.util.HashMap;
import java.util.Map;


public class SignalAssemblyFactory {
    // ppg
    private static final String ppg_key = "0a";
    // gsr
    private static final String gsr_key = "07";
    // ecg
    private static final String env_key = "08";
    // nineAxis
    private static final String nineAxis_key = "09";

    private final Device owner;

    public SignalAssemblyFactory(Device owner) {
        this.owner = owner;
    }

    public Map<String, SignalAssembly> create() {
        Map<String, SignalAssembly> map = new HashMap<>();
        map.put(gsr_key, new GSRSignalAssembly(owner));
        map.put(ppg_key, new PPGSignalAssembly(owner));
        map.put(env_key, new ENVSignalAssembly(owner));
        map.put(nineAxis_key, new NineAxisSignalAssembly(owner));
        return map;
    }
}
