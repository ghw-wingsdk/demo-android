package com.wa.sdk.demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.community.CommunityActivity;
import com.wa.sdk.demo.invite.InviteActivity;
import com.wa.sdk.demo.widget.TitleBar;

/**
 * 使用频率较低的功能
 */
public class RareFunctionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rare_function);

        TitleBar tb = findViewById(R.id.tb_rare_function);
        tb.setRightButton(android.R.drawable.ic_menu_close_clear_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tb.setTitleText(R.string.rare_function);
        tb.setTitleTextColor(R.color.color_white);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_community:
                startActivity(new Intent(this, CommunityActivity.class));
                break;
            case R.id.btn_invite:
                startActivity(new Intent(this, InviteActivity.class));
                break;
            case R.id.btn_update:
                startActivity(new Intent(this, UpdateActivity.class));
                break;
            case R.id.btn_test_crash:
                testCrash();
                break;
            case R.id.btn_clear_campaign:
                clearCampaign();
                break;
            case R.id.btn_video_ad:
                startActivity(new Intent(this, VideoAdActivity.class));
                break;
        }
    }

    /**
     * 闪退测试
     */
    public void testCrash() {
        new AlertDialog.Builder(this).setTitle(R.string.warming).setMessage(R.string.test_crash_warming).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Util.testCrash();
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    private void clearCampaign() {
//        GhwCampaignHelper.getInstance().clearCache(this);
//        Toast.makeText(this, "Campaign report cache clear success", Toast.LENGTH_SHORT).show();
    }
}