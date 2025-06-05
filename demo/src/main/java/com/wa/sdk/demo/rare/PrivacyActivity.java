package com.wa.sdk.demo.rare;

import static com.wa.sdk.core.WACoreProxy.showPrivacyUI;

import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.model.WAResult;
import com.wa.sdk.core.WACoreProxy;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.base.BaseGridActivity;


/**
 * 测试隐私政策
 */
public class PrivacyActivity extends BaseGridActivity {

    @Override
    protected int definedTitleResId() {
        return R.string.privacy_policy;
    }

    @Override
    protected int[] definedButtonResIds() {
        return new int[]{R.string.getUrl, R.string.getTime, R.string.popPrivacyWindow};
    }

    @Override
    protected void onClickButton(int textResId) {
        if (textResId == R.string.getUrl) {
            showLongToast(WACoreProxy.getPrivacyUrl(this));
        } else if (textResId == R.string.getTime) {
            showLongToast(WACoreProxy.getPrivacyUpdateTime(this));
        } else if (textResId == R.string.popPrivacyWindow) {
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
