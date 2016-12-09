package com.wa.sdk.demo;

import android.text.InputType;

import com.wa.sdk.track.WAEventParameterName;

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
}
