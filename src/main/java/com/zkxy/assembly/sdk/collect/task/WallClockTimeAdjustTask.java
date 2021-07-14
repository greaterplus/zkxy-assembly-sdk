package com.zkxy.assembly.sdk.collect.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zkxy.assembly.sdk.model.DataFrame;
import com.zkxy.assembly.sdk.model.Device;
import com.zkxy.assembly.sdk.model.RawDataFrame;
import com.zkxy.assembly.sdk.sensor.*;
import com.zkxy.assembly.sdk.type.SignalType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;


public class WallClockTimeAdjustTask implements Runnable{
    @FunctionalInterface
    public interface CurrentTimeGetter {
        long getCurrentTime();
    }
    private static final Logger LOG = LoggerFactory.getLogger(WallClockTimeAdjustTask.class);

    private Device device;
    private final Map<SignalType, LinkedBlockingQueue<DataFrame>> dataFrameQueueMap;
    private final Map<SignalType, BaseSensor> sensorMap;
    private volatile boolean isInterrupt = false;
    private long wallTime;
    private CurrentTimeGetter currentTimeGetter;

    public WallClockTimeAdjustTask(Device device, CurrentTimeGetter getter) {
        this.device = device;
        currentTimeGetter = getter;
        //this.wallTime = baseWallTime;
        Set<Map.Entry<SignalType, BaseSensor>> allSensor = device.getAllSensorEntry();
        sensorMap = device.getSensorMap();
        dataFrameQueueMap = new HashMap<>(allSensor.size());
        for (Map.Entry<SignalType, BaseSensor> entry : allSensor) {
            SignalType signalType = entry.getKey();
            BaseSensor sensor = entry.getValue();
            dataFrameQueueMap.put(signalType, sensor.getDataFrameQueue());
        }
    }

    @Override
    public void run() {
        if (ObjectUtil.isEmpty(device)) {
            return;
        }
        LOG.info("设备: {} WallClockTimeAdjustTask 任务开启 baseWallTime: [{}]", device.getMacId(), wallTime);

        wallTime = currentTimeGetter.getCurrentTime();
//        wallTime = System.currentTimeMillis();
        Thread currentThread = Thread.currentThread();
        while (!currentThread.isInterrupted() && device.getCollectStatus() == Device.CollectStatus.COLLECTING) {
            //此循环处理没一秒的数据
            Set<SignalType> notReadySensor = new HashSet<>(dataFrameQueueMap.keySet());
            int sleepCount = 0;
            while (!notReadySensor.isEmpty()) {
                //此循环处理有的数据还没有ready时的等待逻辑，目前是最多等待600毫秒
                Iterator<SignalType> iterator = notReadySensor.iterator();
                while (iterator.hasNext()) {
                    SignalType signalType = iterator.next();
                    LinkedBlockingQueue<DataFrame> dataFrameQueue  = dataFrameQueueMap.get(signalType);
                    if (dataFrameQueue.isEmpty()) {
                        continue;
                    }
                    List<Double> data = new ArrayList<>();
                    for (int i = 0; i < dataFrameQueue.size(); i++) {
                        DataFrame dataFrame = dataFrameQueue.poll();
                        if (ObjectUtil.isNotEmpty(dataFrame) && CollUtil.isNotEmpty(dataFrame.getData())) {
                            data.addAll(dataFrame.getData());
                        }
                    }
                    //下面这个判断应该不会命中，保险起见，做个判断
                    if (data.isEmpty()) {
                        continue;
                    }
                    RawDataFrame rawDataFrame = RawDataFrame.builder().timeStamp(wallTime).dataList(data).build();
                    BaseSensor baseSensor = sensorMap.get(signalType);
                    baseSensor.offerRawDataFrame(rawDataFrame);

                    iterator.remove();
                }

                if (sleepCount < 6 && !notReadySensor.isEmpty()) {
                    try {
                        ++sleepCount;
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        isInterrupt = true;
                        break;
                    }
                } else {
                    //将剩余的组成空包
                    for (SignalType signalType : notReadySensor) {
                        RawDataFrame rawDataFrame = RawDataFrame.builder().timeStamp(wallTime).dataList(new ArrayList<>()).build();
                        BaseSensor baseSensor = sensorMap.get(signalType);
                        baseSensor.offerRawDataFrame(rawDataFrame);
                    }
                    break;
                }
            }
            if (isInterrupt) {
                break;
            }

            wallTime += 1000;
            long currentTime = currentTimeGetter.getCurrentTime();
            //long currentTime = System.currentTimeMillis();
            if (wallTime > currentTime) {
                try {
                    Thread.sleep(wallTime - currentTime);
                } catch (InterruptedException e) {
                    break;
                }
            } else {
                LOG.error("device: {} 墙上时间修正有数据积压产生，已经产生延时 {}ms", device.getMacId(), currentTime - wallTime);
            }
        }
        LOG.info("{} 采集任务退出", currentThread.getName());
    }
}
