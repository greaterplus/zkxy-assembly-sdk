package com.zkxy.assembly.sdk.type;

public enum SignalType {
    /**
     * gsr
     */
    GSR("GSR"),
    /**
     * ppg
     */
    PPG("PPG"),
    /**
     * ENV
     */
    ENV("ENV"),
    /**
     * 重力加速度
     */
    ACC("ACC"),

    /**
     * 陀螺仪
     */
    GYRO("GYRO");

    /**
     * 传感器名称
     */
    private String signalName;


    SignalType(String signalName) {
        this.signalName = signalName;

    }

    public String getSignalName() {
        return signalName;
    }

}