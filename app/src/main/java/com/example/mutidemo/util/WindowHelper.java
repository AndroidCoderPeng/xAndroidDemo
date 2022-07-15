package com.example.mutidemo.util;

import android.view.Window;
import android.view.WindowManager;

public class WindowHelper {
    public static void setScreenBrightness(Window window, float brightness) {
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = brightness;
        window.setAttributes(layoutParams);
    }
}
