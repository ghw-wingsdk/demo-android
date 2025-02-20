package com.wa.sdk.demo;

import static com.wa.sdk.core.WACoreProxy.showPrivacyUI;

import android.view.View;

import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.model.WAResult;
import com.wa.sdk.core.WACoreProxy;
import com.wa.sdk.demo.base.BaseGridActivity;


/**
 * 测试隐私政策
 * 
 */
public class PrivacyActivity extends BaseGridActivity {

    @Override
    protected void initViews() {
        title = com.wa.sdk.wa.R.string.privacy_and_cookie_policy;
        titles = new int[]{R.string.getUrl, R.string.getTime, R.string.popPrivacyWindow};

        super.initViews();
    }

    @Override
    public void onClick(View v) {
        int tag = (int) v.getTag();
        if (tag == R.string.getUrl) {
            showLongToast(WACoreProxy.getPrivacyUrl(this));
        } else if (tag == R.string.getTime) {
            showLongToast(WACoreProxy.getPrivacyUpdateTime(this));
        } else if (tag == R.string.popPrivacyWindow) {
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
        }
    }

}
