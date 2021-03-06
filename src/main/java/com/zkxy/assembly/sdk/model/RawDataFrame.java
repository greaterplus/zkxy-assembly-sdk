package com.zkxy.assembly.sdk.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Builder
@ToString
public class RawDataFrame {
    @Getter @Setter
    private List<Double> dataList;
    @Getter @Setter
    private long timeStamp;

}
