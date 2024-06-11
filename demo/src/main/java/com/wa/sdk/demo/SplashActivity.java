package com.wa.sdk.demo;

import static com.wa.sdk.demo.AdMobActivity.DEFAULT_APP_OPEN_AD_STATE;
import static com.wa.sdk.demo.AdMobActivity.DEFAULT_TEST;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.wa.sdk.admob.core.WAAdMobProxy;
import com.wa.sdk.common.WASharedPrefHelper;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.utils.LogUtil;
import com.wa.sdk.core.WACoreProxy;
import com.wa.sdk.demo.base.BaseActivity;

/**
 * Created by yinglovezhuzhu@gmail.com on 2017/4/7.
 */

public class SplashActivity extends BaseActivity {
    private static final int UMP_INIT_SUCCESS = 1;
    private static final int UMP_INIT_FAILURE = 2;
    private static final long TIME_TOTAL = 1000 * 5; // 模拟加载时间

    private TextView mTvCountDown;
    private long mMillisUntilFinished = TIME_TOTAL;
    private int mInitUmpState; //0-未知; 1-成功; 2-失败;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mTvCountDown = findViewById(R.id.tv_count_down);

        WASharedPrefHelper sharedPrefHelper = WASharedPrefHelper.newInstance(this, WADemoConfig.SP_CONFIG_FILE_DEMO);
        boolean isEnableAppOpenAd = sharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_APP_OPEN_AD, DEFAULT_APP_OPEN_AD_STATE);

        // 如果未打开开屏广告开关，直接跳过
        if (!isEnableAppOpenAd) {
            mTvCountDown.setText("Done.");
            startMainActivity();
            return;
        }

        // 是否开启UMP，默认关闭
        boolean isEnableUmp = false;
        Bundle manifest = Util.getMataDatasFromManifest(this);
        if (null != manifest && !manifest.isEmpty() && manifest.containsKey("com.wa.sdk.UMP_ENABLE")) {
            isEnableUmp = manifest.getBoolean("com.wa.sdk.UMP_ENABLE", false);
        }
        if (isEnableUmp) {
            // 开启UMP需要添加监听
            Log.d(TAG, "UMP 开启，添加初始化回调");
            WAAdMobProxy.addUmpInitCallback(new WACallback() {
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
            Log.d(TAG, "UMP 关闭，默认UMP成功");
            mInitUmpState = UMP_INIT_SUCCESS;
        }
        // AdMob强制测试
        WAAdMobProxy.setTest(DEFAULT_TEST);
        // 开启日志
        WACoreProxy.setDebugMode(sharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_DEBUG, true));
        showLoadingDialog("初始化中", false, false, null);
        // SDK初始化
        WACoreProxy.initialize(this, new WACallback<Void>() {
            @Override
            public void onSuccess(int code, String message, Void result) {
                cancelLoadingDialog();
                // 模拟游戏资源加载
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
    }

    /**
     * 展示广告，广告后进入游戏
     */
    private void nextShowAppOpenAd() {
        LogUtil.i(TAG, "尝试显示开屏广告");
        // 如果需要控制在第N次打开游戏才显示开屏广告，可以在尝试显示广告前进行判断处理
        WAAdMobProxy.showAppOpenAd(SplashActivity.this, new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent();
                LogUtil.i(TAG, "开屏广告关闭");
                startMainActivity();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                super.onAdFailedToShowFullScreenContent(adError);
                LogUtil.i(TAG, "开屏广告显示失败: " + adError.getCode() + " - " + adError.getMessage());
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
}
