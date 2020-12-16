package com.zkxy.assembly.sdk.model;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("all")
public class DataFrame implements Serializable {
    private static final long serialVersionUID = 8128341798307373373L;

    public DataFrame(int columeNumber) {
        this.columeNumber = columeNumber;
    }
    /**
     * 该帧数据包中的数据
     */
    private List<Double> data;
    /**
     * 列数量：
     * ppg和gsr： 1
     * nineAxias: 3
     */
    private int columeNumber;

    public List<Double> getData() {
        return data;
    }

    public void setData(List<Double> data) {
        this.data = data;
    }

    public int getColumeNumber() {
        return columeNumber;
    }
}
