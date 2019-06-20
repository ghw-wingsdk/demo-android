package com.wa.sdk.demo;

import android.view.View;

import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.model.WAResult;
import com.wa.sdk.core.WACoreProxy;
import com.wa.sdk.demo.base.BaseGridActivity;

import static com.wa.sdk.core.WACoreProxy.showPrivacyUI;


/**
 * 测试隐私政策
 * Created by hank on 2016/1/4.
 */
public class PrivacyActivity extends BaseGridActivity {

    @Override
    protected void initViews() {
        title = R.string.privacy_and_cookie_policy;
        titles = new int[]{R.string.getUrl, R.string.getTime, R.string.popPrivacyWindow};

        super.initViews();
    }

    @Override
    public void onClick(View v) {
        int tag = (int) v.getTag();
        switch (tag) {
            case R.string.getUrl:
                showLongToast(WACoreProxy.getPrivacyUrl(this));
                break;
            case R.string.getTime:
                showLongToast(WACoreProxy.getPrivacyUpdateTime(this));
                break;
            case R.string.popPrivacyWindow:
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

}
