package com.wa.sdk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.csc.WACscProxy;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.widget.TitleBar;

import java.util.HashMap;


/**
 * 测试客服系統
 * 按照說明對應著來
 */
public class CscActivity extends BaseActivity {

    private TitleBar mTitlebar;
    private String mLanguage = "";
    private final HashMap<String, Object> customData = new HashMap<>();
    private EditText mEdtCustomKey;
    private EditText mEdtCustomValue;
    private TextView mTvCustomData;
    private TextView mTvLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Demo的初始化，跟SDK无关
        WASdkDemo.getInstance().initialize(this);

        setContentView(R.layout.activity_csc);

        mTitlebar = findViewById(R.id.tb_csc);
        mTitlebar.setTitleText(R.string.csc);
        mEdtCustomKey = findViewById(R.id.edt_custom_key);
        mEdtCustomValue = findViewById(R.id.edt_custom_value);
        mTvCustomData = findViewById(R.id.tv_custom_data);
        mTvLanguage = findViewById(R.id.tv_language);
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
        } else if (id == R.id.btn_add_custom_data) { //添加自定义参数
            addCustomParams();
        } else if (id == R.id.btn_reset_custom_data) { //重置自定义参数
            customData.clear();
            mTvCustomData.setText(null);
        } else if (id == R.id.btn_switch_language) { //切换语言
            switchLanguage();
        }
    }

    private void switchLanguage() {
        if ("zh-CN".equals(mLanguage)) {
            mLanguage = "en";
        } else if ("en".equals(mLanguage)) {
            mLanguage = "pt";
        } else {
            mLanguage = "zh-CN";
        }
        mTvLanguage.setText("当前语言："+mLanguage);
        WACscProxy.setSDKLanguage(mLanguage);
    }

    private void addCustomParams() {
        String key = mEdtCustomKey.getText().toString().trim();
        String value = mEdtCustomValue.getText().toString().trim();
        if (TextUtils.isEmpty(key)) {
            showShortToast("Key不能为空");
            return;
        }
        customData.put(key, value);
        mTvCustomData.setText(customData.toString());
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
