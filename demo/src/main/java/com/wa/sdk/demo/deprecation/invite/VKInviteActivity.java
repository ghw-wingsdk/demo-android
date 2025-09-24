package com.wa.sdk.demo.deprecation.invite;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.wa.sdk.WAConstants;
import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.model.WAResult;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.social.WASocialProxy;

/**
 * Facebook邀请页面
 *
 */
public class VKInviteActivity extends BaseActivity {

    private static final String TAG = BaseActivity.TAG + "_VKINVITE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vk_invite);

        TitleBar titleBar = (TitleBar) findViewById(R.id.tb_vk_invite);
        titleBar.setTitleText(R.string.vk_invite);
        titleBar.setLeftButton(android.R.drawable.ic_menu_revert, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.setTitleTextColor(R.color.color_white);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!WACommonProxy.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_vk_app_invite) {
        } else if (id == R.id.btn_vk_game_service_invite) {
            startActivity(new Intent(VKInviteActivity.this, VKInviteFriendsActivity.class));
        } else if (id == R.id.btn_vk_invite_install_reward) {
            inviteInstallReward();
        } else if (id == R.id.btn_vk_invite_event_reward) {
            inviteEventReward();
        }
    }

    private void inviteInstallReward() {
        // 测试邀请奖励检查，邀请奖励检查必须是在Facebook登录的前提下
        showLoadingDialog(getString(R.string.loading), null);
        WASocialProxy.inviteInstallReward(this, WAConstants.CHANNEL_VK, new WACallback<WAResult>() {
            @Override
            public void onSuccess(int code, String message, WAResult result) {
                Log.i(TAG, "Install invite reward success: " + message);
                cancelLoadingDialog();
                showShortToast(message);
            }

            @Override
            public void onCancel() {
                cancelLoadingDialog();
                Log.i(TAG, "Install invite reward canceled: ");
            }

            @Override
            public void onError(int code, String message, WAResult result, Throwable throwable) {
                Log.i(TAG, "Install invite reward error: " + message);
                cancelLoadingDialog();
                showShortToast("code:" + code
                        + "\nmessage:" + message);
            }
        });
    }

    private void inviteEventReward() {
        showLoadingDialog(getString(R.string.loading), null);
        WASocialProxy.inviteEventReward(WAConstants.CHANNEL_VK, "testCpReward", new WACallback<WAResult>() {
            @Override
            public void onSuccess(int code, String message, WAResult result) {
                Log.i(TAG, "Invite event reward success: " + message);
                cancelLoadingDialog();
                showShortToast(message);
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "Invite event reward canceled: ");
                cancelLoadingDialog();
            }

            @Override
            public void onError(int code, String message, WAResult result, Throwable throwable) {
                Log.i(TAG, "Invite event reward error: " + message);
                cancelLoadingDialog();
                showShortToast("code:" + code
                        + "\nmessage:" + message);
            }
        });
    }
}
