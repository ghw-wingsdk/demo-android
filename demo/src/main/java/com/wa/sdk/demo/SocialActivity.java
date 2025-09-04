package com.wa.sdk.demo;

import android.annotation.SuppressLint;
import android.app.ComponentCaller;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.social.WASocialProxy;
import com.wa.sdk.user.WAUserProxy;
import com.wa.sdk.user.model.WAPlatformAccountInfo;
import com.wa.sdk.wa.common.utils.ImageUtils;

@SuppressLint("SetTextI18n")
public class SocialActivity extends BaseActivity {

    private TextView mTvNickname;
    private TextView mTvPlatform;
    private TextView mTvPicture;
    private ImageView mIvPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);
        setTitleBar(R.string.social_function);

        mIvPicture = findViewById(R.id.iv_picture);
        mTvNickname = findViewById(R.id.tv_nickname);
        mTvPlatform = findViewById(R.id.tv_platform);
        mTvPicture = findViewById(R.id.tv_picture);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.btn_platform_account_info) {
            getPlatformAccountInfo();
        } else if (id == R.id.btn_share_invite_with_system) {
            shareInviteLink(0);
        } else if (id == R.id.btn_share_invite_with_facebook) {
            shareInviteLink(1);
        } else if (id == R.id.btn_share_count) {
            getShareFriendsCount();
        }
    }

    private void shareInviteLink(int shareType) {
        WASocialProxy.shareInviteLink(this, shareType, new WACallback<Void>() {
            @Override
            public void onSuccess(int code, String message, Void result) {
                logIShortToast("分享成功");
            }

            @Override
            public void onCancel() {
                logIShortToast("分享取消");
            }

            @Override
            public void onError(int code, String message, @Nullable Void result, @Nullable Throwable throwable) {
                logIShortToast("分享失败（" + code + "）：" + message);
            }
        });
    }

    private void getShareFriendsCount() {
        WASocialProxy.getShareFriendsCount(new WACallback<Integer>() {
            @Override
            public void onSuccess(int code, String message, Integer result) {
                logILongToast("获取成功，当前邀请人数：" + result);
            }

            @Override
            public void onCancel() {
                logILongToast("取消");
            }

            @Override
            public void onError(int code, String message, @Nullable Integer result, @Nullable Throwable throwable) {
                logILongToast("获取失败（" + code + "）：" + message);
            }
        });
    }

    private void getPlatformAccountInfo() {
        if (isNotLoginAndTips()) return;

        String picture = "";
        String nickname = "";
        String platform = "";

        WAPlatformAccountInfo accountInfo = WAUserProxy.getPlatformAccountInfo();
        if (accountInfo != null) {
            picture = accountInfo.getPicture();
            nickname = accountInfo.getNickname();
            platform = accountInfo.getPlatform();
            showShortToast("获取账号信息成功：" + accountInfo);
        } else {
            showShortToast("无法获取账号信息");
        }

        ImageUtils.loadImage(this, picture, mIvPicture);
        mTvNickname.setText("昵称：" + nickname);
        mTvPlatform.setText("平台：" + platform);
        mTvPicture.setText("头像：" + picture);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data, @NonNull ComponentCaller caller) {
        super.onActivityResult(requestCode, resultCode, data, caller);
        WACommonProxy.onActivityResult(requestCode, resultCode, data);
    }
}
