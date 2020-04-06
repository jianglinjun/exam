package com.jlj.exam.entity;

import java.util.List;

public class ResultEntity {
    private double maxValue;
    private double minValue;

    private List<Data> records;

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public List<Data> getRecords() {
        return records;
    }

    public void setRecords(List<Data> records) {
        this.records = records;
    }
}
