package com.wa.sdk.demo.tracking;

import android.os.Bundle;
import android.view.View;

import com.wa.sdk.demo.R;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.tracking.helper.WingSdkEventHelper;
import com.wa.sdk.demo.widget.TitleBar;

public class TrackingSimulateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_simulate);

        TitleBar titleBar = findViewById(R.id.tb_main);
        titleBar.setTitleText("事件");
        titleBar.setTitleTextColor(R.color.color_white);
        titleBar.setLeftButton(android.R.drawable.ic_menu_revert, v -> finish());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_user_import) {
            WingSdkEventHelper.ghw_user_import(this);
        } else if (id == R.id.btn_user_create) {
            WingSdkEventHelper.ghw_user_create(this);
        } else if (id == R.id.btn_initiated_purchase) {
            WingSdkEventHelper.ghw_initiated_purchase(this);
        } else if (id == R.id.btn_purchase) {
            WingSdkEventHelper.ghw_purchase(this);
        } else if (id == R.id.btn_level_achieved) {
            WingSdkEventHelper.ghw_level_achieved(this);
        } else if (id == R.id.btn_user_info_update) {
            WingSdkEventHelper.ghw_user_info_update(this);
        } else if (id == R.id.btn_lv_x) {
            WingSdkEventHelper.ghw_self_lv_x(this);
        } else if (id == R.id.btn_tutorial_completed) {
            WingSdkEventHelper.ghw_self_tutorial_completed(this);
        }
    }

}
