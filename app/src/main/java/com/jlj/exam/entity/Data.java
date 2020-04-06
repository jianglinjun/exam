package com.jlj.exam.entity;

/**
 * 属性名跟下行数据保持一致
 */
public class Data {
    private double volume_of_mobile_data;
    private String quarter;
    private String year;
    private boolean isDrop;

    public boolean isDrop() {
        return isDrop;
    }

    public void setDrop(boolean drop) {
        isDrop = drop;
    }

    public double getVolume_of_mobile_data() {
        return volume_of_mobile_data;
    }

    public void setVolume_of_mobile_data(double volume_of_mobile_data) {
        this.volume_of_mobile_data = volume_of_mobile_data;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

}
