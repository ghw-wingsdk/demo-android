package com.wa.sdk.demo;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.demo.base.BaseGridActivity;
import com.wa.sdk.user.WAUserProxy;
import com.wa.sdk.user.model.WAShortUrlResult;

/**
 * 用户中心
 */
public class UserCenterActivity extends BaseGridActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 判断是否已经开启用户中心功能，根据结果控制按钮显示（返回true）和隐藏（返回false）
        boolean isOpenUserCenter = WAUserProxy.isOpenUserCenter();
        Button btnUserCenter = getButton(R.string.show_user_center_ui);
        btnUserCenter.setText(btnUserCenter.getText() + (isOpenUserCenter ? "(显示)" : "(隐藏)"));
        btnUserCenter.setTextColor(isOpenUserCenter ? Color.BLACK : Color.GRAY);
    }

    @Override
    protected int definedTitleResId() {
        return R.string.user_center;
    }

    @Override
    protected int[] definedButtonResIds() {
        return new int[]{R.string.show_user_center_ui, R.string.get_user_center_data};
    }

    @Override
    protected void onClickButton(int textResId) {
        // 常用接入（一般只接入 打开界面接口 WAUserProxy.showUserCenterNoticeUI 和 按钮显示控制接口 WAUserProxy.isOpenUserCenter）
        if (textResId == R.string.show_user_center_ui) {
            // 打开用户中心界面，内置 UID（SDK用户ID） 和 CID 显示和复制功能
            showUserCenterNoticeUI();
        }

        // 其他接口功能
        if (textResId == R.string.get_user_center_data) {
            // 获取用户中心相关数据，包括 UID 和 CID 等
            getUserCenterData();
        }
    }

    /**
     * 打开用户中心界面
     */
    public void showUserCenterNoticeUI() {
        if (isNotLoginAndTips()) return;
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

    /**
     * 获取用户中心数据
     */
    public void getUserCenterData() {
        if (isNotLoginAndTips()) return;
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
                            showShortToast("复制成功:"+result.getCharacterId());
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

}
