package com.example.multidemo.model;

public class Satellite {
    private String svid; // 卫星svid
    private int type; // 卫星导航系统的类型
    private int signal; // 卫星的信噪比（信号）
    private int elevation; // 卫星的仰角
    private int azimuth; // 卫星的方位角
    private boolean usedInFix; // 是否有卫星的星历数据

    public String getSvid() {
        return svid;
    }

    public void setSvid(String svid) {
        this.svid = svid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    public int getElevation() {
        return elevation;
    }

    public void setElevation(int elevation) {
        this.elevation = elevation;
    }

    public int getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(int azimuth) {
        this.azimuth = azimuth;
    }

    public boolean isUsedInFix() {
        return usedInFix;
    }

    public void setUsedInFix(boolean usedInFix) {
        this.usedInFix = usedInFix;
    }
}