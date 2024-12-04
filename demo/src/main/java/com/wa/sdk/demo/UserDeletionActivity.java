package com.wa.sdk.demo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.model.WAResult;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.user.WAUserProxy;
import com.wa.sdk.user.model.WADeleteResult;
import com.wa.sdk.user.model.WALoginResult;

public class UserDeletionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_deletion);

        TitleBar titleBar = findViewById(R.id.titlebar);
        titleBar.setTitleText("账号删除");
        titleBar.setLeftButton(android.R.drawable.ic_menu_revert, v -> finish());
        titleBar.setTitleTextColor(R.color.color_white);

        Button btnRequestDeletionUi = findViewById(R.id.btn_request_deletion_ui);
        Button btnRequestDeletion = findViewById(R.id.btn_request_deletion);
        Button btnCancelDeletion = findViewById(R.id.btn_cancel_deletion);

        btnRequestDeletionUi.setOnClickListener(v -> requestDeletionUi());
        btnRequestDeletion.setOnClickListener(v -> requestDeletion());
        btnCancelDeletion.setOnClickListener(v -> cancelDeletion());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        WACommonProxy.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 账号删除带UI
     */
    private void requestDeletionUi() {
        if (!WASdkDemo.getInstance().isLogin()) {
            showLoginTips();
            return;
        }
        WAUserProxy.requestDeleteAccountUI(UserDeletionActivity.this, new WACallback<WAResult>() {
            @Override
            public void onSuccess(int code, String message, WAResult result) {
                showLongToast("申请账号删除成功!\nCP需要退出sdk，然后再退出游戏到登录页");
                // CP调用退出登录
                WAUserProxy.logout();
                WASdkDemo.getInstance().logout();
            }

            @Override
            public void onCancel() {
                showShortToast("取消");
            }

            @Override
            public void onError(int code, String message, WAResult result, Throwable throwable) {
                showShortToast("错误：" + code + " , " + message);
            }
        });
    }

    /**
     * 申请账号删除无UI
     */
    private void requestDeletion() {
        if (!WASdkDemo.getInstance().isLogin()) {
            showLoginTips();
            return;
        }
        WAUserProxy.requestDeleteAccount(new WACallback<WADeleteResult>() {
            @Override
            public void onSuccess(int code, String message, WADeleteResult result) {
                showLongToast("申请账号删除成功\nCP需要退出sdk，然后再退出游戏到登录页\n申请时间：" + result.getApplyDate() + "\n注销时间：" + result.getDeleteDate());
                WALoginResult loginAccount = WASdkDemo.getInstance().getLoginAccount();
                loginAccount.setApplyDeleteStatus(1);
                loginAccount.setDeleteDate(result.getDeleteDate());
                // CP调用退出登录
                WAUserProxy.logout();
                WASdkDemo.getInstance().logout();
            }

            @Override
            public void onCancel() {
                showShortToast("取消");
            }

            @Override
            public void onError(int code, String message, WADeleteResult result, Throwable throwable) {
                showShortToast("错误：" + code + " , " + message);
            }
        });
    }

    /**
     * 取消账号删除申请，无UI
     */
    private void cancelDeletion() {
        WALoginResult loginAccount = WASdkDemo.getInstance().getLoginAccount();
        if (loginAccount == null || TextUtils.isEmpty(loginAccount.getUserId())) {
            showShortToast("请先尝试登录删除中的账号");
            showLoginTips();
            return;
        }
        if (loginAccount.getApplyDeleteStatus() != 1) {
            showShortToast("该账号未申请删除");
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage("该账号将在 " + loginAccount.getDeleteDate() + " 进行账号删除，是否继续取消？")
                .setPositiveButton("继续", (dialog, which) ->
                        // 申请取消账号删除
                        WAUserProxy.cancelRequestDeleteAccount(loginAccount.getUserId(), new WACallback<WAResult>() {
                            @Override
                            public void onSuccess(int code, String message, WAResult result) {
                                showLongToast("取消账号删除成功");
                            }

                            @Override
                            public void onCancel() {
                                showShortToast("取消");
                            }

                            @Override
                            public void onError(int code, String message, WAResult result, Throwable throwable) {
                                showShortToast("错误：" + code + " , " + message);
                            }
                        }))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

}