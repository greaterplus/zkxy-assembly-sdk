package com.zkxy.demo;

import cn.hutool.core.thread.ThreadUtil;
import com.zkxy.assembly.sdk.config.*;
import com.zkxy.assembly.sdk.model.Device;
import com.zkxy.assembly.sdk.model.RawDataFrame;
import com.zkxy.assembly.sdk.type.SignalType;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author ingin
 */
public class D1 {
    public static void main(String[] args) {

        GsrConfig gsrConfig = new GsrConfig(4, 1);
        PpgConfig ppgConfig = new PpgConfig(100, 1);
        EnvConfig envConfig = new EnvConfig(1, 3);
        NineAxisConfig nineAxisConfig = new NineAxisConfig(20, 6);
        DeviceConfig deviceConfig = new DeviceConfig(gsrConfig, ppgConfig, envConfig, nineAxisConfig);

        Device device1 = new Device("aa", deviceConfig);
        Device device2 = new Device("bb", deviceConfig);
        Device device3 = new Device("cc", deviceConfig);
        LinkedBlockingQueue<String> notifyDataQueue1 = device1.getNotifyDataQueue();
        LinkedBlockingQueue<String> notifyDataQueue2 = device2.getNotifyDataQueue();
        LinkedBlockingQueue<String> notifyDataQueue3 = device3.getNotifyDataQueue();
        LinkedBlockingQueue<RawDataFrame> rawDataFrameQueue1 = device1.getRawDataFrameQueue(SignalType.ENV);
        LinkedBlockingQueue<RawDataFrame> rawDataFrameQueue2 = device2.getRawDataFrameQueue(SignalType.ENV);
        LinkedBlockingQueue<RawDataFrame> rawDataFrameQueue3 = device3.getRawDataFrameQueue(SignalType.ENV);
        device1.startCollect();
        device2.startCollect();
        device3.startCollect();

        new Thread(() -> {
            int i = 0;
            while (i<=5) {
                notifyDataQueue1.offer("01000800F0B61E00030000042042E6414BE47C44E83BA441");
                notifyDataQueue2.offer("01000800F0B61E00030000042042E6414BE47C44E83BA441");
                notifyDataQueue3.offer("01000800F0B61E00030000042042E6414BE47C44E83BA441");
                ThreadUtil.sleep(1000);
                i++;
            }
        }).start();

        int i = 0;
        while (i<=10) {

            try {
                RawDataFrame take1 = rawDataFrameQueue1.take();
                RawDataFrame take2 = rawDataFrameQueue2.take();
                RawDataFrame take3 = rawDataFrameQueue3.take();
                System.out.println(take1);
                System.out.println(take2);
                System.out.println(take3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }
        device1.stopCollect();
        device2.stopCollect();
        device3.stopCollect();

    }
}
