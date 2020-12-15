package com.zkxy.assembly.sdk.sensor;

/**
 * @Description : 传感器相关的一些字段
 * @Author : ChangHao
 * @Date: 2020-09-10 15:41
 */
public interface SensorConstants {

    /**
     * gsr 最小值
     */
    double GSR_MIN = 1e3f;

    /**
     * gsr 最大值
     */
    double GSR_MAX = 1e7f;

    /**
     * ppg最小值
     */
    double PPG_MIN = 1e4f;

    /**
     * ppg最大值
     */
    double PPG_MAX = 2080641f;

    /**
     * 非法数据最大计数
     */
    int ILLEGAL_MAX_COUNT = 3;


}
