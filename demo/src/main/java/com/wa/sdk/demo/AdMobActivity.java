package com.wa.sdk.demo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wa.sdk.WAConstants;
import com.wa.sdk.admob.WAAdMobPublicProxy;
import com.wa.sdk.admob.model.WAAdMobAdsCallback;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.utils.LogUtil;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.utils.WADemoConfig;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * AdMob广告
 */
public class AdMobActivity extends BaseActivity {
    public static boolean DEFAULT_APP_OPEN_AD_STATE = false; //开屏广告默认状态
    public static boolean DEFAULT_BANNER_AD_STATE = false; //主页面横幅广告默认状态
    public static boolean IS_LOADING_AD = false;

    private final CustomCallback mCallbackInterstitial = new CustomCallback("插页");
    private final CustomCallback mCallbackAppOpen = new CustomCallback("开屏");
    private final CustomCallback mCallbackRewarded = new CustomCallback("激励");
    private EditText mEdtRewardedName;
    private EditText mEdtOpenRewardedName;

    private class CustomCallback extends WAAdMobAdsCallback {
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
        public void onAdDismissed() {
            LogUtil.i(WAConstants.TAG, mAdType + " 广告：关闭");
            IS_LOADING_AD = false;
        }

        @Override
        public void onAdFailedToShow(@NonNull String error_message) {
            String msg = mAdType + " 广告：显示失败 - " + error_message;
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
        public void onAdShowed() {
            LogUtil.i(WAConstants.TAG, mAdType + " 广告：显示成功");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admob);
        setTitleBar(R.string.admob);
        mEdtRewardedName = findViewById(R.id.edt_rewarded_ad_name);
        mEdtOpenRewardedName = findViewById(R.id.edt_open_rewarded_ad_name);

        ToggleButton btnEnableAppOpenAd = findViewById(R.id.btn_enable_app_open_ad);
        btnEnableAppOpenAd.setChecked(getSpHelper().getBoolean(WADemoConfig.SP_KEY_ENABLE_APP_OPEN_AD, DEFAULT_APP_OPEN_AD_STATE));
        btnEnableAppOpenAd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getSpHelper().saveBoolean(WADemoConfig.SP_KEY_ENABLE_APP_OPEN_AD, isChecked);
            showShortToast("重启应用后生效");
        });
        ToggleButton btnEnableBannerAd = findViewById(R.id.btn_enable_banner_ad);
        btnEnableBannerAd.setChecked(getSpHelper().getBoolean(WADemoConfig.SP_KEY_ENABLE_BANNER_AD, DEFAULT_BANNER_AD_STATE));
        btnEnableBannerAd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getSpHelper().saveBoolean(WADemoConfig.SP_KEY_ENABLE_BANNER_AD, isChecked);
            showShortToast("重启应用后生效");
        });
        // 横幅广告
        boolean isEnableBannerAd = getSpHelper().getBoolean(WADemoConfig.SP_KEY_ENABLE_BANNER_AD, DEFAULT_BANNER_AD_STATE);
        if (isEnableBannerAd && WAAdMobPublicProxy.isOpenBannerAd()) {
            FrameLayout containerBanner = findViewById(R.id.container_admob_banner);
            WAAdMobPublicProxy.bindBannerAd(this, containerBanner);
        }
        // 选项配置，显示控制
        findViewById(R.id.btn_show_ump_options).setVisibility(WAAdMobPublicProxy.checkUmpOptions() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        // 广告开关判断
        if (id == R.id.btn_is_open_app_open_ad) {
            boolean isOpenAppOpenAd = WAAdMobPublicProxy.isOpenAppOpenAd();
            showShortToast("是否打开开屏广告：" + isOpenAppOpenAd);
        } else if (id == R.id.btn_is_open_banner_ad) {
            boolean isOpenBannerAd = WAAdMobPublicProxy.isOpenBannerAd();
            showShortToast("是否打开横幅广告：" + isOpenBannerAd);
        } else if (id == R.id.btn_is_open_interstitial_ad) {
            boolean isOpenInterstitialAd = WAAdMobPublicProxy.isOpenInterstitialAd();
            showShortToast("是否打开插页广告：" + isOpenInterstitialAd);
        } else if (id == R.id.btn_is_open_rewarded_ad) {
            String openRewardedName = mEdtOpenRewardedName.getText().toString();
            boolean openRewardedWithAdName = WAAdMobPublicProxy.isOpenRewardedWithAdName(openRewardedName);
            showShortToast("是否打开激励广告 (" + openRewardedName + ")：" + openRewardedWithAdName);
        }
        // isOpenWithCallback(id);

        // 广告接口
        if (id == R.id.btn_check_interstitial_ad) {
            boolean isInterstitialReady = WAAdMobPublicProxy.checkInterstitialAdReady();
            showShortToast("插页广告Ready：" + isInterstitialReady);
        } else if (id == R.id.btn_show_interstitial_ad) {
            IS_LOADING_AD = true;
            WAAdMobPublicProxy.showInterstitialAd(this, mCallbackInterstitial);
        } else if (id == R.id.btn_check_app_open_ad) {
            boolean isAppOpenReady = WAAdMobPublicProxy.checkAppOpenAdReady();
            showShortToast("开屏广告Ready：" + isAppOpenReady);
        } else if (id == R.id.btn_show_app_open_ad) {
            IS_LOADING_AD = true;
            WAAdMobPublicProxy.showAppOpenAd(this, mCallbackAppOpen);
        } else if (id == R.id.btn_show_rewarded_ad) {
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
            WAAdMobPublicProxy.showRewardedAd(this, adName, extInfo, mCallbackRewarded);
        }

        // UMP 功能接口
        if (id == R.id.btn_check_ump_options) {
            showShortToast("检查UMP配置：" + WAAdMobPublicProxy.checkUmpOptions());
        } else if (id == R.id.btn_show_ump_options) {
            WAAdMobPublicProxy.showUmpOptions(this, new WACallback() {
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
        }
    }

    /**
     * 判断广告开关状态（异步回调）
     */
    private void isOpenWithCallback(int id) {
        if (id == R.id.btn_is_open_app_open_ad) {
            WAAdMobPublicProxy.isOpenAppOpenAdWithCallback(new WACallback<Boolean>() {
                @Override
                public void onSuccess(int code, String message, Boolean result) {
                    showShortToast("是否打开开屏广告：" + result);
                    new AlertDialog.Builder(AdMobActivity.this)
                            .setMessage("test....")
                            .show();
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(int code, String message, @Nullable Boolean result, @Nullable Throwable throwable) {

                }
            });
        } else if (id == R.id.btn_is_open_banner_ad) {
            WAAdMobPublicProxy.isOpenBannerAdWithCallback(new WACallback<Boolean>() {
                @Override
                public void onSuccess(int code, String message, Boolean result) {
                    showShortToast("是否打开横幅广告：" + result);
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(int code, String message, @Nullable Boolean result, @Nullable Throwable throwable) {

                }
            });
        } else if (id == R.id.btn_is_open_interstitial_ad) {
            WAAdMobPublicProxy.isOpenInterstitialAdWithCallback(new WACallback<Boolean>() {
                @Override
                public void onSuccess(int code, String message, Boolean result) {
                    showShortToast("是否打开插页广告：" + result);
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(int code, String message, @Nullable Boolean result, @Nullable Throwable throwable) {

                }
            });
        } else if (id == R.id.btn_is_open_rewarded_ad) {
            String openRewardedName = mEdtOpenRewardedName.getText().toString();
            WAAdMobPublicProxy.isOpenRewardedWithAdNameCallback(openRewardedName, new WACallback<Boolean>() {
                @Override
                public void onSuccess(int code, String message, Boolean result) {
                    showShortToast("是否打开激励广告 (" + openRewardedName + ")：" + result);
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(int code, String message, @Nullable Boolean result, @Nullable Throwable throwable) {

                }
            });
        }
    }

    public static void logTcfString(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String iabtcfTcString = preferences.getString("IABTCF_TCString", null);
        String iabtcfAddtlConsent = preferences.getString("IABTCF_AddtlConsent", null);
        LogUtil.i(TAG, "iabtcfTcString:" + iabtcfTcString);
        LogUtil.i(TAG, "iabtcfAddtlConsent:" + iabtcfAddtlConsent);
    }
}