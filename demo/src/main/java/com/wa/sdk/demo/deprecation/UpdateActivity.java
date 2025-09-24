package com.wa.sdk.demo.deprecation;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.hup.WAHupProxy;
import com.wa.sdk.hup.model.WAUpdateInfo;

@Deprecated
public class UpdateActivity extends BaseActivity {

    WAUpdateInfo mUpdateInfo;

    private TextView mTvUpdateLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        setTitleBar(R.string.update);

        mTvUpdateLink = (TextView) findViewById(R.id.tv_update_link);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.btn_check_hot_update) {
            checkHotUpdate();
        } else if (id == R.id.btn_download_patch) {//                downloadPatch();
            showShortToast("检查更新后自动下载，无需手动调用下载");
//                File file = Environment.getExternalStorageDirectory();
//                WAUpdateInfo info = new WAUpdateInfo();
//                info.setDownloadUrl(new File(file, "patch_v1.apatch").getPath());
//                WAHupProxy.startUpdate(info, null);
        } else if (id == R.id.btn_get_update_link) {
            getUpdateLink();
        }
    }

    public void checkHotUpdate() {
        WAHupProxy.checkUpdate();
    }

    public void downloadPatch() {
        WAHupProxy.startUpdate(mUpdateInfo, new WACallback<String>() {
            @Override
            public void onSuccess(int code, String message, String result) {
                String text = "Update succeed! patch file: " + result;
                logD(text);
                showLongToast(text);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(int code, String message, String result, Throwable throwable) {
                String text = "Download patch file failed: " + (null == result ? "" : result) + "\n"
                        + (null == throwable ? "" : getStackTrace(throwable));
                logE(text);
                showLongToast(text);
            }
        });
    }

    public void getUpdateLink() {
        showLoadingDialog(getString(R.string.loading), null);
        WACommonProxy.getAppUpdateLink(new WACallback<String>() {
            @Override
            public void onSuccess(int code, String message, String result) {
                mTvUpdateLink.setText("Result: " + result);
                cancelLoadingDialog();
            }

            @Override
            public void onCancel() {
                cancelLoadingDialog();

            }

            @Override
            public void onError(int code, String message, String result, Throwable throwable) {
                mTvUpdateLink.setText("Error: \ncode: " + code + "\nmessage:" + message);
                cancelLoadingDialog();
            }
        });
    }
}
