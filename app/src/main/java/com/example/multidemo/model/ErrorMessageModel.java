package com.example.multidemo.model;

public class ErrorMessageModel {
    private int code;
    private String data;
    private String exceptionClazz;
    private String message;
    private boolean isSuccess;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getExceptionClazz() {
        return exceptionClazz;
    }

    public void setExceptionClazz(String exceptionClazz) {
        this.exceptionClazz = exceptionClazz;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
