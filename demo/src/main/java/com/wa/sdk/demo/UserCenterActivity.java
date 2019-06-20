package com.wa.sdk.demo;

import android.view.View;

import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.demo.base.BaseGridActivity;
import com.wa.sdk.user.WAUserProxy;
import com.wa.sdk.user.model.WAUserCenterResult;

public class UserCenterActivity extends BaseGridActivity {
    private static final String TAG = "UserCenterActivity";

    @Override
    protected void initViews() {
        title = R.string.user_center;
        titles = new int[]{R.string.get_user_center_data, R.string.show_user_center_ui};

        super.initViews();
    }

    @Override
    public void onClick(View v) {
        int tag = (int) v.getTag();

        if (tag == R.string.get_user_center_data) {
            getUserCenterData();
        } else if (tag == R.string.show_user_center_ui) {
            showUserCenterUI();
        }
    }

    /**
     * 获取用户中心数据
     */
    public void getUserCenterData() {
        WAUserProxy.getUserCenterNotice(new WACallback<WAUserCenterResult>() {
            @Override
            public void onSuccess(int code, String message, WAUserCenterResult result) {
                showShortToast(result.toString());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(int code, String message, WAUserCenterResult result, Throwable throwable) {
                showShortToast(message);
            }
        });
    }

    /**
     * 显示用户中心UI
     */
    public void showUserCenterUI() {
        WAUserProxy.showUserCenterNoticeUI(this, new WACallback<WAUserCenterResult>() {
            @Override
            public void onSuccess(int code, String message, WAUserCenterResult result) {
                // 此方法不会被调用
            }

            @Override
            public void onCancel() {
                showShortToast("窗口关闭");
            }

            @Override
            public void onError(int code, String message, WAUserCenterResult result, Throwable throwable) {
                showShortToast(message);
            }
        });
    }
}
