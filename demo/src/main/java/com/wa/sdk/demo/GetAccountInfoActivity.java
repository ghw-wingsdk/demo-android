package com.wa.sdk.demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wa.sdk.WAConstants;
import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.utils.StringUtil;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.user.WAUserProxy;
import com.wa.sdk.user.model.WALoginResult;
import com.wa.sdk.user.model.WAUser;

/**
 * 获取账户信息
 * Created by yunying on 2016/7/27.
 */
public class GetAccountInfoActivity extends BaseActivity {

    private ImageView mIvAvatar;
    private TextView mTvName;
    private TextView mTvId;
    private TextView mTvPlatform;

    private String [] mAccountTypeArray = new String [] {"Facebook", "Google", "VK", "Twitter", "Instagram", };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_get_account_info);

        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!WACommonProxy.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_get_account_info_switch_account:
                switchAccount();
                break;
            case R.id.btn_get_facebook_account:
                getAccountInfo(WAConstants.CHANNEL_FACEBOOK);
                break;
            case R.id.btn_get_google_account:
                getAccountInfo(WAConstants.CHANNEL_GOOGLE);
                break;
            case R.id.btn_get_vk_account:
                getAccountInfo(WAConstants.CHANNEL_VK);
                break;
            case R.id.btn_get_twitter_account:
                getAccountInfo(WAConstants.CHANNEL_TWITTER);
                break;
            case R.id.btn_get_instagram_account:
                getAccountInfo(WAConstants.CHANNEL_INSTAGRAM);
                break;
            default:
                break;
        }
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.tb_get_account_info);
        titleBar.setTitleText(R.string.get_account_info);
        titleBar.setLeftButton(android.R.drawable.ic_menu_revert, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.setTitleTextColor(R.color.color_white);

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
                .setItems(mAccountTypeArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String platform = WAConstants.CHANNEL_WA;
                        switch (which) {
                            case 0:
                                platform = WAConstants.CHANNEL_FACEBOOK;
                                break;
                            case 1:
                                platform = WAConstants.CHANNEL_GOOGLE;
                                break;
                            case 2:
                                platform = WAConstants.CHANNEL_VK;
                                break;
                            case 3:
                                platform = WAConstants.CHANNEL_TWITTER;
                                break;
                            case 4:
                                platform = WAConstants.CHANNEL_INSTAGRAM;
                                break;
                            default:
                                break;
                        }
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
                                Toast.makeText(GetAccountInfoActivity.this, "Cancel to login with google", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onError(int code, String message, WALoginResult result, Throwable throwable) {
                                cancelLoadingDialog();
                                Toast.makeText(GetAccountInfoActivity.this, message + "\n"
                                        + (null == throwable ? "" : throwable.getMessage()), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
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
                if(!StringUtil.isEmpty(avatar)) {
                    Picasso.with(GetAccountInfoActivity.this)
                            .load(avatar)
                            .placeholder(R.drawable.ic_avatar_default)
                            .into(mIvAvatar);
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
