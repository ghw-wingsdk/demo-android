package com.wa.sdk.demo.rare.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.wa.sdk.WAConstants;

/**
 *
 */

public class PushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (null == intent || !WAConstants.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            return;
        }
        Bundle bundle = intent.getExtras();
        if (null == bundle) {
            Log.e("PushReceiver", "通知打开，没有数据");
        } else {
            Log.e("PushReceiver", "通知打开，" + "包含的数据");
            for (String key : bundle.keySet()) {
                Log.e("PushReceiver", key + " -- " + bundle.get(key));
            }
        }
    }
}
