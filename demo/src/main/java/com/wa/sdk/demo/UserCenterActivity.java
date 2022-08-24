package com.wa.sdk.demo;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.demo.base.BaseGridActivity;
import com.wa.sdk.user.WAUserProxy;
import com.wa.sdk.user.model.WAShortUrlResult;

public class UserCenterActivity extends BaseGridActivity {
    private static final String TAG = "WA_UserCenterActivity";

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
        if (!isLoginAndTips()) return;
        showLoadingDialog("请求中...",false,false,null);
        WAUserProxy.getUserCenterNotice(this, new WACallback<WAShortUrlResult>() {
            @Override
            public void onSuccess(int code, String message, WAShortUrlResult result) {
                dismissLoadingDialog();
                String msg = "信息：" + result.getInfo()
                        + "\nUID：" + result.getUid()
                        + "\nCharacterID：" + result.getCharacterId()
                        + "\n短链：" + result.getShortUrl()                        ;
                new AlertDialog.Builder(UserCenterActivity.this)
                        .setTitle("成功")
                        .setMessage(msg)
                        .setNegativeButton("关闭", null)
                        .setPositiveButton("复制CharacterID", (dialog, which) -> {
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("text", result.getCharacterId());
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(UserCenterActivity.this,"复制成功:"+result.getCharacterId(),Toast.LENGTH_SHORT).show();
                        })
                        .show();
            }

            @Override
            public void onCancel() {
                dismissLoadingDialog();
            }

            @Override
            public void onError(int code, String message, WAShortUrlResult result, Throwable throwable) {
                dismissLoadingDialog();
                String msg = "短链获取失败：" + code + "  " + message;
                new AlertDialog.Builder(UserCenterActivity.this)
                        .setTitle("失败")
                        .setMessage(msg)
                        .setNegativeButton("确定", null)
                        .show();
            }
        });
    }

    /**
     * 显示用户中心UI
     */
    public void showUserCenterUI() {
        if (!isLoginAndTips()) return;
        WAUserProxy.showUserCenterNoticeUI(this, new WACallback<WAShortUrlResult>() {
            @Override
            public void onSuccess(int code, String message, WAShortUrlResult result) {
                // 此方法不会被调用
            }

            @Override
            public void onCancel() {
                showShortToast("窗口关闭");
            }

            @Override
            public void onError(int code, String message, WAShortUrlResult result, Throwable throwable) {
                showShortToast(message);
            }
        });
    }
}
