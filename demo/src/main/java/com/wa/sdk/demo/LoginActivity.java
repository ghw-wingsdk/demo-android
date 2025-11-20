package com.wa.sdk.demo;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;

import com.wa.sdk.WAConstants;
import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.utils.WADemoConfig;
import com.wa.sdk.demo.utils.WASdkDemo;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.user.WAUserProxy;
import com.wa.sdk.user.model.WALoginResult;
import com.wa.sdk.user.model.WALoginResultV2;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 登录
 */
public class LoginActivity extends BaseActivity {

    private TitleBar mTitlebar;
    private boolean mAutoFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAutoFinish = getIntent().getBooleanExtra("auto_finish", false);

        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        WACommonProxy.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1005 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loginPlatform(WAConstants.CHANNEL_NOWGG); // nowgg 授权后，再次调用登录
            return;
        }
        if (WACommonProxy.onRequestPermissionsResult(this, requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        // 常用接入（一般登录接入只需 loginUI 和 logout 即可）
        if (id == R.id.btn_login_form) {
            // SDK 登录，内置登录方式选择界面，和隐私协议显示（在刚接入时需要运营在后台添加测试设备，才会显示 Google、Facebook 等登录方式）
            WAUserProxy.loginUIV2(LoginActivity.this, isEnableCache(), mLoginCallback);
        } else if (id == R.id.btn_logout) {
            // SDK 登出
            WAUserProxy.logout();
            logoutDemo(); // 登出 Demo，与SDK无关
        }

        // 登录接口登录（直接指定登录方式登录）
        if (id == R.id.btn_gg_login) {
            // Google 登录
            loginPlatform(WAConstants.CHANNEL_GOOGLE);
        } else if (id == R.id.btn_fb_login) {
            // Facebook 登录
            loginPlatform(WAConstants.CHANNEL_FACEBOOK);
        } else if (id == R.id.btn_anonymous_login) {
            // 游客Guest 登录
            loginPlatform(WAConstants.CHANNEL_GUEST);
        } else if (id == R.id.btn_winga_login) {
            // 手机验证码、邮箱密码登录
            loginPlatform(WAConstants.CHANNEL_WA);
        } else if (id == R.id.btn_ghg_integration_login) {
            // GHG 登录
            loginPlatform(WAConstants.CHANNEL_GHG);
        } else if (id == R.id.btn_r2_integration_login) {
            // R2 登录
            loginPlatform(WAConstants.CHANNEL_R2);
        } else if (id == R.id.btn_nowgg_login) {
            // Nowgg 登录
            loginPlatform(WAConstants.CHANNEL_NOWGG);
        } else if (id == R.id.btn_qoo_login) {
            // qoo 登录
            loginPlatform(WAConstants.CHANNEL_QOOAPP);
        }

        // 其他接口功能
        if (id == R.id.btn_clear_login_cache) {
            // 清除登录缓存，下一次调用loginUI时会显示登录方式选择界面，而不是直接自动登录
            WAUserProxy.clearLoginCache();
            showShortToast(R.string.clean_login_cache);
        }

        // 不常用登录
        if (id == R.id.btn_app_login) {
            // 应用内登录
            appLogin();
        } else if (id == R.id.btn_huawei_hms_login) {
            // 华为HMS登录
            loginPlatform(WAConstants.CHANNEL_HUAWEI_HMS);
        } else if (id == R.id.btn_vk_login) {
            // VK 登录
            loginPlatform(WAConstants.CHANNEL_VK);
        } else if (id == R.id.btn_twitter_login) {
            // twitter 登录
            loginPlatform(WAConstants.CHANNEL_TWITTER);
        } else if (id == R.id.btn_instagram_login) {
            // Instagram 登录
            loginPlatform(WAConstants.CHANNEL_INSTAGRAM);
        }
    }

    private void initView() {
        mTitlebar = findViewById(R.id.tb_login);
        setTitleBar(R.string.login);

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
        loginCache.setChecked(isEnableCache());
        loginCache.setOnCheckedChangeListener(mOnCheckedChangeListener);
    }


    private final CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = (buttonView, isChecked) -> {
        int id = buttonView.getId();
        if (id == R.id.tbtn_login_flow_type) {
            int flowType = isChecked ? WAConstants.LOGIN_FLOW_TYPE_REBIND : WAConstants.LOGIN_FLOW_TYPE_DEFAULT;
            WASdkDemo.getInstance().setLoginFlowType(flowType);
        } else if (id == R.id.tbtn_enable_login_cache) {
            getSpHelper().saveBoolean(WADemoConfig.SP_KEY_ENABLE_LOGIN_CACHE, isChecked);
        }
    };

    private final WACallback<WALoginResultV2> mLoginCallback = new WACallback<WALoginResultV2>() {
        @Override
        public void onSuccess(int code, String message, WALoginResultV2 result) {
            String text = "code:" + code + "\nmessage:" + message;
            if (null == result) {
                text = "Login failed->" + text;
            } else {
                text = "Login success->" + text
                        + "\nplatform:" + result.getPlatform()
                        + "\nuserId:" + result.getUserId()
                        + "\ntoken:" + result.getToken()
                        + "\nisBindMobile: " + result.isBindMobile()
                        + "\nisBindAccount: " + result.getIsBindAccount()
                        + "\nisGuestAccount: " + result.getIsGuestAccount()
                        + "\nisFistLogin: " + result.isFirstLogin();

                /** 登录成功后，用户会开始进服，创角，相关的事件发送 以及 通知权限申请，具体参考 {@link MainActivity#userEnterGame(WALoginResult)} 中的处理 **/
                // MainActivity.userEnterGame(result);

                mTitlebar.setTitleText("登录(" + result.getPlatform() + ")");
            }

            logI(text);
            showLongToast(text);
            cancelLoadingDialog();
            WASdkDemo.getInstance().updateLoginAccount(result);

            if (mAutoFinish) finish();
        }

        @Override
        public void onCancel() {
            cancelLoadingDialog();
            logI("Login canceled");
            showLongToast("Login canceled");
        }

        @Override
        public void onError(int code, String message, WALoginResultV2 result, Throwable throwable) {
            cancelLoadingDialog();
            String text = "code:" + code + "\nmessage:" + message;
            logW("Login failed->" + text);
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

    /**
     * Facebook登录点击
     */
    public void fbLogin() {

        showLoadingDialog("正在登录Facebook", null);

        WAUserProxy.loginV2(this, WAConstants.CHANNEL_FACEBOOK, mLoginCallback, null);

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
//        WAUserProxy.loginV2(this, WAConstants.CHANNEL_FACEBOOK, mLoginCallback, extInfoJson.toString());
    }

    /**
     * 应用内登录
     */
    public void appLogin() {
        // 额外参数
        JSONObject extObject = new JSONObject();
        try {
            extObject.putOpt("appSelfLogin", true);
            extObject.putOpt("puserId", "12345");  // CP用户ID
            extObject.putOpt("accessToken", "o1akkfjia81FMvFSO8kxC96TgQYlhEEr"); // CP用户token
            extObject.putOpt("extInfo", "extInfo String"); // 透传参数
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loginPlatform(WAConstants.CHANNEL_APPSELF, extObject.toString());
    }

    /**
     * 指定平台登录
     *
     * @param platform 平台，比如Google则传入{@link WAConstants#CHANNEL_GOOGLE}
     */
    private void loginPlatform(String platform) {
        loginPlatform(platform, null);
    }

    /**
     * 指定平台登录
     *
     * @param platform 平台，比如Google则传入{@link WAConstants#CHANNEL_GOOGLE}
     * @param extra    拓展参数，一般传入null即可，只有Facebook自定义权限登录、应用内登录等特别登录时才需要传入特定内容
     */
    private void loginPlatform(String platform, String extra) {
        showLoadingDialog("正在登录 " + platform + " ...", null);
        WAUserProxy.loginV2(this, platform, mLoginCallback, extra);
    }

    /**
     * 退出 Demo，与 SDK 无关
     */
    public void logoutDemo() {
        showLongToast("Logout");
        mTitlebar.setTitleText("登录");
        WASdkDemo.getInstance().logout();
    }

    /**
     * 是否启用缓存登录，一般传入 true 即可
     */
    private boolean isEnableCache() {
        return getSpHelper().getBoolean(WADemoConfig.SP_KEY_ENABLE_LOGIN_CACHE, true);
    }
}
