package com.wa.sdk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.model.WAResult;
import com.wa.sdk.core.WACoreProxy;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.widget.TitleBar;

import static com.wa.sdk.core.WACoreProxy.showPrivacyUI;


/**
 * 测试Login
 * Created by yinglovezhuzhu@gmail.com on 2016/1/4.
 */
public class PrivacyActivity extends BaseActivity {

    private TitleBar mTitlebar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Demo的初始化，跟SDK无关
        WASdkDemo.getInstance().initialize(this);

        setContentView(R.layout.activity_privacy);
        initView();
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelLoadingDialog();
    }

    String showConversationFlag = "1";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_url:
                showLongToast(WACoreProxy.getPrivacyUrl(this));
                break;
            case R.id.btn_get_privacy_time:
                showLongToast(WACoreProxy.getPrivacyUpdateTime(this));
                break;
            case R.id.btn_show_pops:
                showPrivacyUI(this, new WACallback<WAResult>() {
                    @Override
                    public void onSuccess(int code, String message, WAResult result) {

                    }

                    @Override
                    public void onCancel() {
                        showLongToast("关闭窗口");
                    }

                    @Override
                    public void onError(int code, String message, WAResult result, Throwable throwable) {

                    }
                });
                break;

            default:
                break;
        }
    }

    private void initView() {
        mTitlebar = (TitleBar) findViewById(R.id.tb_csc);
        mTitlebar.setTitleText(R.string.privacy_and_cookie_policy);
        mTitlebar.setLeftButton(android.R.drawable.ic_menu_revert, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });
        mTitlebar.setTitleTextColor(R.color.color_white);
    }

    public void exit() {
        finish();
    }

}
