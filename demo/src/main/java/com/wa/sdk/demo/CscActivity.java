package com.wa.sdk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.csc.WACscProxy;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.widget.TitleBar;


/**
 * 测试客服系統
 * 按照說明對應著來
 */
public class CscActivity extends BaseActivity {

    private TitleBar mTitlebar;
    private String mLanguage = "";
    private TextView mTvLanguage;
    private TextView mTvUnreadMessageCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Demo的初始化，跟SDK无关
        WASdkDemo.getInstance().initialize(this);
        setContentView(R.layout.activity_csc);

        mTitlebar = findViewById(R.id.tb_csc);
        mTitlebar.setTitleText(R.string.csc);
        mTvLanguage = findViewById(R.id.tv_language);
        mTvUnreadMessageCount = findViewById(R.id.tv_unread_message_count);
        mTitlebar.setLeftButton(android.R.drawable.ic_menu_revert, v -> exit());
        mTitlebar.setTitleTextColor(R.color.color_white);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_openAiHelp) {
            if (WACscProxy.isOpenAiHelp())
                WACscProxy.openAiHelpV2();
        } else if (id == R.id.btn_isOpenAiHelp) {
            showLongToast(WACscProxy.isOpenAiHelp() ? "已开启" : "未开启");
        } else if (id == R.id.btn_isOpenGameReviewAiHelp) {
            showLongToast(WACscProxy.isOpenGameReviewAiHelp() ? "已开启" : "未开启");
        } else if (id == R.id.btn_openGameReviewAiHelp) {
            if (WACscProxy.isOpenGameReviewAiHelp())
                WACscProxy.openGameReviewAiHelp();
        } else if (id == R.id.btn_switch_language) {
            switchLanguage();
        } else if (id == R.id.btn_get_unread_message_count) {
            getUnreadMessageCount();
        }
    }

    private void getUnreadMessageCount() {
        WACscProxy.getUnreadMessageCount(new WACallback<Integer>() {
            @Override
            public void onSuccess(int code, String message, Integer result) {
                mTvUnreadMessageCount.setText("未读消息数:" + result);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(int code, String message, Integer result, Throwable throwable) {
                showLongToast(message);
            }
        });
    }

    private void switchLanguage() {
        if ("zh-CN".equals(mLanguage)) {
            mLanguage = "en";
        } else if ("en".equals(mLanguage)) {
            mLanguage = "pt";
        } else {
            mLanguage = "zh-CN";
        }
        mTvLanguage.setText("当前语言：" + mLanguage);
        WACscProxy.setSDKLanguage(mLanguage);
    }

    public void exit() {
        finish();
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        WACommonProxy.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (WACommonProxy.onRequestPermissionsResult(this, requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelLoadingDialog();
    }

}
