package com.wa.sdk.demo.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;

import com.wa.sdk.common.utils.LogUtil;
import com.wa.sdk.demo.BuildConfig;
import com.wa.sdk.track.WAEventParameterName;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


/**
 *
 */
public class DemoUtil {

    private DemoUtil() {

    }

    public static void testCrash() {
        ArrayList<String> array = new ArrayList<>();
        String a = array.get(2);
    }

    public static int getInputType(String paramName) {
        if (WAEventParameterName.AGE.equals(paramName)
                || WAEventParameterName.LEVEL.equals(paramName)
                || WAEventParameterName.IAP_AMOUNT.equals(paramName)
                || WAEventParameterName.ITEM_AMOUNT.equals(paramName)
                || WAEventParameterName.SCORE.equals(paramName)
                || WAEventParameterName.QUANTITY.equals(paramName)
                || WAEventParameterName.REGISTER_TIME.equals(paramName)
                || WAEventParameterName.VIP.equals(paramName)
                || WAEventParameterName.BINDED_GAME_GOLD.equals(paramName)
                || WAEventParameterName.GAME_GOLD.equals(paramName)
                || WAEventParameterName.FIGHTING.equals(paramName)
                || WAEventParameterName.TASK_STATUS.equals(paramName)
                || WAEventParameterName.AMOUNT.equals(paramName)
                || WAEventParameterName.CURRENCY_AMOUNT.equals(paramName)) {
            return InputType.TYPE_CLASS_NUMBER;
        } else if (WAEventParameterName.SUCCESS.equals(paramName)) {
            return InputType.TYPE_MASK_FLAGS;
        } else if (WAEventParameterName.CURRENCY_AMOUNT.equals(paramName)
                || WAEventParameterName.VERTUAL_COIN_AMOUNT.equals(paramName)
                || WAEventParameterName.PRICE.equals(paramName)) {
            return InputType.TYPE_NUMBER_FLAG_DECIMAL;
        }
        return InputType.TYPE_CLASS_TEXT;
    }

    public static void showHashKey(Context context) {

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES); //Your            package name here
//            PackageInfo info = context.getPackageManager().getPackageInfo("com.proficientcity.nyjjh", PackageManager.GET_SIGNATURES); //Your            package name here

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                LogUtil.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            // do nothing
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // do nothing
            e.printStackTrace();
        }

    }

    public static String getMD5(InputStream is) throws IOException, NoSuchAlgorithmException {

        if (null == is) {
            return "";
        }

        try {
            byte[] buffer = new byte[1024 * 16]; // 16KB
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            int len;
            while ((len = is.read(buffer)) != -1) {
                md5.update(buffer, 0, len);
            }
            return convertByteToHex(md5.digest());
        } finally {
            is.close();
            is = null;
        }
    }

    private static String convertByteToHex(byte[] byteData) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    public static Bundle getMataDatasFromManifest(Context context) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return ai.metaData;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.e("DemoSdk2", "Failed to load meta-data, NameNotFound: " + e.getMessage());
        }
        return null;
    }

    public static String getApkBuildInfo(Context context) {
        String packageName = "包名：" + context.getPackageName();
        String versionName = "版本名称：" + BuildConfig.VERSION_NAME;
        String versionCode = "代码版本：" + BuildConfig.VERSION_CODE;
        String buildType = "打包类型：" + BuildConfig.FLAVOR + "_" + BuildConfig.BUILD_TYPE;
        String buildTime = "打包时间：" + BuildConfig.DEMO_BUILD_TIME;
        String isTestRepository = "测试仓库：" + (BuildConfig.IS_TEST_REPOSITORY ? "是" : "否");
        String lineNew = "\n";

        Bundle manifest = DemoUtil.getMataDatasFromManifest(context);
        boolean isOfficialPackage = false;
        if (null != manifest && !manifest.isEmpty() && manifest.containsKey("com.wa.sdk.OFFICIAL_PACKAGE")) {
            isOfficialPackage = manifest.getBoolean("com.wa.sdk.OFFICIAL_PACKAGE", false);
        }
        String officialPackage = "官网包：" + (isOfficialPackage ? "是" : "否");

        return packageName + lineNew
                + versionName + lineNew
                + versionCode + lineNew
                + officialPackage + lineNew
                + buildType + lineNew
                + buildTime + lineNew
                + isTestRepository + lineNew
                ;
    }
}
