package com.wa.sdk.demo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.wa.sdk.admob.WAAdMobPublicProxy;
import com.wa.sdk.cmp.WACmpProxy;
import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.WASharedPrefHelper;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.model.WAResult;
import com.wa.sdk.common.utils.StringUtil;
import com.wa.sdk.core.WACoreProxy;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.base.FlavorApiHelper;
import com.wa.sdk.demo.tracking.TrackingSimulateActivity;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.pay.WAPayProxy;
import com.wa.sdk.pay.model.WAPurchaseResult;
import com.wa.sdk.track.WATrackProxy;
import com.wa.sdk.track.model.WAUserCreateEvent;
import com.wa.sdk.track.model.WAUserImportEvent;
import com.wa.sdk.user.WAUserProxy;
import com.wa.sdk.user.model.WAGameReviewCallback;
import com.wa.sdk.user.model.WALoginResult;

import java.util.UUID;


public class MainActivity extends BaseActivity {
    private WASharedPrefHelper mSharedPrefHelper;
    private EditText mEtProductId;
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

        boolean isEnableAppOpenAd = mSharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_APP_OPEN_AD, AdMobActivity.DEFAULT_APP_OPEN_AD_STATE);
        // 避免与 AdMob 开屏页重复初始化
        if (isEnableAppOpenAd) {
            doAfterInitSuccess();
            return;
        }

        // AdMob 强制开启测试
        boolean isTest = mSharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_TEST_AD_UNIT, AdMobActivity.DEFAULT_TEST);
        WAAdMobPublicProxy.setTest(this, isTest);
        // 开启 WingSDK 日志
        WACoreProxy.setDebugMode(mSharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_DEBUG, true));
        // WingSDK 初始化
        showLoadingDialog("初始化中", false, false, null);
        WACoreProxy.initialize(this, new WACallback<Void>() {
            @Override
            public void onSuccess(int code, String message, Void result) {
                cancelLoadingDialog();
                // 初始化成功，执行 WingSDK支付初始化 等逻辑
                doAfterInitSuccess();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(int code, String message, Void result, Throwable throwable) {
                cancelLoadingDialog();
                logE("init failed, code:" + code + ", message:" + message);
                new AlertDialog.Builder(MainActivity.this)
                        .setCancelable(false)
                        .setMessage("初始化失败，请退出应用重新进入")
                        .setPositiveButton("退出", (dialog, which) -> finish())
                        .show();
            }
        });
    }

    private void doAfterInitSuccess() {
        // 支付初始化
        WAPayProxy.initialize(this, new WACallback<WAResult>() {

            @Override
            public void onSuccess(int code, String message, WAResult result) {
                logD("Payment initialize success.");
                mPayInitialized = true;
            }

            @Override
            public void onCancel() {
                logD("Payment initialize cancelled.");
                mPayInitialized = false;
            }

            @Override
            public void onError(int code, String message, WAResult result, Throwable throwable) {
                String text = "Payment initialize failed.";
                logE(text);
                showLongToast(text);
                mPayInitialized = false;
            }
        });

        // 显示或隐藏 Consent同意设置 按钮
        WACmpProxy.checkConsentPreferences(new WACallback<Boolean>() {
            @Override
            public void onSuccess(int code, String message, Boolean isShow) {
                // 返回 true，则需要显示 Consent同意设置 按钮；false，则否；
                Button btn = findViewById(R.id.btn_show_consent_preferences);
                btn.setTextColor(isShow ? Color.BLACK : Color.GRAY);
                btn.setText(btn.getText() + (isShow ? "(显示)" : "(隐藏)"));
            }

            @Override
            public void onCancel() {
                // 忽略，无需处理
            }

            @Override
            public void onError(int code, String message, Boolean result, Throwable throwable) {
                // 忽略，无需处理
            }
        });

        // AdMob 横幅广告
        boolean isEnableBannerAd = mSharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_BANNER_AD, AdMobActivity.DEFAULT_BANNER_AD_STATE);
        if (isEnableBannerAd) {
            WAAdMobPublicProxy.bindBannerAd(this, findViewById(R.id.layout_main_banner_ad));
        }

        // 延迟 n 秒后调用登录弹窗
        delayLoginUI(1);
    }

    private void delayLoginUI(int second) {
        if (second < 0 || FlavorApiHelper.isNowggFlavor()) return; // nowgg包 不能直接登录

        // WAUserProxy.loginUI() 在刚接入时需要获取ClientId给运营在后台添加测试设备，才会显示具体登录方式，比如: Google，Facebook
        new Handler().postDelayed(() -> WAUserProxy.loginUI(MainActivity.this, true, new WACallback<WALoginResult>() {
            @Override
            public void onSuccess(int code, String message, WALoginResult result) {
                String text = "code:" + code + "\nmessage:" + message;

                if (result == null) {
                    text = "Login failed->" + text;
                    showLongToast(text);
                    logI(text);
                    return;
                }

                WASdkDemo.getInstance().updateLoginAccount(result);
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
                showLongToast(text);
                logI(text);

                // 登录成功后，用户会开始进服，创角，相关的事件发送 以及 通知权限申请
                userEnterGame(result);
            }

            @Override
            public void onCancel() {
                showLongToast("Login canceled");
            }

            @Override
            public void onError(int code, String message, WALoginResult result, Throwable throwable) {
                String text = "code:" + code + "\nmessage:" + message;
                logW("Login failed->" + text);
                showLongToast("Login failed->" + text);
            }
        }), second * 1000L);
    }

    /**
     * 登录成功后，用户会开始进服，创角，相关的事件发送 以及 通知权限申请，具体参考本方法实现
     *
     * @param result 登录结果
     */
    private void userEnterGame(WALoginResult result) {
        // 用户选服之后，点击进服，需要发送进服事件
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            String serverId = "server1"; // 服务器ID
            int level = 1; // 用户等级
            String gameUserId; // 角色ID
            String nickname; // 角色昵称

            //首次进服标志（游戏需要根据用户实际情况判断该用户是否首次进服，此处只是演示示例）
            boolean isFirstEnter = mSharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_IS_FIRST_ENTER, true);
            if (isFirstEnter) {
                // 首次进服，此时未创角
                gameUserId = "-1"; // 如果未创角，可以设置为 -1
                nickname = ""; // 如果未创角，可以设置为空
            } else {
                // 非首次进服时，会有 nickname 和 gameUserId
                gameUserId = serverId + "-role1-" + result.getUserId();
                nickname = "青铜" + serverId + "-" + result.getUserId();
            }

            logD(isFirstEnter ? "Demo 首次进服（正常发送）" : "Demo 非首次进服");

            // 进服事件
            WAUserImportEvent importEvent = new WAUserImportEvent(serverId, gameUserId, nickname, level, isFirstEnter);
            WATrackProxy.trackEvent(MainActivity.this, importEvent);

            if (isFirstEnter) {
                // 发了首次进服后，下次无论有无创角都可以按非首次进服发送
                mSharedPrefHelper.saveBoolean(WADemoConfig.SP_KEY_IS_FIRST_ENTER, false);
                // 首次进服后，用户会进行创角操作，需要发送创角事件
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    // 创角后，有角色ID和NickName
                    String newGameUserId = serverId + "-role1-" + result.getUserId();
                    String newNickname = "青铜" + serverId + "-" + result.getUserId();
                    long registerTime = System.currentTimeMillis();
                    WAUserCreateEvent userCreateEvent = new WAUserCreateEvent(serverId, newGameUserId, newNickname, registerTime);
                    WATrackProxy.trackEvent(MainActivity.this, userCreateEvent);
                }, 2000);
            }

            // 玩家进服后申请通知权限
            if (Build.VERSION.SDK_INT >= 33) {
                // 该权限不要求用户必须授权，参数按照下面传入false不强制，和null无弹窗提示语即可
                WACommonProxy.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS, false, null, null, null);
            } else {
                // 系统低于 Android 13 无需授权通知权限，默认开启
            }
        }, 2000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // logI( "---onStart---");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // logI( "---onResume---");
        if (FlavorApiHelper.isLeidianFlavor()) {
            WACommonProxy.onResume(this); // 接入雷电渠道才需要
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // logI( "---onPause---");
        if (FlavorApiHelper.isLeidianFlavor()) {
            WACommonProxy.onPause(this); // 接入雷电渠道才需要
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // logI( "---onStop---");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // logI( "---onDestroy---");
        if (FlavorApiHelper.isLeidianFlavor()) {
            WACommonProxy.onDestroy(this); // 接入雷电渠道才需要
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // 必须添加，否则会导致无法获得登录结果等问题
        if (WACommonProxy.onActivityResult(requestCode, resultCode, intent)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_login) {
            // 登录
            startActivity(new Intent(this, LoginActivity.class));
        } else if (id == R.id.btn_pay) {
            // 支付
            startActivity(new Intent(this, PaymentActivity.class));
        } else if (id == R.id.btn_static_pay) {
            // ProductId直接支付
            productPay();
        } else if (id == R.id.btn_tracking) {
            // 数据采集
            startActivity(new Intent(this, TrackingSimulateActivity.class));
        } else if (id == R.id.btn_request_permission) {
            // 权限申请（通知权限）
            startActivity(new Intent(this, PermissionActivity.class));
        } else if (id == R.id.btn_account_manager) {
            // 账号管理
            if (isNotLoginAndTips()) return;
            startActivity(new Intent(this, AccountManagerActivity.class));
        } else if (id == R.id.btn_csc) {
            // AiHelp客服
            startActivity(new Intent(this, CscActivity.class));
        } else if (id == R.id.btn_user_center) {
            // 用户中心
            startActivity(new Intent(this, UserCenterActivity.class));
        } else if (id == R.id.btn_account_deletion) {
            // 账号删除
            startActivity(new Intent(MainActivity.this, UserDeletionActivity.class));
        } else if (id == R.id.btn_open_game_review) {
            // 打开游戏评价
            openGameReview();
        } else if (id == R.id.btn_show_consent_preferences) {
            // Consent同意设置
            WACmpProxy.showConsentPreferences(this);
        } else if (id == R.id.btn_admob) {
            // AdMob 广告
            startActivity(new Intent(this, AdMobActivity.class));
        } else if (id == R.id.btn_rare_function) {
            // 不常用功能
            startActivity(new Intent(this, RareFunctionActivity.class));
        }

        // 下面是测试用的功能
        if (id == R.id.btn_display_app_version_info) {
            // Demo 信息
            new AlertDialog.Builder(this).setMessage(Util.getApkBuildInfo(this)).show();
        } else if (id == R.id.btn_switch_orientation) {
            // 修改Demo屏幕方向
            int orientation = mSharedPrefHelper.getInt(WADemoConfig.SP_KEY_SETTING_ORIENTATION, 0);
            orientation++;
            if (orientation > 2) orientation = 0;
            mSharedPrefHelper.saveInt(WADemoConfig.SP_KEY_SETTING_ORIENTATION, orientation);
            updateScreenOrientationText();
        } else if (id == R.id.btn_create_random_client_id) {
            // 设置随机ClientId（测试用）
            String clientId = UUID.randomUUID().toString().replaceAll("-", "");
            WACoreProxy.setClientId(clientId);
            showShortToast("Client设置成功：" + clientId);
        } else if (id == R.id.btn_clear_first_login) {
            // 清除 MainActivity 的首次登录标志
            boolean remove = mSharedPrefHelper.remove(WADemoConfig.SP_KEY_IS_FIRST_ENTER);
            WAUserProxy.logout();
            showShortToast("清除首次登录：" + remove);
        }
    }

    private void productPay() {
        if (!mPayInitialized) {
            showShortToast("Payment not initialize!");
            return;
        }
        final String skuId = mEtProductId.getText().toString().trim();
        if (StringUtil.isEmpty(skuId)) {
            showShortToast("sdk product id is empty!");
            return;
        }
        WAPayProxy.payUI(this, skuId, "static payment", new WACallback<WAPurchaseResult>() {
            @Override
            public void onSuccess(int code, String message, WAPurchaseResult result) {
                cancelLoadingDialog();
                showLongToast("Product:" + result.getWAProductId() + " Payment  successful", true);
            }

            @Override
            public void onCancel() {
                cancelLoadingDialog();
                showLongToast("Payment cancelled.", true);
            }

            @Override
            public void onError(int code, String message, WAPurchaseResult result, Throwable throwable) {
                cancelLoadingDialog();
                if (WACallback.CODE_NOT_LOGIN == code) {
                    isNotLoginAndTips();
                }

                String text = "Payment failed, code:" + code + ", msg:" + message;
                showLongToast(text);
                logE(text);
            }
        });
    }

    /**
     * 打开游戏评价窗口。弹出后用户可以选择提交好评(即拉起Google评分)，或我要提意见(即打开AiHelp)，或不谢谢(即关闭窗口，手势返回关闭同这个操作一样)
     * <br>注意：
     * <br>1.由于Google限制，我们无法知道用户是否已经评分，是否弹有出评分界面，onReviewComplete()仅代表Google评分接口调用成功
     * <br>2.如果需要评价完成给用户发放奖励，在 onReviewComplete()回调中处理即可
     */
    private void openGameReview() {
        WAUserProxy.openGameReview(this, new WAGameReviewCallback() {
            @Override
            public void onError(int code, String message) {
                String text = "打开游戏评价失败：" + code + "," + message;
                showShortToast(text);
                logE(text);
            }

            @Override
            public void onReject() {
                String text = "游戏评价结果：不，谢谢！";
                showShortToast(text, true);
            }

            @Override
            public void onOpenAiHelp() {
                String text = "游戏评价结果：我要提意见";
                showShortToast(text, true);
            }

            @Override
            public void onReviewComplete() {
                // 如果需要好评发奖励，可以在这里处理
                String text = "游戏评价结果：提交好评";
                showShortToast(text, true);
            }
        });
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

        mEtProductId = findViewById(R.id.et_static_pay_product_id);
        mEtProductId.setText("1");

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

}
