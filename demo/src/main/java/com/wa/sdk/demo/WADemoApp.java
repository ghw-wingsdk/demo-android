package com.wa.sdk.demo;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

/**
 *
 * Created by yinglovezhuzhu@gmail.com on 2016/7/11.
 */
public class WADemoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
