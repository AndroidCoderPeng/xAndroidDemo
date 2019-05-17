package com.example.mutidemo.event;

public class ZBEATEvent {

    private String require;
    private String response;

    public ZBEATEvent(String require, String response) {
        this.require = require;
        this.response = response;
    }

    public String getRequire() {
        return require;
    }

    public String getResponse() {
        return response;
    }
}
