package com.wa.sdk.demo.rare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wa.sdk.WAConstants;
import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.utils.StringUtil;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.utils.WASdkDemo;
import com.wa.sdk.user.WAUserProxy;
import com.wa.sdk.user.model.WALoginResult;
import com.wa.sdk.user.model.WAUser;
import com.wa.sdk.wa.common.utils.ImageUtils;

/**
 * 获取账户信息
 */
public class GetAccountInfoActivity extends BaseActivity {

    private ImageView mIvAvatar;
    private TextView mTvName;
    private TextView mTvId;
    private TextView mTvPlatform;

    private String[] mAccountTypeArray = new String[]{
            WAConstants.CHANNEL_FACEBOOK,
            WAConstants.CHANNEL_GOOGLE,
            WAConstants.CHANNEL_VK,
            WAConstants.CHANNEL_TWITTER,
            WAConstants.CHANNEL_INSTAGRAM,
            WAConstants.CHANNEL_GHG,
            WAConstants.CHANNEL_R2,
            WAConstants.CHANNEL_GUEST,
            WAConstants.CHANNEL_WA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_get_account_info);

        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!WACommonProxy.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.btn_get_account_info_switch_account) {
            switchAccount();
        } else if (id == R.id.btn_get_facebook_account) {
            getAccountInfo(WAConstants.CHANNEL_FACEBOOK);
        } else if (id == R.id.btn_get_google_account) {
            getAccountInfo(WAConstants.CHANNEL_GOOGLE);
        } else if (id == R.id.btn_get_vk_account) {
            getAccountInfo(WAConstants.CHANNEL_VK);
        } else if (id == R.id.btn_get_twitter_account) {
            getAccountInfo(WAConstants.CHANNEL_TWITTER);
        } else if (id == R.id.btn_get_instagram_account) {
            getAccountInfo(WAConstants.CHANNEL_INSTAGRAM);
        }
    }

    private void initView() {
        setTitleBar(R.string.get_account_info);

        mIvAvatar = (ImageView) findViewById(R.id.iv_account_info_avatar);
        mTvName = (TextView) findViewById(R.id.tv_account_info_name);
        mTvId = (TextView) findViewById(R.id.tv_account_info_id);
        mTvPlatform = (TextView) findViewById(R.id.tv_account_info_platform);
    }

    /**
     * 切换账号
     */
    private void switchAccount() {
        new AlertDialog.Builder(this)
                .setTitle("Choose account type")
                .setItems(mAccountTypeArray, (dialog, which) -> {
                    String platform = mAccountTypeArray[which];
                    showLoadingDialog("Login", null);
                    WAUserProxy.switchAccount(GetAccountInfoActivity.this, platform, new WACallback<WALoginResult>() {
                        @Override
                        public void onSuccess(int code, String message, WALoginResult result) {
                            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                                mLoadingDialog.dismiss();
                            }
                            if (null == result) {
                                return;
                            }
                            String text = "code:" + code + "\nmessage:" + message;
                            text = "Login success->" + text
                                    + "\nplatform:" + result.getPlatform()
                                    + "\nuserId:" + result.getUserId()
                                    + "\ntoken:" + result.getToken()
                                    + "\nplatformUserId:" + result.getPlatformUserId()
                                    + "\nplatformToken:" + result.getPlatformToken();
                            WASdkDemo.getInstance().updateLoginAccount(result);
                            cancelLoadingDialog();
                            showShortToast(text);
                        }

                        @Override
                        public void onCancel() {
                            cancelLoadingDialog();
                            showLongToast("Cancel to login with google");
                        }

                        @Override
                        public void onError(int code, String message, WALoginResult result, Throwable throwable) {
                            cancelLoadingDialog();
                            showLongToast(message + "\n" + (null == throwable ? "" : throwable.getMessage()));
                        }
                    });
                })
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    private void getAccountInfo(String platform) {
        showLoadingDialog(getString(R.string.loading), null);
        mIvAvatar.setImageResource(R.drawable.ic_avatar_default);
        mTvName.setText(String.format(getString(R.string.name_format), "-"));
        mTvId.setText(String.format(getString(R.string.id_format), "-"));
        mTvPlatform.setText(String.format(getString(R.string.platform_format), "-"));
        WAUserProxy.getAccountInfo(this, platform, new WACallback<WAUser>() {
            @Override
            public void onSuccess(int code, String message, WAUser result) {
                dismissLoadingDialog();

                final String avatar = result.getPicture();
                if (!StringUtil.isEmpty(avatar)) {
//                    Picasso.get()
//                            .load(avatar)
//                            .placeholder(R.drawable.ic_avatar_default)
//                            .into(mIvAvatar);
                    ImageUtils.loadImage(GetAccountInfoActivity.this, avatar, mIvAvatar);
                }
                mTvName.setText(String.format(getString(R.string.name_format), result.getName()));
                mTvId.setText(String.format(getString(R.string.id_format), result.getId()));
                mTvPlatform.setText(String.format(getString(R.string.platform_format), result.getPlatform()));
            }

            @Override
            public void onCancel() {
                dismissLoadingDialog();

            }

            @Override
            public void onError(int code, String message, WAUser result, Throwable throwable) {
                dismissLoadingDialog();
                mTvName.setText(R.string.error_loading_data);
                mTvId.setText(code + "--" + message);
            }
        });
    }
}
