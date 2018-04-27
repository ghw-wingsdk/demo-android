package com.wa.sdk.demo;

import android.text.InputType;

import com.wa.sdk.track.WAEventParameterName;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


/**
 * Created by yinglovezhuzhu@gmail.com on 2016/2/1.
 */
public class Util {

    private Util() {

    }

    public static void testCrash() {
        ArrayList<String> array = new ArrayList<>();
        String a = array.get(2);
    }

    public static int getInputType(String paramName) {
        if(WAEventParameterName.AGE.equals(paramName)
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
        } else if(WAEventParameterName.SUCCESS.equals(paramName)) {
            return InputType.TYPE_MASK_FLAGS;
        } else if(WAEventParameterName.CURRENCY_AMOUNT.equals(paramName)
                || WAEventParameterName.VERTUAL_COIN_AMOUNT.equals(paramName)
                || WAEventParameterName.PRICE.equals(paramName)) {
            return InputType.TYPE_NUMBER_FLAG_DECIMAL;
        }
        return InputType.TYPE_CLASS_TEXT;
    }



    public static String getMD5(InputStream is) throws IOException, NoSuchAlgorithmException {

        if(null == is) {
            return "";
        }

        try {
            byte[] buffer = new byte[1024 * 16]; // 16KB
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            int len;
            while((len = is.read(buffer)) != -1){
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
}
