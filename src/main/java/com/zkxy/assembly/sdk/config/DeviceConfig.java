package com.zkxy.assembly.sdk.config;

/**
 * @Description : 设备配置
 * @Author : ChangHao
 * @Date: 2020-12-10 16:01
 */
public class DeviceConfig {
    private int gsrRate;
    private int ppgRate;
    private int envRate;
    private int nineAxisRate;
    private int gsrColumn;
    private int ppgColumn;
    private int envColumn;
    private int nineAxisColumn;
    private int accRate;
    private int gyroRate;
    private int accColumn;
    private int gyroColumn;

    /**
     * @param gsrConfig gsr配置
     * @param ppgConfig ppg配置
     * @param envConfig env配置
     * @param nineAxisConfig 九轴配置
     */
    public DeviceConfig(GsrConfig gsrConfig, PpgConfig ppgConfig, EnvConfig envConfig, NineAxisConfig nineAxisConfig) {
        this.gsrRate = gsrConfig.getRate();
        this.gsrColumn = gsrConfig.getColumn();
        this.ppgRate = ppgConfig.getRate();
        this.ppgColumn = ppgConfig.getColumn();
        this.envRate = envConfig.getRate();
        this.envColumn = envConfig.getColumn();
        this.nineAxisRate = nineAxisConfig.getRate();
        this.nineAxisColumn = nineAxisConfig.getColumn();
        this.accRate = nineAxisConfig.getRate();
        this.accColumn = nineAxisConfig.getColumn() / 2;
        this.gyroRate = nineAxisConfig.getRate();
        this.gyroColumn = nineAxisConfig.getColumn() / 2;
    }

    public int getGsrRate() {
        return gsrRate;
    }

    public int getPpgRate() {
        return ppgRate;
    }

    public int getEnvRate() {
        return envRate;
    }

    public int getNineAxisRate() {
        return nineAxisRate;
    }

    public int getGsrColumn() {
        return gsrColumn;
    }

    public int getPpgColumn() {
        return ppgColumn;
    }

    public int getEnvColumn() {
        return envColumn;
    }

    public int getNineAxisColumn() {
        return nineAxisColumn;
    }

    public int getAccRate() {
        return accRate;
    }

    public int getGyroRate() {
        return gyroRate;
    }

    public int getAccColumn() {
        return accColumn;
    }

    public int getGyroColumn() {
        return gyroColumn;
    }
}
