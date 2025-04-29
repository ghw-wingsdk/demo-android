package com.wa.sdk.demo;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;

import com.wa.sdk.WAConstants;
import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.WASharedPrefHelper;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.utils.LogUtil;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.user.WAUserProxy;
import com.wa.sdk.user.model.WALoginResult;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 测试Login
 *
 */
public class LoginActivity extends BaseActivity {

    private TitleBar mTitlebar;
    private EditText mEdtServerId;

    private WASharedPrefHelper mSharedPrefHelper;

    private int mResultCode = RESULT_CANCELED;

    private boolean mAutoFinish = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Demo的初始化，跟SDK无关
        WASdkDemo.getInstance().initialize(this);

        setContentView(R.layout.activity_login);
        mSharedPrefHelper = WASharedPrefHelper.newInstance(this, WADemoConfig.SP_CONFIG_FILE_DEMO);

        Intent intent = getIntent();
        if (intent.hasExtra("auto_finish")) {
            mAutoFinish = intent.getBooleanExtra("auto_finish", false);
        }

        initView();

        Bundle metaData = null;
        boolean apiKey;
        try {
            android.content.pm.ApplicationInfo ai = this.getPackageManager()
                    .getApplicationInfo(this.getPackageName(), android.content.pm.PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getBoolean("com.wa.sdk.android_ad");
                boolean a = apiKey;
            }
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
        }
    }


    @Override
    public void onBackPressed() {
        exit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        WACommonProxy.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1005 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            nowggLogin(); // nowgg 授权后，再次调用登录
            return;
        }
        if (WACommonProxy.onRequestPermissionsResult(this, requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelLoadingDialog();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_fb_login) {
            fbLogin();
        } else if (id == R.id.btn_gg_login) {
            googleLogin();
        } else if (id == R.id.btn_anonymous_login) {
            anonymousLogin();
        } else if (id == R.id.btn_winga_login) {
            wingaLogin();
        } else if (id == R.id.btn_app_login) {
            appLogin();
        } else if (id == R.id.btn_vk_login) {
            vkLogin();
        } else if (id == R.id.btn_twitter_login) {
            twitterLogin();
        } else if (id == R.id.btn_instagram_login) {
            instagramLogin();
        } else if (id == R.id.btn_huawei_hms_login) {
            huaweiHmsLogin();
        } else if (id == R.id.btn_logout) {
            logout();
        } else if (id == R.id.btn_login_form) {
            loginUi();
        } else if (id == R.id.btn_ghg_integration_login) {
            ghgIntegrationLogin();
        } else if (id == R.id.btn_r2_integration_login) {
            r2IntegrationLogin();
        } else if (id == R.id.btn_nowgg_login) {
            nowggLogin();
        } else if (id == R.id.btn_clear_login_cache) {
            WAUserProxy.clearLoginCache();
            showShortToast(R.string.clean_login_cache);
        }
    }

    private void initView() {
        mTitlebar = findViewById(R.id.tb_login);
        mTitlebar.setTitleText(R.string.login);
        mTitlebar.setLeftButton(android.R.drawable.ic_menu_revert, v -> exit());
        mTitlebar.setTitleTextColor(R.color.color_white);

        mEdtServerId = findViewById(R.id.edt_server_id);

        ToggleButton loginFlowType = findViewById(R.id.tbtn_login_flow_type);
        int flowType = WASdkDemo.getInstance().getLoginFlowType();
        WAUserProxy.setLoginFlowType(flowType);
        if (WAConstants.LOGIN_FLOW_TYPE_DEFAULT == flowType) {
            loginFlowType.setChecked(false);
        } else if (WAConstants.LOGIN_FLOW_TYPE_REBIND == flowType) {
            loginFlowType.setChecked(true);
        }
        loginFlowType.setOnCheckedChangeListener(mOnCheckedChangeListener);

        ToggleButton loginCache = findViewById(R.id.tbtn_enable_login_cache);
        loginCache.setChecked(mSharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_LOGIN_CACHE, true));
        loginCache.setOnCheckedChangeListener(mOnCheckedChangeListener);
    }


    private final CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int id = buttonView.getId();
            if (id == R.id.tbtn_login_flow_type) {
                int flowType = isChecked ? WAConstants.LOGIN_FLOW_TYPE_REBIND : WAConstants.LOGIN_FLOW_TYPE_DEFAULT;
                WASdkDemo.getInstance().setLoginFlowType(flowType);
            } else if (id == R.id.tbtn_enable_login_cache) {
                mSharedPrefHelper.saveBoolean(WADemoConfig.SP_KEY_ENABLE_LOGIN_CACHE, isChecked);
            }

        }
    };

    private final WACallback<WALoginResult> mLoginCallback = new WACallback<WALoginResult>() {
        @Override
        public void onSuccess(int code, String message, WALoginResult result) {
            String text = "code:" + code + "\nmessage:" + message;
            if (null == result) {
                text = "Login failed->" + text;
            } else {
                text = "Login success->" + text
                        + "\nplatform:" + result.getPlatform()
                        + "\nuserId:" + result.getUserId()
                        + "\ntoken:" + result.getToken()
                        + "\nplatformUserId:" + result.getPlatformUserId()
                        + "\nplatformToken:" + result.getPlatformToken()
                        + "\nisBindMobile: " + result.isBindMobile()
                        + "\nisBindAccount: " + result.getIsBindAccount()
                        + "\nisGuestAccount: " + result.getIsGuestAccount()
                        + "\nisFistLogin: " + result.isFirstLogin();

                /** 登录成功后，用户会开始进服，创角，相关的事件发送 以及 通知权限申请，具体参考 {@link MainActivity#userEnterGame(WALoginResult)} 中的处理 **/
                // MainActivity.userEnterGame(result);

                mTitlebar.setTitleText("登录(" + result.getPlatform() + ")");
                mEdtServerId.clearFocus();
            }

            logI(text);
            showLongToast(text);
            cancelLoadingDialog();
            WASdkDemo.getInstance().updateLoginAccount(result);

            mResultCode = RESULT_OK;
            if (mAutoFinish) {
                exit();
            }
        }

        @Override
        public void onCancel() {
            cancelLoadingDialog();
            LogUtil.i(LogUtil.TAG, "Login canceled");
            showLongToast("Login canceled");
        }

        @Override
        public void onError(int code, String message, WALoginResult result, Throwable throwable) {
            cancelLoadingDialog();
            String text = "code:" + code + "\nmessage:" + message;
            LogUtil.w(LogUtil.TAG, "Login failed->" + text);
            showLongToast("Login failed->" + text);
            if (code == WACallback.CODE_ACCOUNT_IN_DELETION_BUFFER_DAYS) {
                // 正在删除中的账号，会返回删除状态，删除时间，及UserID
                WASdkDemo.getInstance().updateLoginAccount(result);
                String message1 = "code:" + code
                        + "\nmessage:" + message
                        + "\nuserId:" + result.getUserId()
                        + "\ndeleteDate:" + result.getDeleteDate();
                // 正在删除中的账号，会返回删除状态，删除时间，及UserID
                WASdkDemo.getInstance().updateLoginAccount(result);
                new AlertDialog.Builder(LoginActivity.this).setMessage(message1).show();
            }
        }
    };

    public void exit() {
        setResult(mResultCode, new Intent());
        finish();
    }

    private void loginUi() {
        // WAUserProxy.loginUI() 在刚接入时需要运营在后台添加测试设备，才会显示具体登录方式，比如: Google，Facebook
        WAUserProxy.loginUI(LoginActivity.this,
                mSharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_LOGIN_CACHE, true),
                mLoginCallback);
    }

    /**
     * Facebook登陆点击
     */
    public void fbLogin() {

        showLoadingDialog("正在登陆Facebook", null);

        WAUserProxy.login(this, WAConstants.CHANNEL_FACEBOOK, mLoginCallback, null);

//        JSONObject extInfoJson = new JSONObject();
//        try {
//            extInfoJson.putOpt("permissionType", "read");
//            JSONArray permissions = new JSONArray();
//            permissions.put("public_profile");
//            permissions.put("user_friends");
//            extInfoJson.putOpt("permissions", permissions);
////            extInfoJson.putOpt("permissionTYpe", "publish");
////            JSONArray permissions = new JSONArray();
////            permissions.put("publish_actions");
////            extInfoJson.putOpt("permissions", permissions);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        WAUserProxy.login(this, WAConstants.CHANNEL_FACEBOOK, mLoginCallback, extInfoJson.toString());
    }

    /**
     * Google登陆点击
     */
    public void googleLogin() {
        showLoadingDialog("正在登陆Google", null);
        WAUserProxy.login(this, WAConstants.CHANNEL_GOOGLE, mLoginCallback, null);
    }

    /**
     * 匿名登录点击
     */
    public void anonymousLogin() {
        showLoadingDialog("正在匿名登录", null);
        WAUserProxy.login(this, WAConstants.CHANNEL_GUEST, mLoginCallback, null);
    }

    /**
     * VK平台登录
     */
    public void vkLogin() {
        showLoadingDialog("正在登录VK", null);
        WAUserProxy.login(LoginActivity.this, WAConstants.CHANNEL_VK, mLoginCallback, null);
    }

    /**
     * Twitter平台登录
     */
    public void twitterLogin() {
        showLoadingDialog("正在登录Twitter", null);
        WAUserProxy.login(this, WAConstants.CHANNEL_TWITTER, mLoginCallback, null);
    }

    /**
     * Instagram平台登录
     */
    public void instagramLogin() {
        showLoadingDialog("正在登录Instagram", null);
        WAUserProxy.login(this, WAConstants.CHANNEL_INSTAGRAM, mLoginCallback, null);
    }

    /**
     * 华为hms平台登录
     */
    public void huaweiHmsLogin() {
        showLoadingDialog("正在登录华为Hms", null);
        WAUserProxy.login(this, WAConstants.CHANNEL_HUAWEI_HMS, mLoginCallback, null);
    }

    /**
     * GHG集成登录
     */
    private void ghgIntegrationLogin() {
        showLoadingDialog("正在登录GHG", null);
        WAUserProxy.login(this, WAConstants.CHANNEL_GHG, mLoginCallback, null);
    }

    /**
     * GHG集成登录
     */
    private void r2IntegrationLogin() {
        showLoadingDialog("正在登录R2", null);
        WAUserProxy.login(this, WAConstants.CHANNEL_R2, mLoginCallback, null);
    }

    /**
     * Nowgg登录
     */
    private void nowggLogin() {
        showLoadingDialog("正在登录Nowgg", null);
        WAUserProxy.login(this, WAConstants.CHANNEL_NOWGG, mLoginCallback, null);
    }

    /**
     * 应用内登录
     */
    public void appLogin() {
        JSONObject extObject = new JSONObject();
        try {
            extObject.putOpt("appSelfLogin", true);
            extObject.putOpt("puserId", "12345");
            extObject.putOpt("accessToken", "o1akkfjia81FMvFSO8kxC96TgQYlhEEr");
            extObject.putOpt("extInfo", "extInfo String");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showLoadingDialog("应用内登录", null);
        WAUserProxy.login(this, WAConstants.CHANNEL_APPSELF, mLoginCallback, extObject.toString());
//        WAUserProxy.login(this, "APPSELF", mLoginCallback, extObject.toString());
    }

    private void wingaLogin() {
        showLoadingDialog("正在登录" + WAConstants.CHANNEL_WA, null);
        WAUserProxy.login(this, WAConstants.CHANNEL_WA, mLoginCallback, "拓展参数");
    }

    /**
     * 登出点击
     */
    public void logout() {
        showLongToast("Logout");
        WAUserProxy.logout();
        mTitlebar.setTitleText("登录");
        WASdkDemo.getInstance().logout();
//        WALoginResult loginAccount = WASdkDemo.getInstance().getLoginAccount();
//        if(null == loginAccount) {
//            return;
//        }
//        Map<String, Object> eventValues = new HashMap<>();
//        eventValues.put(GhwParameterName.USER_ID,  loginAccount.getUserId());
//        eventValues.put(GhwParameterName.SERVER_ID, "165");
//        eventValues.put(GhwParameterName.LEVEL, 0);
//
//        GhwTrackingSDK.track(this, new GhwEvent.Builder().setDefaultEventName("logout")
//                .setDefaultEventValues(eventValues).build());
//
//        GhwSdkDemo.getInstance().updateLoginAccount(null);

    }


}
