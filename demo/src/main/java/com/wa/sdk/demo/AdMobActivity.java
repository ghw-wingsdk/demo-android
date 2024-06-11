package com.wa.sdk.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.wa.sdk.WAConstants;
import com.wa.sdk.admob.core.WAAdMobProxy;
import com.wa.sdk.common.WASharedPrefHelper;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.utils.LogUtil;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.widget.TitleBar;

import org.json.JSONException;
import org.json.JSONObject;

public class AdMobActivity extends BaseActivity {
    public static boolean DEFAULT_APP_OPEN_AD_STATE = true; //开屏广告默认状态
    public static boolean DEFAULT_MAIN_BANNER_AD_STATE = true; //主页面横幅广告默认状态
    public static boolean DEFAULT_TEST = true; //客户端强制测试广告
    public static boolean IS_LOADING_AD = false;

    private final CustomCallback mCallbackInterstitial = new CustomCallback("插页");
    private final CustomCallback mCallbackAppOpen = new CustomCallback("开屏");
    private final CustomCallback mCallbackRewarded = new CustomCallback("激励");
    private WASharedPrefHelper mSpHelper;
    private EditText mEdtRewardedName;

    private class CustomCallback extends FullScreenContentCallback {
        private final String mAdType;
        /**
         * 展示次数
         */
        private int mShowTimes;

        public CustomCallback(String adType) {
            this.mAdType = adType;
        }

        @Override
        public void onAdClicked() {
            LogUtil.i(WAConstants.TAG, mAdType + " 广告：点击");
        }

        @Override
        public void onAdDismissedFullScreenContent() {
            LogUtil.i(WAConstants.TAG, mAdType + " 广告：关闭");
            IS_LOADING_AD = false;
        }

        @Override
        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
            String msg = mAdType + " 广告：显示失败, " + adError;
            LogUtil.i(WAConstants.TAG, msg);
            showShortToast(msg);
            IS_LOADING_AD = false;
        }

        @Override
        public void onAdImpression() {
            mShowTimes++;
            LogUtil.i(WAConstants.TAG, mAdType + " 广告：次数, " + mShowTimes);
        }

        @Override
        public void onAdShowedFullScreenContent() {
            LogUtil.i(WAConstants.TAG, mAdType + " 广告：显示成功");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admob);

        mSpHelper = WASharedPrefHelper.newInstance(AdMobActivity.this, WADemoConfig.SP_CONFIG_FILE_DEMO);

        TitleBar tb = findViewById(R.id.tb_admob);
        tb.setRightButton(android.R.drawable.ic_menu_close_clear_cancel, v -> finish());
        tb.setTitleText(R.string.admob);
        tb.setTitleTextColor(R.color.color_white);

        mEdtRewardedName = (EditText) findViewById(R.id.edt_rewarded_ad_name);

        ToggleButton btnEnableAppOpenAd = findViewById(R.id.btn_enable_app_open_ad);
        btnEnableAppOpenAd.setChecked(mSpHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_APP_OPEN_AD, DEFAULT_APP_OPEN_AD_STATE));
        btnEnableAppOpenAd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mSpHelper.saveBoolean(WADemoConfig.SP_KEY_ENABLE_APP_OPEN_AD, isChecked);
        });

        // 横幅广告
        FrameLayout containerBanner = findViewById(R.id.container_admob_banner);
        WAAdMobProxy.bindBannerAd(this, containerBanner);

        // 选项配置，显示控制
        findViewById(R.id.btn_show_ump_options).setVisibility(WAAdMobProxy.checkUmpOptions() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_check_interstitial_ad:
                boolean isInterstitialReady = WAAdMobProxy.checkInterstitialAdReady();
                showShortToast("插页广告Ready：" + isInterstitialReady);
                break;
            case R.id.btn_show_interstitial_ad:
                IS_LOADING_AD = true;
                WAAdMobProxy.showInterstitialAd(this, mCallbackInterstitial);
                break;
            case R.id.btn_check_app_open_ad:
                boolean isAppOpenReady = WAAdMobProxy.checkAppOpenAdReady();
                showShortToast("开屏广告Ready：" + isAppOpenReady);
                break;
            case R.id.btn_show_app_open_ad:
                IS_LOADING_AD = true;
                WAAdMobProxy.showAppOpenAd(this, mCallbackAppOpen);
                break;
            case R.id.btn_show_rewarded_ad:
                IS_LOADING_AD = true;
                String adName = mEdtRewardedName.getText().toString();
                String extInfo = "";
                JSONObject object = new JSONObject();
                try {
                    object.put("cpKey", "abcdefghiklmn");
                    object.put("cpValue", "AAAA2222BBBB");
                    extInfo = object.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                WAAdMobProxy.showRewardedAd(this, adName, extInfo, mCallbackRewarded);
                break;
            case R.id.btn_check_ump_options:
                showShortToast("检查UMP配置：" + WAAdMobProxy.checkUmpOptions());
                break;
            case R.id.btn_show_ump_options:
                WAAdMobProxy.showUmpOptions(this, new WACallback() {
                    @Override
                    public void onSuccess(int code, String message, Object result) {
                        showShortToast(message);
                        logTcfString(AdMobActivity.this);
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(int code, String message, Object result, Throwable throwable) {
                        showShortToast("showUmpOptions error: " + code + " - " + message);
                    }
                });
                break;
        }
    }

    public static void logTcfString(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String iabtcfTcString = preferences.getString("IABTCF_TCString", null);
        String iabtcfAddtlConsent = preferences.getString("IABTCF_AddtlConsent", null);
        LogUtil.i(TAG, "iabtcfTcString:" + iabtcfTcString);
        LogUtil.i(TAG, "iabtcfAddtlConsent:" + iabtcfAddtlConsent);
    }
}