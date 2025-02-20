package com.wa.sdk.demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;

import com.wa.sdk.WAConstants;
import com.wa.sdk.ad.WAAdProxy;
import com.wa.sdk.ad.model.WAAdCachedCallback;
import com.wa.sdk.cmp.WACmpProxy;
import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.WASharedPrefHelper;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.model.WAResult;
import com.wa.sdk.common.utils.LogUtil;
import com.wa.sdk.common.utils.StringUtil;
import com.wa.sdk.core.WACoreProxy;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.base.FlavorApiHelper;
import com.wa.sdk.demo.game.GameServiceActivity;
import com.wa.sdk.demo.tracking.TrackingSimulateActivity;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.pay.WAPayProxy;
import com.wa.sdk.pay.model.WAPurchaseResult;
import com.wa.sdk.user.WAUserProxy;
import com.wa.sdk.user.model.WAGameReviewCallback;
import com.wa.sdk.user.model.WALoginResult;

import java.util.Set;
import java.util.UUID;


public class MainActivity extends BaseActivity {
    private WASharedPrefHelper mSharedPrefHelper;
    private PendingAction mPendingAction = PendingAction.NONE;
    private EditText mEtSkuId;
    private EditText mEdtClientId;
    private TextView mTvScreenOrientation;

    private boolean mPayInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setScreenOrientation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Demo的初始化，跟SDK无关
        WASdkDemo.getInstance().initialize(this);
        mSharedPrefHelper = WASharedPrefHelper.newInstance(this, WADemoConfig.SP_CONFIG_FILE_DEMO);
        initView();

        boolean isEnableAppOpenAd = mSharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_APP_OPEN_AD, FlavorApiHelper.AdMob.getSettingEnable("DEFAULT_APP_OPEN_AD_STATE"));
        // 避免与开屏页重复初始化
        if (isEnableAppOpenAd) {
            doAfterInitSuccess();
            return;
        }

        // AdMob强制测试
        FlavorApiHelper.AdMob.setTest(this);
        // 开启日志
        WACoreProxy.setDebugMode(mSharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_DEBUG, true));
        showLoadingDialog("初始化中", false, false, null);
        // SDK初始化
        WACoreProxy.initialize(this, new WACallback<Void>() {
            @Override
            public void onSuccess(int code, String message, Void result) {
                cancelLoadingDialog();
                doAfterInitSuccess();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(int code, String message, Void result, Throwable throwable) {
                cancelLoadingDialog();
                new AlertDialog.Builder(MainActivity.this)
                        .setCancelable(false)
                        .setMessage("初始化失败，请退出应用重新进入")
                        .setPositiveButton("退出", (dialog, which) -> finish())
                        .show();
            }
        });
    }

    private void delayLoginUI(int second) {
        if (second < 0 || FlavorApiHelper.isNowggFlavor()) return; // nowgg 版本不能直接登录

        // WAUserProxy.loginUI() 在刚接入时需要运营在后台添加测试设备，才会显示具体登录方式，比如: Google，Facebook
        new Handler().postDelayed(() -> WAUserProxy.loginUI(MainActivity.this, true, new WACallback<WALoginResult>() {
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

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        // 进入游戏
                        String txServerId = "2";
                        String serverId = TextUtils.isEmpty(txServerId) ? "server2" : "server" + txServerId;
                        String gameUserId = serverId + "-role1-" + result.getUserId();
                        String nickname = "青铜" + serverId + "-" + result.getUserId();

                        WACoreProxy.setServerId(serverId);
                        WACoreProxy.setGameUserId(gameUserId);
                        WACoreProxy.setNickname(nickname);
                    }, 3000);
                }
                WASdkDemo.getInstance().updateLoginAccount(result);
                LogUtil.i(TAG, text);
                showLongToast(text);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(int code, String message, WALoginResult result, Throwable throwable) {
                String text = "code:" + code + "\nmessage:" + message;
                LogUtil.w(LogUtil.TAG, "Login failed->" + text);
                showLongToast("Login failed->" + text);
            }
        }), second * 1000L);
    }

    private void doAfterInitSuccess() {
        // 横幅广告
        FlavorApiHelper.AdMob.bindMainBannerAd(this);

        // 支付初始化
        WAPayProxy.initialize(this, new WACallback<WAResult>() {

            @Override
            public void onSuccess(int code, String message, WAResult result) {
                LogUtil.d(TAG, "WAPayProxy.initialize success");
                mPayInitialized = true;
                WAPayProxy.queryInventory(null);
            }

            @Override
            public void onCancel() {
                LogUtil.d(TAG, "WAPayProxy.initialize has been cancelled.");
                mPayInitialized = false;
            }

            @Override
            public void onError(int code, String message, WAResult result, Throwable throwable) {
                LogUtil.d(TAG, "WAPayProxy.initialize error");
                showLongToast("Payment initialization fail.");
                mPayInitialized = false;
            }
        });

        // 延迟 n 秒后调用登录弹窗
        delayLoginUI(1);

        // 调试入口
        if (mSharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_LOGCAT, true)) {
            WACommonProxy.enableLogcat(this);
        } else {
            WACommonProxy.disableLogcat(this);
        }

        WAAdProxy.setAdCachedCallback(new WAAdCachedCallback() {
            @Override
            public void onVideoCached(int validVideoCount) {
                String text = "有新的广告缓存成功，当前可用广告数： " + validVideoCount;
                LogUtil.e(WAConstants.TAG, text);
                showShortToast(text);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (null == intent) {
            return;
        }
        Bundle bundle = intent.getExtras();
        if (null != bundle) {
            Set<String> keys = bundle.keySet();
            for (String key : keys) {
                LogUtil.e(TAG, "onNewIntent - Key :" + key);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        WACommonProxy.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    @Override
    protected void onStart() {
        super.onStart();
//        Chartboost.onStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // LogUtil.i(TAG, "---onResume---");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // LogUtil.i(TAG, "---onPause---");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // LogUtil.i(TAG, "---onStop---");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // LogUtil.i(TAG, "---onDestroy---");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (WACommonProxy.onActivityResult(requestCode, resultCode, intent)) {
            return;
        }
        switch (requestCode) {
            case 200: // 登录
                if (RESULT_OK == resultCode) {
                    switch (mPendingAction) {
                        case GO_ACCOUNT_MANAGER:
                            accountManager();
                            break;
                        default:
                            break;
                    }
                }
                mPendingAction = PendingAction.NONE;
                break;
            case 201: // 账号管理
                break;
            case 202: // 支付
                break;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_request_permission) {
            startActivity(new Intent(this, PermissionActivity.class));
        } else if (id == R.id.btn_login) {
            login(false);
        } else if (id == R.id.btn_account_manager) {
            openAccountManager();
        } else if (id == R.id.btn_pay) {
            payment();
        } else if (id == R.id.btn_static_pay) {
            staticPay();
        } else if (id == R.id.btn_tracking) {
            testTracking();
        } else if (id == R.id.btn_game_service) {
            startActivity(new Intent(MainActivity.this, GameServiceActivity.class));
        } else if (id == R.id.btn_csc) {
            startActivity(new Intent(this, CscActivity.class));
        } else if (id == R.id.btn_privacy) {
            startActivity(new Intent(this, PrivacyActivity.class));
        } else if (id == R.id.btn_user_center) {
            startActivity(new Intent(this, UserCenterActivity.class));
        } else if (id == R.id.btn_random_client_id) {
            String clientId = UUID.randomUUID().toString().replaceAll("-", "");
            mEdtClientId.setText(clientId);
        } else if (id == R.id.btn_create_client_id) {
            String strClientId = mEdtClientId.getText().toString();
            if (TextUtils.isEmpty(strClientId)) {
                showShortToast("ClientId不能为空");
            } else {
                WACoreProxy.setClientId(strClientId);
                showShortToast("Client设置成功：" + strClientId);
            }
        } else if (id == R.id.btn_open_review) {
            openReview();
        } else if (id == R.id.btn_account_deletion) {
            startActivity(new Intent(MainActivity.this, UserDeletionActivity.class));
        } else if (id == R.id.btn_display_app_version_info) {//app 信息
            new AlertDialog.Builder(this).setMessage(Util.getApkBuildInfo(this)).show();
        } else if (id == R.id.btn_switch_orientation) {
            int orientation = mSharedPrefHelper.getInt(WADemoConfig.SP_KEY_SETTING_ORIENTATION, 0);
            orientation++;
            if (orientation > 2) orientation = 0;
            mSharedPrefHelper.saveInt(WADemoConfig.SP_KEY_SETTING_ORIENTATION, orientation);
            updateScreenOrientationText();
        } else if (id == R.id.btn_open_game_review) {
            openGameReview();
        } else if (id == R.id.btn_show_consent_preferences) {
            WACmpProxy.showConsentPreferences(this);
        } else if (id == R.id.btn_admob) {
            if (FlavorApiHelper.isAdMobFlavor()) {
                // 打开 AdMobActivity
                startActivity(new Intent(this, FlavorApiHelper.AdMob.getAdMobActivityClass()));
            } else {
                showShortToast("不是Admob包");
            }
        } else if (id == R.id.btn_rare_function) {
            startActivity(new Intent(this, RareFunctionActivity.class));
        }
    }

    private void updateScreenOrientationText() {
        int orientation = mSharedPrefHelper.getInt(WADemoConfig.SP_KEY_SETTING_ORIENTATION, 0);
        if (orientation == 1) {
            mTvScreenOrientation.setText("强制竖屏");
        } else if (orientation == 2) {
            mTvScreenOrientation.setText("强制横屏");
        } else {
            mTvScreenOrientation.setText("默认");
        }
    }

    private void openReview() {
        //不管回掉结果是什么，都需要统一当成成功处理后续逻辑
        WAUserProxy.openReview(this, new WACallback<Boolean>() {
            @Override
            public void onSuccess(int code, String message, Boolean result) {
                showShortToast("api调用流程已经完成，无法获取用户是否评分，是否弹出评分框," + message);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(int code, String message, Boolean result, Throwable throwable) {


            }
        });
    }

    private void openAccountManager() {
        if (WASdkDemo.getInstance().isLogin()) {
            accountManager();
        } else {
            showLongToast("Not loginAccount! Please loginAccount first!");
            new AlertDialog.Builder(MainActivity.this).setTitle(R.string.warming).setMessage(R.string.not_login_yet).setPositiveButton(R.string.login_now, (dialog, which) -> {
                mPendingAction = PendingAction.GO_ACCOUNT_MANAGER;
                login(true);
            }).setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel()).show();
        }
    }

    private void staticPay() {
        if (!mPayInitialized) {
            showShortToast("Payment not initialize!");
            return;
        }
        final String skuId = mEtSkuId.getText().toString().trim();
        if (StringUtil.isEmpty(skuId)) {
            showShortToast("Sku id is empty!");
            return;
        }
        WAPayProxy.payUI(this, skuId, "static payment", new WACallback<WAPurchaseResult>() {
            @Override
            public void onSuccess(int code, String message, WAPurchaseResult result) {
                LogUtil.d(TAG, "pay success");
                cancelLoadingDialog();
                showLongToast("Payment is successful..." + result.getWAProductId());
            }

            @Override
            public void onCancel() {
                LogUtil.d(TAG, "pay cancel");
                cancelLoadingDialog();
                showLongToast("Payment has been cancelled.");
            }

            @Override
            public void onError(int code, String message, WAPurchaseResult result, Throwable throwable) {
                LogUtil.d(TAG, "pay error, code:" + code + ", msg:" + message);
                cancelLoadingDialog();
                if (WACallback.CODE_NOT_LOGIN == code) {
                    new AlertDialog.Builder(MainActivity.this).setTitle(R.string.warming).setMessage(R.string.not_login_yet).setPositiveButton(R.string.login_now, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            login(true);
                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();
                }
                showLongToast(StringUtil.isEmpty(message) ? "Billing service is not available at this moment." : message);
            }
        });
    }

    private void openGameReview() {
        WAUserProxy.openGameReview(this, new WAGameReviewCallback() {
            @Override
            public void onError(int code, String message) {
                String text = "打开游戏评价失败：" + code + "," + message;
                showShortToast(text);
                Log.d(WAConstants.TAG, text);
            }

            @Override
            public void onReject() {
                String text = "游戏评价结果：不，谢谢！";
                showShortToast(text);
                Log.d(WAConstants.TAG, text);
            }

            @Override
            public void onOpenAiHelp() {
                String text = "游戏评价结果：我要提意见";
                showShortToast(text);
                Log.d(WAConstants.TAG, text);
            }

            @Override
            public void onReviewComplete() {
                String text = "游戏评价结果：提交好评";
                showShortToast(text);
                Log.d(WAConstants.TAG, text);
            }
        });
    }

    private void payment() {
        startActivityForResult(new Intent(this, PaymentActivity.class), 202);
    }

    /**
     * 登录
     */
    public void login(boolean autoFinish) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("auto_finish", autoFinish);
        startActivityForResult(intent, 200);
    }


    /**
     * 账号管理
     */
    public void accountManager() {
        startActivityForResult(new Intent(this, AccountManagerActivity.class), 201);
    }

    /**
     * 支付测试点击
     */
    /*public void ggIapPay() {

        Intent intent = new Intent(this, GGIabActivity.class);
        startActivity(intent);
    }*/

    /**
     * 数据收集测试点击
     */
    public void testTracking() {
        startActivity(new Intent(this, TrackingSimulateActivity.class));
    }

    private void initView() {

        TitleBar tb = findViewById(R.id.tb_main);
        tb.setRightButton(android.R.drawable.ic_menu_close_clear_cancel, v -> finish());
        tb.setTitleText(R.string.app_name);
        tb.setTitleTextColor(R.color.color_white);

        ToggleButton tbtnLogcat = findViewById(R.id.tbtn_logcat);
        boolean enableLogcat = mSharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_LOGCAT, true);
        tbtnLogcat.setChecked(enableLogcat);
        tbtnLogcat.setOnCheckedChangeListener(mOnCheckedChangeListener);

        ToggleButton tbtnDebug = findViewById(R.id.tbtn_debug);
        boolean enableDebug = mSharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_DEBUG, true);
        tbtnDebug.setChecked(enableDebug);
        tbtnDebug.setOnCheckedChangeListener(mOnCheckedChangeListener);

        mEtSkuId = findViewById(R.id.et_static_pay_sku_id);
        mEtSkuId.setText("1");

        mEdtClientId = findViewById(R.id.edt_client_id);
        mTvScreenOrientation = findViewById(R.id.tv_screen_orientation);
        updateScreenOrientationText();
    }

    private final CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int id = buttonView.getId();
            if (id == R.id.tbtn_logcat) {
                mSharedPrefHelper.saveBoolean(WADemoConfig.SP_KEY_ENABLE_LOGCAT, isChecked);
                if (isChecked) {
                    WACommonProxy.enableLogcat(MainActivity.this);
                } else {
                    WACommonProxy.disableLogcat(MainActivity.this);
                }
            } else if (id == R.id.tbtn_debug) {
                mSharedPrefHelper.saveBoolean(WADemoConfig.SP_KEY_ENABLE_DEBUG, isChecked);
                WACoreProxy.setDebugMode(isChecked);
            }

        }
    };

    private void loginGuest() {
        WAUserProxy.login(MainActivity.this, WAConstants.CHANNEL_GUEST, new WACallback<WALoginResult>() {
            @Override
            public void onSuccess(int code, String message, WALoginResult result) {
                showShortToast("游客登录成功:" + result.getUserId());
                WASdkDemo.getInstance().updateLoginAccount(result);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(int code, String message, WALoginResult result, Throwable throwable) {
                if (code == WACallback.CODE_ACCOUNT_IN_DELETION_BUFFER_DAYS) {
                    WASdkDemo.getInstance().updateLoginAccount(result);
                }
            }
        }, null);
    }
}
