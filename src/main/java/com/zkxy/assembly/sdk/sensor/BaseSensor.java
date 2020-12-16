package com.zkxy.assembly.sdk.sensor;

import com.zkxy.assembly.sdk.model.DataFrame;
import com.zkxy.assembly.sdk.model.Device;
import com.zkxy.assembly.sdk.model.RawDataFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class BaseSensor {
    private static final Logger LOG = LoggerFactory.getLogger(BaseSensor.class);
    protected Device device;

    /**
     * 原始数据队列
     */
    protected final LinkedBlockingQueue<DataFrame> dataFrameQueue;
    /**
     * 组装完成数据队列
     */
    protected final LinkedBlockingQueue<RawDataFrame> rawDataFrameQueue;


    public BaseSensor(Device device) {
        this.device = device;
        this.dataFrameQueue = new LinkedBlockingQueue<>();
        this.rawDataFrameQueue = new LinkedBlockingQueue<>();

    }

    /**
     * 向原始队列中添加原始数据
     * @param dataFrame 原始数据
     */
    public void offerDataFrame(DataFrame dataFrame){

        this.dataFrameQueue.offer(dataFrame);
    }


    public LinkedBlockingQueue<DataFrame> getDataFrameQueue() {
        return dataFrameQueue;
    }

    public void clearDataFrameQueue() {
        dataFrameQueue.clear();
    }

    /**
     * 向rawDataFrameQueue中添加原始数据
     * @param rawDataFrame
     */
    public void offerRawDataFrame(RawDataFrame rawDataFrame) {

        rawDataFrameQueue.offer(rawDataFrame);
    }

    public LinkedBlockingQueue<RawDataFrame> getRawDataFrameQueue(){
        return rawDataFrameQueue;
    }

    public void clearRawDataFrameQueue() {
        rawDataFrameQueue.clear();
    }

}
