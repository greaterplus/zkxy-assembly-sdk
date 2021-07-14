package com.zkxy.assembly.sdk.model;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.zkxy.assembly.sdk.assembly.CheckSignalAndDistribute;
import com.zkxy.assembly.sdk.collect.task.WallClockTimeAdjustTask;
import com.zkxy.assembly.sdk.config.DeviceConfig;
import com.zkxy.assembly.sdk.sensor.BaseSensor;
import com.zkxy.assembly.sdk.sensor.SensorFactory;
import com.zkxy.assembly.sdk.type.SignalType;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("all")
public class Device implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(Device.class);
    private static final long serialVersionUID = 1371664521482309688L;

    /**
     * 设备macId
     */
    @Getter
    private String macId;

    /**
     * 此设备拥有的传感器
     */
    @Getter
    private final Map<SignalType, BaseSensor> sensorMap;

    /**
     * 管理该设备采集线程的map
     */
    private final Map<String, Thread> threadMap = new HashMap<>();

    /**
     * 装载属于本设备的collectData数据
     */
    @Getter
    private LinkedBlockingQueue<String> notifyDataQueue;

    /**
     * 设备和组包逻辑对象1:1
     */
    private CheckSignalAndDistribute distribute;

    @Getter @Setter
    private volatile CollectStatus collectStatus = CollectStatus.NOT_COLLECT;

    /**
     * 设备配置
     */
    @Getter @Setter
    private DeviceConfig deviceConfig;

    // 结束
    public Device(String macId, DeviceConfig config) {
        this.macId = macId;
        this.deviceConfig = config;

        SensorFactory sensorFactory = new SensorFactory(this);
        sensorMap = sensorFactory.create();

        this.distribute = new CheckSignalAndDistribute(this);
        this.notifyDataQueue = new LinkedBlockingQueue<>();
    }


    public LinkedBlockingQueue<String> getNotifyDataQueue() {
        return notifyDataQueue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Device device = (Device) o;
        return Objects.equals(macId, device.macId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(macId);
    }



    /**
     * 释放掉该设备采集线程资源
     */
    public synchronized void releaseCollectResource() {
        // 释放掉该设备的采集线程
        for (Map.Entry<String, Thread> threadEntry : threadMap.entrySet()) {
            threadEntry.getValue().interrupt();
        }

        // 卸载线程之后应该清空该设备所有线程管理中的线程 等待重新添加
        threadMap.clear();
    }

    /**
     * 设备开始采集
     */
    public synchronized boolean startCollect(WallClockTimeAdjustTask.CurrentTimeGetter currentTimeGetter) {

        this.setCollectStatus(CollectStatus.COLLECTING);
        clearAssemblyCache();
        this.notifyDataQueue.clear();

        String wallClockTimeAdjustThreadName = this.macId + StrUtil.SPACE + "wallClockTimeAdjustThread";
        Thread wallClockTimeAdjustThread = ThreadUtil.newThread(new WallClockTimeAdjustTask(this, currentTimeGetter), wallClockTimeAdjustThreadName);
        String assemblyThreadName = this.macId + StrUtil.SPACE + "assemblyTask";
        Thread assemblyThread = ThreadUtil.newThread(new AssemblyTask(this), assemblyThreadName);

        threadMap.put(wallClockTimeAdjustThreadName, wallClockTimeAdjustThread);
        threadMap.put(assemblyThreadName, assemblyThread);
        for (Map.Entry<String, Thread> threadEntry : threadMap.entrySet()) {
            threadEntry.getValue().start();
        }

        return true;
    }


    /**
     * 设备停止采集
     */
    public synchronized void stopCollect() {

        this.setCollectStatus(CollectStatus.NOT_COLLECT);
        releaseCollectResource();
    }

    /**
     * 根据传感器类型获取该传感器的dataframe队列
     *
     * @param signalType 传感器类型
     */
    LinkedBlockingQueue<DataFrame> getDataFrameQueue(SignalType signalType) {
        BaseSensor baseSensor = getSensor(signalType);
        if (baseSensor == null) {
            return null;
        }
        return baseSensor.getDataFrameQueue();
    }

    /**
     * 根据传感器类型获取rawdataFrame队列
     *
     * @param signalType 传感器类型
     */
    public LinkedBlockingQueue<RawDataFrame> getRawDataFrameQueue(SignalType signalType) {
        BaseSensor baseSensor = getSensor(signalType);
        if (baseSensor == null) {
            return null;
        }
        return baseSensor.getRawDataFrameQueue();
    }


    /**
     * 组包任务
     */
    class AssemblyTask implements Runnable {
        private Device device;

        public AssemblyTask(Device device) {
            this.device = device;
        }

        @Override
        public void run() {
            Thread currentThread = Thread.currentThread();
            while (!currentThread.isInterrupted() && device.getCollectStatus() == CollectStatus.COLLECTING) {
                try {
                    String collectData = null;
                    try {
                        collectData = notifyDataQueue.poll(500, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        break;
                    }
                    if (ObjectUtil.isEmpty(collectData)) {
                        continue;
                    }
                    distribute.checkSignalAndAssembly(collectData);
                } catch (Exception e) {
                    LOG.error("device: {} AssemblyTask 获取原始数据失败: {}",e);
                }
            }
            LOG.info("{} 任务退出", currentThread.getName());

        }
    }

    /**
     * 获取传感器
     * @param signalType
     */
    public BaseSensor getSensor(SignalType signalType) {
        return sensorMap.get(signalType);
    }


    Collection<BaseSensor> getAllSensor() {
        return sensorMap.values();
    }

    /**
     * 获取所有中台虚拟传感器
     */
    public Set<Map.Entry<SignalType, BaseSensor>> getAllSensorEntry() {
        return sensorMap.entrySet();
    }

    /**
     * 清理所有传感器的缓存数据
     */
    void clearAssemblyCache() {
        distribute.clearAssemblyCache();
    }

    void clearRawDataFrameQueue() {
        for (BaseSensor sensor : sensorMap.values()) {
            sensor.clearRawDataFrameQueue();
        }
    }

    /**
     * 关于采集的状态
     */
    public enum CollectStatus {
        NOT_COLLECT(0, "未开始采集"),
        /**
         * 正在采集状态为蓝牙设备为采集做好准备，具体开启采集由中台网关控制
         */
        READY_COLLECT(1, "准备采集"),
        /**
         * 该状态为中台网关已经开始采集命令，手环处于采集状态
         */
        COLLECTING(2, "正在采集");
        /**
         * 状态码
         */
        private int code;
        /**
         * 状态说明
         */
        private String message;

        CollectStatus(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
