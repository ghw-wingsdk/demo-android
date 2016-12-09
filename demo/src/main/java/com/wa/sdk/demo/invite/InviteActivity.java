package com.wa.sdk.demo.invite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.WASharedPrefHelper;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.WADemoConfig;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.widget.TitleBar;

/**
 * 邀请好友
 * Created by ghw_zhangyy on 2015/7/1.
 */
public class InviteActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_invite);

        TitleBar titleBar = (TitleBar) findViewById(R.id.tb_invite);
        titleBar.setTitleText(R.string.invite);
        titleBar.setLeftButton(android.R.drawable.ic_menu_revert, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.setTitleTextColor(R.color.color_white);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fb_invite:
                startActivity(new Intent(this, FBInviteActivity.class));
                break;
            case R.id.btn_vk_invite:
                startActivity(new Intent(this, VKInviteActivity.class));
                break;
            default:
                break;
        }
    }
}
