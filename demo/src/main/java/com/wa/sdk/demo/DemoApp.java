package com.wa.sdk.demo;

import static com.wa.sdk.demo.AdMobActivity.DEFAULT_APP_OPEN_AD_STATE;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.wa.sdk.WAConstants;
import com.wa.sdk.admob.core.WAAdMobProxy;
import com.wa.sdk.common.WASharedPrefHelper;
import com.wa.sdk.common.utils.LogUtil;

public class DemoApp extends Application implements Application.ActivityLifecycleCallbacks {
    private int mNumStarted = 0;
    private long mLastShowAdTime;
    private boolean mIsLoadingAd;
    private WASharedPrefHelper mSharedPrefHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPrefHelper = WASharedPrefHelper.newInstance(this, WADemoConfig.SP_CONFIG_FILE_DEMO);
        this.registerActivityLifecycleCallbacks(this);
    }

    private void showAppOpenAd(String where,Activity activity) {
        boolean isEnableAppOpenAd = mSharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_APP_OPEN_AD, DEFAULT_APP_OPEN_AD_STATE);
        if (!isEnableAppOpenAd) {
            return;
        }
        if (mIsLoadingAd) {
            LogUtil.w(WAConstants.TAG, "开屏广告 已经在显示中，请稍候...");
            return;
        }
        mIsLoadingAd = true;
        // 回到应用限制为20s只会有一次，正常你哥哥限制为更长时间比如20min
        long diff = System.currentTimeMillis() - mLastShowAdTime;
        boolean isLimited = diff < (20 * 1000);
        if (isLimited) {
            LogUtil.i(WAConstants.TAG, "开屏广告 距离上次显示时间 " + (diff / 1000) + " s，不到20s，不显示");
            mIsLoadingAd = false;
            return;
        }
        if (WAAdMobProxy.checkAppOpenAdReady()) {
            LogUtil.i(WAConstants.TAG, "开屏广告 - " + where + "：成功");
            WAAdMobProxy.showAppOpenAd(activity, new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    mIsLoadingAd = false;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    mIsLoadingAd = false;
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    mLastShowAdTime = System.currentTimeMillis(); // 记录开屏广告显示时间
                }
            });
        } else {
            LogUtil.i(WAConstants.TAG, "开屏广告 - " + where + "：失败");
            mIsLoadingAd = false;
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
//        Log.w("zii-", "onActivityCreated: "+activity);
        // 假设 MainActivity只会启动一次
//        if (activity instanceof MainActivity) {
//            showAppOpenAd("启动应用",activity);
//        }
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
//        Log.w("zii-", "onActivityStarted: " + activity);
        if (mNumStarted == 0) {
            // APP 返回前台，并且是 MainActivity
            if (!(activity instanceof SplashActivity)) {
                if (!AdMobActivity.IS_LOADING_AD) {
                    showAppOpenAd("回到应用",activity);
                } else {
                    LogUtil.i(WAConstants.TAG, "其他广告展示中，不展示回到应用的开屏广告");
                }
            }
        }
        mNumStarted++;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
//        Log.w("zii-", "onActivityResumed: " + activity);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
//        Log.w("zii-", "onActivityPaused: " + activity);
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
//        Log.w("zii-", "onActivityStopped: " + activity);
        mNumStarted--;
//        if (mNumStarted == 0) {
//             APP 进入后台
//        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

}
