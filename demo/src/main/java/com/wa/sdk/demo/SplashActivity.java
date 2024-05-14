package com.wa.sdk.demo;

import static com.wa.sdk.demo.AdMobActivity.DEFAULT_APP_OPEN_AD_STATE;
import static com.wa.sdk.demo.AdMobActivity.DEFAULT_TEST;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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

    private static final long TIME_TOTAL = 1000 * 5; // 模拟加载时间
    private CountDownTimer mTimer;
    private long mMillisUntilFinished = TIME_TOTAL;
    private TextView mTvCountDown;
    private boolean mIsInitUmp;
    private WASharedPrefHelper mSharedPrefHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mTvCountDown = findViewById(R.id.tv_count_down);

        mSharedPrefHelper = WASharedPrefHelper.newInstance(this, WADemoConfig.SP_CONFIG_FILE_DEMO);
        WACoreProxy.setDebugMode(mSharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_DEBUG, true));

        boolean isEnableAppOpenAd = mSharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_APP_OPEN_AD, DEFAULT_APP_OPEN_AD_STATE);
        // 如果未打开开屏广告开关，直接跳过
        if (!isEnableAppOpenAd) {
            mIsInitUmp = true;
            mTvCountDown.setText("Done.");
            startMainActivity();
            return;
        }

        WAAdMobProxy.addUmpInitCallback(new WACallback() {
            @Override
            public void onSuccess(int code, String message, Object result) {
                mIsInitUmp = true;
                AdMobActivity.logTcfString(SplashActivity.this);
                if (mMillisUntilFinished == 0) {
                    // 如果资源已经加载完成后UMP才进行同意，需要判断进入游戏
                    startMainActivity();
                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(int code, String message, Object result, Throwable throwable) {

            }
        });
        WAAdMobProxy.setTest(DEFAULT_TEST);
        WACoreProxy.initialize(this);
        mTimer = new MyCountDownTimer(TIME_TOTAL);
        mTimer.start();
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
    }

    private void startMainActivity() {
        //必须UMP同意后才能进入主页
        if (mIsInitUmp) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            LogUtil.i(TAG, "进入MainActivity");
        } else {
            LogUtil.i(TAG, "UMP 未同意，不进入MainActivity");
        }
    }
}
