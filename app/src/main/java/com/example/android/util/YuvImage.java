package com.example.android.util;

public class YuvImage {
    public byte[] data;
    public int width;
    public int height;

    public YuvImage(byte[] data, int width, int height) {
        this.data = data;
        this.width = width;
        this.height = height;
    }
}
