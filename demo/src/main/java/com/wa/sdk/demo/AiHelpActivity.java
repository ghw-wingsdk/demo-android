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

/**
 * AiHelp客服
 */
public class AiHelpActivity extends BaseActivity {

    private String mLanguage = "";
    private TextView mTvLanguage;
    private TextView mTvUnreadMessageCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csc);
        setTitleBar(R.string.csc);
        mTvLanguage = findViewById(R.id.tv_language);
        mTvUnreadMessageCount = findViewById(R.id.tv_unread_message_count);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        // 常用接入（一般只接入 isOpenAiHelp 和 openAiHelpV2 即可）
        if (id == R.id.btn_openAiHelp) {
            if (WACscProxy.isOpenAiHelp())
                WACscProxy.openAiHelpV2();
        }

        // 其他接口功能
        if (id == R.id.btn_isOpenAiHelp) {
            // 判断是否已开启AiHelp
            showLongToast(WACscProxy.isOpenAiHelp() ? "已开启" : "未开启");
        } else if (id == R.id.btn_isOpenGameReviewAiHelp) {
            // 判断是否已开启游戏评价客服入口
            showLongToast(WACscProxy.isOpenGameReviewAiHelp() ? "已开启" : "未开启");
        } else if (id == R.id.btn_openGameReviewAiHelp) {
            // 打开游戏评价客服入口
            if (WACscProxy.isOpenGameReviewAiHelp())
                WACscProxy.openGameReviewAiHelp();
        } else if (id == R.id.btn_switch_language) {
            // 切换语言
            switchLanguage();
        } else if (id == R.id.btn_get_unread_message_count) {
            // 获取未读消息数
            getUnreadMessageCount();
        }
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

    /**
     * 获取未读消息数
     */
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

    /**
     * 切换语言
     */
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
}
