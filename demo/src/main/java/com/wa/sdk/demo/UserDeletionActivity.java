package com.wa.sdk.demo;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.model.WAResult;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.utils.WASdkDemo;
import com.wa.sdk.user.WAUserProxy;
import com.wa.sdk.user.model.WADeleteResult;
import com.wa.sdk.user.model.WALoginResultV2;

/**
 * 账号注销
 */
public class UserDeletionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_deletion);
        setTitleBar(R.string.account_deletion);

        // 判断是否已经开启账号注销功能，根据结果控制按钮显示（返回true）和隐藏（返回false）
        boolean isOpenDeleteAccount = WAUserProxy.isOpenDeleteAccount();
        Button btnRequestUi = findViewById(R.id.btn_request_deletion_ui);
        btnRequestUi.setText(btnRequestUi.getText() + (isOpenDeleteAccount ? "(显示)" : "(隐藏)"));
        btnRequestUi.setTextColor(isOpenDeleteAccount ? Color.BLACK : Color.GRAY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        WACommonProxy.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        // 常用接入（一般只接入 打开界面接口 WAUserProxy.requestDeleteAccountUI 和 按钮显示控制接口 WAUserProxy.isOpenDeleteAccount）
        if (id == R.id.btn_request_deletion_ui) {
            // 打开账号注销申请界面，内置完整的注销申请流程
            requestDeleteAccountUI();
        }

        // 其他接口功能
        if (id == R.id.btn_request_deletion) {
            // 接口申请注销账号（无UI）
            requestDeleteAccount();
        } else if (id == R.id.btn_cancel_deletion) {
            // 接口取消注销账号申请（无UI）
            cancelRequestDeleteAccount();
        }
    }

    /**
     * 打开账号注销界面，内置完整的注销申请流程
     */
    private void requestDeleteAccountUI() {
        WAUserProxy.requestDeleteAccountUI(UserDeletionActivity.this, new WACallback<WAResult>() {
            @Override
            public void onSuccess(int code, String message, WAResult result) {
                showLongToast("申请账号注销成功!\nCP需要退出游戏到登录页");
                // CP需要退出游戏到登录页
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
     * 接口申请注销账号（无UI）
     */
    private void requestDeleteAccount() {
        WAUserProxy.requestDeleteAccount(new WACallback<WADeleteResult>() {
            @Override
            public void onSuccess(int code, String message, WADeleteResult result) {
                showLongToast("申请账号注销成功\nCP需要退出游戏到登录页\n申请时间：" + result.getApplyDate() + "\n注销时间：" + result.getDeleteDate());
                WALoginResultV2 loginAccount = WASdkDemo.getInstance().getLoginAccount();
                loginAccount.setApplyDeleteStatus(1);
                loginAccount.setDeleteDate(result.getDeleteDate());
                // CP需要退出游戏到登录页
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
     * 接口取消注销账号申请（无UI）
     */
    private void cancelRequestDeleteAccount() {
        WALoginResultV2 loginAccount = WASdkDemo.getInstance().getLoginAccount();
        if (loginAccount == null || TextUtils.isEmpty(loginAccount.getUserId())) {
            showShortToast("请先尝试登录注销中的账号");
            showLoginTips();
            return;
        }
        if (loginAccount.getApplyDeleteStatus() != 1) {
            showShortToast("该账号未申请注销");
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage("该账号将在 " + loginAccount.getDeleteDate() + " 进行账号注销，是否继续取消？")
                .setPositiveButton("继续", (dialog, which) ->
                        // 申请取消账号注销
                        WAUserProxy.cancelRequestDeleteAccount(loginAccount.getUserId(), new WACallback<WAResult>() {
                            @Override
                            public void onSuccess(int code, String message, WAResult result) {
                                showLongToast("取消账号注销成功");
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