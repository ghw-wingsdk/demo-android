package com.wa.sdk.demo.base;


import com.wa.sdk.WAConstants;
import com.wa.sdk.demo.BuildConfig;

public class FlavorApiHelper {

    public static boolean isNowggFlavor() {
        return BuildConfig.FLAVOR.contains("nowgg");
    }

    public static boolean isLeidianFlavor() {
        return BuildConfig.FLAVOR.contains("leidian");
        }

    public static String getQueryProductChannel() {
        if (isNowggFlavor()) {
            return WAConstants.CHANNEL_NOWGG;
        } else if (isLeidianFlavor()) {
            return WAConstants.CHANNEL_LEIDIAN;
        } else {
            return WAConstants.CHANNEL_GOOGLE;
        }
    }

}
