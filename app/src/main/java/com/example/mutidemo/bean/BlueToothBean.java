package com.example.mutidemo.bean;

public class BlueToothBean {
    private String blueToothName;
    private String blueToothAddress;

    public BlueToothBean(String blueToothName, String blueToothAddress) {
        this.blueToothName = blueToothName;
        this.blueToothAddress = blueToothAddress;
    }

    public String getBlueToothName() {
        return blueToothName;
    }

    public void setBlueToothName(String blueToothName) {
        this.blueToothName = blueToothName;
    }

    public String getBlueToothAddress() {
        return blueToothAddress;
    }

    public void setBlueToothAddress(String blueToothAddress) {
        this.blueToothAddress = blueToothAddress;
    }
}
