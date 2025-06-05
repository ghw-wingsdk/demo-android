package com.wa.sdk.demo;

import static com.wa.sdk.demo.AdMobActivity.DEFAULT_APP_OPEN_AD_STATE;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.wa.sdk.admob.WAAdMobPublicProxy;
import com.wa.sdk.admob.model.WAAdMobAdsCallback;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.utils.LogUtil;
import com.wa.sdk.core.WACoreProxy;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.utils.Util;
import com.wa.sdk.demo.utils.WADemoConfig;

/**
 * 只有使用【AdMob开屏广告】才需要参考这里实现，否则无需关注本页面示例
 */
@android.annotation.SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {
    private static final int UMP_INIT_SUCCESS = 1;
    private static final int UMP_INIT_FAILURE = 2;
    private static final long TIME_TOTAL = 1000 * 5; // 模拟加载时间

    private TextView mTvCountDown;
    private long mMillisUntilFinished = TIME_TOTAL;
    private int mInitUmpState; //0-未知; 1-成功; 2-失败;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setScreenOrientation();
        super.onCreate(savedInstanceState);

        // 如果未打开开屏广告开关，直接跳过
        boolean isEnableAppOpenAd = getSpHelper().getBoolean(WADemoConfig.SP_KEY_ENABLE_APP_OPEN_AD, DEFAULT_APP_OPEN_AD_STATE);
        if (!isEnableAppOpenAd) {
            startMainActivity();
            return;
        }

        setContentView(R.layout.activity_splash);
        mTvCountDown = findViewById(R.id.tv_count_down);

        // 添加UMP监听处理，UMP默认关闭。如无特殊要求，不需要开启并接入UMP相关功能
        handleUMP();

        // 开启日志
        WACoreProxy.setDebugMode(getSpHelper().getBoolean(WADemoConfig.SP_KEY_ENABLE_DEBUG, true));
        showLoadingDialog("初始化中", false, false, null);
        // SDK初始化
        WACoreProxy.initialize(this, new WACallback<Void>() {
            @Override
            public void onSuccess(int code, String message, Void result) {
                cancelLoadingDialog();
                // 模拟游戏资源加载，加载完成后尝试显示开屏广告
                CountDownTimer timer = new MyCountDownTimer(TIME_TOTAL);
                timer.start();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(int code, String message, Void result, Throwable throwable) {
                cancelLoadingDialog();
                new AlertDialog.Builder(SplashActivity.this)
                        .setCancelable(false)
                        .setMessage("初始化失败，请退出应用重新进入")
                        .setPositiveButton("退出", (dialog, which) -> finish())
                        .show();
            }
        });
    }

    private class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long millisInFuture) {
            super(millisInFuture, 1000);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mMillisUntilFinished = millisUntilFinished;
            long seconds = mMillisUntilFinished / 1000;
            mTvCountDown.setText(seconds + "");
        }

        @Override
        public void onFinish() {
            mMillisUntilFinished = 0;
            mTvCountDown.setText("Done.");
            if (mInitUmpState == UMP_INIT_SUCCESS) {
                nextShowAppOpenAd(); // 已授权，可以尝试显示广告
            } else if (mInitUmpState == UMP_INIT_FAILURE) {
                nextAlter(); // 授权失败，提示继续或退出
            } else {
                LogUtil.i(TAG, "未授权，不处理");// 未进行授权，需要等授权后进行处理
            }
        }
    }

    private void startMainActivity() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.demo_fade_in, R.anim.demo_fade_out);
    }

    /**
     * 展示广告，广告后进入游戏
     */
    private void nextShowAppOpenAd() {
        LogUtil.i(TAG, "尝试显示开屏广告");
        // 如果需要控制在第N次打开游戏才显示开屏广告，可以在尝试显示广告前进行判断处理
        WAAdMobPublicProxy.showAppOpenAd(SplashActivity.this, new WAAdMobAdsCallback() {
            @Override
            public void onAdDismissed() {
                LogUtil.i(TAG, "开屏广告关闭");
                startMainActivity();
            }

            @Override
            public void onAdFailedToShow(@NonNull String error_message) {
                LogUtil.i(TAG, "开屏广告显示失败: " + error_message);
                startMainActivity();
            }
        });
    }

    /**
     * 提示同意授权失败，用户选择继续游戏或退出
     */
    private void nextAlter() {
        LogUtil.i(TAG, "采集同意失败，提示继续游戏或退出");
        new AlertDialog.Builder(this)
                .setMessage("UMP 未同意，AdMob功能无法初始化，继续游戏，或退出程序？")
                .setCancelable(false)
                .setPositiveButton("继续游戏", (dialog, which) -> startMainActivity())
                .setNegativeButton("退出", (dialog, which) -> finish()).show();
    }

    /**
     * 添加UMP监听处理，UMP默认关闭。如无特殊要求，不需要开启并接入UMP相关功能
     */
    private void handleUMP() {
        boolean isEnableUmp = false;
        Bundle manifest = Util.getMataDatasFromManifest(this);
        if (null != manifest && !manifest.isEmpty()) {
            isEnableUmp = manifest.getBoolean("com.wa.sdk.UMP_ENABLE", false);
        }

        if (isEnableUmp) {
            // 开启UMP需要添加监听
            Log.d(TAG, "UMP 开启，添加初始化回调");
            WAAdMobPublicProxy.addUmpInitCallback(new WACallback() {
                @Override
                public void onSuccess(int code, String message, Object result) {
                    AdMobActivity.logTcfString(SplashActivity.this);

                    mInitUmpState = UMP_INIT_SUCCESS;
                    if (mMillisUntilFinished == 0) {
                        nextShowAppOpenAd(); // 如果资源已经加载完成后UMP才进行同意，已经初始化可以尝试展示广告
                    }
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(int code, String message, Object result, Throwable throwable) {
                    LogUtil.w(TAG, "UMP 初始化失败：" + code + ", " + message);

                    mInitUmpState = UMP_INIT_FAILURE;
                    if (mMillisUntilFinished == 0) {
                        nextAlter(); // 如果资源已经加载完成后UMP才失败，提示继续或退出
                    }
                }
            });
        } else {
            // 关闭UMP无需监听，直接默认成功
            Log.d(TAG, "UMP is disable，default return success");
            mInitUmpState = UMP_INIT_SUCCESS;
        }
    }

}
