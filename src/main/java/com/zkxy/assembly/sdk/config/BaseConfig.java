package com.zkxy.assembly.sdk.config;

/**
 * @Description : config父类
 * @Author : ChangHao
 * @Date: 2020-12-15 10:14
 */
class BaseConfig {
    private int rate;
    private int column;

    public BaseConfig(int rate, int column) {
        this.rate = rate;
        this.column = column;
    }

    public int getRate() {
        return rate;
    }

    public int getColumn() {
        return column;
    }
}
