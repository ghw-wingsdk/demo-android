package com.wa.sdk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.wa.sdk.common.utils.LogUtil;
import com.wa.sdk.demo.base.BaseActivity;

import java.util.Set;

/**
 * Created by yinglovezhuzhu@gmail.com on 2017/4/7.
 */

public class SplashActivity extends BaseActivity {

    Handler mHandler = new Handler(Looper.myLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView iv = new ImageView(this);
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        iv.setImageResource(R.drawable.ic_launcher);
        setContentView(iv);
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
                finish();
//            }
//        }, 3000);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
