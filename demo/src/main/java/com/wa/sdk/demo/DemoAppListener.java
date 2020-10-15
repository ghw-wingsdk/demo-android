package com.wa.sdk.demo;

import android.content.Context;
import android.content.res.Configuration;

import androidx.multidex.MultiDex;

import com.wa.sdk.WAApplication;
import com.wa.sdk.WAIApplicationListener;
import com.wa.sdk.common.utils.LogUtil;
import com.wa.sdk.downloader.util.Log;

/**
 * Date: 2020/05/11
 * Author:zgq
 */
public class DemoAppListener implements WAIApplicationListener {
    @Override
    public void onAppCreate(WAApplication app) {
        LogUtil.d(LogUtil.TAG,"onAppCreate");
    }

    @Override
    public void onAppAttachBaseContext(WAApplication app, Context base) {
        MultiDex.install(base);
    }

    @Override
    public void onAppConfigurationChanged(WAApplication app, Configuration newConfig) {
        LogUtil.d(LogUtil.TAG,"onAppConfigurationChanged");
    }

    @Override
    public void onAppTerminate(WAApplication app) {

    }
}
