package com.example.multidemo.model;

public class Satellite {
    private String svid; // 卫星svid
    private int type; // 卫星导航系统的类型
    private String typeName; // 卫星导航系统的类型
    private float signal; // 卫星的信噪比（信号）
    private float elevation; // 卫星的仰角
    private float azimuth; // 卫星的方位角

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

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public float getSignal() {
        return signal;
    }

    public void setSignal(float signal) {
        this.signal = signal;
    }

    public float getElevation() {
        return elevation;
    }

    public void setElevation(float elevation) {
        this.elevation = elevation;
    }

    public float getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
    }
}