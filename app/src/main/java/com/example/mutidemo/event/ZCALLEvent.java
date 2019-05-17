package com.example.mutidemo.event;

public class ZCALLEvent {

    private String require;
    private String response;

    public ZCALLEvent(String require, String response) {
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
