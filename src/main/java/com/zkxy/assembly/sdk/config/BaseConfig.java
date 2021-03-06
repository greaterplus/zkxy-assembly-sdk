package com.zkxy.assembly.sdk.config;

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
