package com.wa.sdk.demo.rare;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wa.sdk.cmp.WACmpProxy;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.model.WAResult;
import com.wa.sdk.core.WACoreProxy;
import com.wa.sdk.demo.AccountManagerActivity;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.UserCenterActivity;
import com.wa.sdk.demo.UserDeletionActivity;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.deprecation.DeprecationFunctionActivity;
import com.wa.sdk.user.WAUserProxy;

/**
 * 使用频率较低的功能
 */
public class RareFunctionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rare_function);
        setTitleBar(R.string.rare_function);

        // 显示或隐藏 Consent同意设置 按钮
        WACmpProxy.checkConsentPreferences(new WACallback<Boolean>() {
            @Override
            public void onSuccess(int code, String message, Boolean isShow) {
                // 返回 true，则需要显示 Consent同意设置 按钮；false，则否；
                Button btn = findViewById(R.id.btn_show_consent_preferences);
                btn.setTextColor(isShow ? Color.BLACK : Color.GRAY);
                btn.setText(btn.getText() + (isShow ? "(显示)" : "(隐藏)"));
            }

            @Override
            public void onCancel() {
                // 忽略，无需处理
            }

            @Override
            public void onError(int code, String message, Boolean result, Throwable throwable) {
                // 忽略，无需处理
            }
        });

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_open_review) {
            // Google评价
            openGoogleReview();
        } else if (id == R.id.btn_show_open_url) {
            // 打开链接
            WACoreProxy.showOpenUrl(this, new WACallback<WAResult>() {
                @Override
                public void onSuccess(int code, String message, WAResult result) {
                    showShortToast("打开链接成功");
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(int code, String message, WAResult result, Throwable throwable) {
                    showShortToast("打开链接失败：" + code + "," + message);
                }
            });
        } else if (id == R.id.btn_privacy) {
            // 隐私政策
            startActivity(new Intent(this, PrivacyActivity.class));
        } else if (id == R.id.btn_game_service) {
            // 游戏服务（Google）
            startActivity(new Intent(this, GoogleGameActivity.class));
        } else if (id == R.id.btn_account_manager) {
            // 账号管理
            if (isNotLoginAndTips()) return;
            startActivity(new Intent(this, AccountManagerActivity.class));
        } else if (id == R.id.btn_account_deletion) {
            // 账号删除
            if (isNotLoginAndTips()) return;
            startActivity(new Intent(this, UserDeletionActivity.class));
        } else if (id == R.id.btn_user_center) {
            // 用户中心
            startActivity(new Intent(this, UserCenterActivity.class));
        } else if (id == R.id.btn_show_consent_preferences) {
            // Consent同意设置
            WACmpProxy.showConsentPreferences(this);
        }

        if (id == R.id.btn_deprecation_function) {
            // 已经废弃的功能
            startActivity(new Intent(this, DeprecationFunctionActivity.class));
        }
    }

    /**
     * 调起Google评分
     * <br>注意：
     * <br>1.由于Google限制，我们无法知道用户是否已经评分，是否弹有出评分界面，onSuccess()仅代表Google评分接口调用成功
     * <br>2.如果需要评价完成给用户发放奖励，在 onSuccess()回调中处理即可
     */
    private void openGoogleReview() {
        WAUserProxy.openReview(this, new WACallback<Boolean>() {
            @Override
            public void onSuccess(int code, String message, Boolean result) {
                //不管回掉结果是什么，都需要统一当成成功处理后续逻辑
                showShortToast("api调用流程已经完成，无法获取用户是否评分，是否弹出评分框," + message);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(int code, String message, Boolean result, Throwable throwable) {


            }
        });
    }


}