package com.wa.sdk.demo.base;


import android.app.Activity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.wa.sdk.common.WASharedPrefHelper;
import com.wa.sdk.demo.BuildConfig;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.WADemoConfig;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FlavorApiHelper {

    public static boolean isNowggFlavor() {
        return BuildConfig.FLAVOR.contains("nowgg");
    }

    public static boolean isAdMobFlavor() {
        return BuildConfig.FLAVOR.contains("admob");
    }

    public static class AdMob {

        public static Class<?> getAdMobActivityClass() {
            try {
                // 获取AdMobActivity类
                return Class.forName("com.wa.sdk.demo.AdMobActivity");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static void setTest() {
            try {
                // WAAdMobProxy.setTest(AdMobActivity.DEFAULT_TEST)

                boolean defaultTest = getSettingEnable("DEFAULT_TEST");
                Class<?> waAdMobProxyClass = Class.forName("com.wa.sdk.admob.core.WAAdMobProxy");
                Method setTestMethod = waAdMobProxyClass.getMethod("setTest", boolean.class);
                setTestMethod.invoke(null, defaultTest);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        public static void bindMainBannerAd(Activity activity) {
            try {
                // WAAdMobProxy.bindBannerAd(activity, ((FrameLayout) findViewById(R.id.layout_main_banner_ad)));

                boolean defaultBannerAdState = getSettingEnable("DEFAULT_BANNER_AD_STATE");
                WASharedPrefHelper spHelper = WASharedPrefHelper.newInstance(activity, WADemoConfig.SP_CONFIG_FILE_DEMO);
                boolean isEnableBannerAd = spHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_BANNER_AD, defaultBannerAdState);
                if (isEnableBannerAd) {
                    Class<?> waAdMobProxyClass = Class.forName("com.wa.sdk.admob.core.WAAdMobProxy");
                    Method bindBannerAdMethod = waAdMobProxyClass.getMethod("bindBannerAd", Activity.class, ViewGroup.class);
                    FrameLayout frameLayout = activity.findViewById(R.id.layout_main_banner_ad);
                    bindBannerAdMethod.invoke(null, activity, frameLayout);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        public static boolean getSettingEnable(String field) {
            try {
                // AdMobActivity.DEFAULT_APP_OPEN_AD_STATE
                // AdMobActivity.DEFAULT_TEST
                // AdMobActivity.DEFAULT_BANNER_AD_STATE
                Class<?> adMobActivityClass = Class.forName("com.wa.sdk.demo.AdMobActivity");
                Field defaultTestField = adMobActivityClass.getField(field);
                return defaultTestField.getBoolean(null);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

}
