package com.wa.sdk.demo.utils;

import android.app.Application;

public class DemoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Demo的初始化，跟SDK无关
        WASdkDemo.getInstance().initialize(this);
    }
}
