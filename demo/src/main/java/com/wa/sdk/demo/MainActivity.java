package com.wa.sdk.demo;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.wa.sdk.admob.WAAdMobPublicProxy;
import com.wa.sdk.cmp.WACmpProxy;
import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.model.WAResult;
import com.wa.sdk.common.utils.StringUtil;
import com.wa.sdk.core.WACoreProxy;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.base.FlavorApiHelper;
import com.wa.sdk.demo.rare.RareFunctionActivity;
import com.wa.sdk.demo.utils.WADemoConfig;
import com.wa.sdk.demo.utils.WASdkDemo;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.pay.WAPayProxy;
import com.wa.sdk.pay.model.WAPurchaseResult;
import com.wa.sdk.track.WATrackProxy;
import com.wa.sdk.track.model.WAUserCreateEvent;
import com.wa.sdk.track.model.WAUserImportEvent;
import com.wa.sdk.user.WAUserProxy;
import com.wa.sdk.user.model.WAGameReviewCallback;
import com.wa.sdk.user.model.WALoginResultV2;


public class MainActivity extends BaseActivity {
    private EditText mEtProductId;
    private boolean mPayInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setScreenOrientation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        boolean isEnableAppOpenAd = getSpHelper().getBoolean(WADemoConfig.SP_KEY_ENABLE_APP_OPEN_AD, AdMobActivity.DEFAULT_APP_OPEN_AD_STATE);
        // 避免与 AdMob 开屏页重复初始化
        if (isEnableAppOpenAd) {
            doAfterInitSuccess();
            return;
        }

        // 开启 WingSDK 日志
        WACoreProxy.setDebugMode(getSpHelper().getBoolean(WADemoConfig.SP_KEY_ENABLE_DEBUG, true));
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
        boolean isEnableBannerAd = getSpHelper().getBoolean(WADemoConfig.SP_KEY_ENABLE_BANNER_AD, AdMobActivity.DEFAULT_BANNER_AD_STATE);
        if (isEnableBannerAd) {
            WAAdMobPublicProxy.bindBannerAd(this, findViewById(R.id.layout_main_banner_ad));
        }

        // 延迟 n 秒后调用登录弹窗
        delayLoginUI(1);
    }

    private void delayLoginUI(int second) {
        if (second < 0 || FlavorApiHelper.isNowggFlavor()) return; // nowgg包 不能直接登录

        // WAUserProxy.loginUI() 在刚接入时需要获取ClientId给运营在后台添加测试设备，才会显示具体登录方式，比如: Google，Facebook
        new Handler().postDelayed(() -> WAUserProxy.loginUIV2(MainActivity.this, true, new WACallback<WALoginResultV2>() {
            @Override
            public void onSuccess(int code, String message, WALoginResultV2 result) {
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
            public void onError(int code, String message, WALoginResultV2 result, Throwable throwable) {
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
    private void userEnterGame(WALoginResultV2 result) {
        // 用户选服之后，点击进服，需要发送进服事件
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            String serverId = "server1"; // 服务器ID
            int level = 1; // 用户等级
            String gameUserId; // 角色ID
            String nickname; // 角色昵称

            // 首次进服在创角时发送
            boolean isFirstImportInCreate = getSpHelper().getBoolean(WADemoConfig.SP_KEY_IS_FIRST_IMPORT_IN_CREATE, false);
            //首次进服标志（游戏需要根据用户实际情况判断该用户是否首次进服，此处只是演示示例）
            boolean isFirstEnter = getSpHelper().getBoolean(WADemoConfig.SP_KEY_IS_FIRST_ENTER, true);
            if (isFirstEnter && !isFirstImportInCreate) {
                // 首次进服，此时未创角
                gameUserId = "-1"; // 如果未创角，可以设置为 -1
                nickname = ""; // 如果未创角，可以设置为空
            } else {
                // 非首次进服时（或首次进服在创角时才发送），会有 nickname 和 gameUserId
                gameUserId = serverId + "-role1-" + result.getUserId();
                nickname = "青铜" + serverId + "-" + result.getUserId();
            }

            if (isFirstEnter) {
                logD(isFirstImportInCreate ? "Demo 首次进服（在创角时发送）" : "Demo 首次进服（正常发送）");
            } else {
                logD("Demo 非首次进服");
            }

            // 进服事件
            WAUserImportEvent importEvent = new WAUserImportEvent(serverId, gameUserId, nickname, level, isFirstEnter);
            WATrackProxy.trackEvent(MainActivity.this, importEvent);

            if (isFirstEnter) {
                // 发了首次进服后，下次无论有无创角都可以按非首次进服发送
                getSpHelper().saveBoolean(WADemoConfig.SP_KEY_IS_FIRST_ENTER, false);
                // 首次进服后，用户会进行创角操作，需要发送创角事件
                if (!isFirstImportInCreate) {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        // 创角后，有角色ID和NickName
                        String newGameUserId = serverId + "-role1-" + result.getUserId();
                        String newNickname = "青铜" + serverId + "-" + result.getUserId();
                        long registerTime = System.currentTimeMillis();
                        WAUserCreateEvent userCreateEvent = new WAUserCreateEvent(serverId, newGameUserId, newNickname, registerTime);
                        WATrackProxy.trackEvent(MainActivity.this, userCreateEvent);
                    }, 2000);
                } else {
                    // 进服和创角一起发时无间隔
                    String newGameUserId = serverId + "-role1-" + result.getUserId();
                    String newNickname = "青铜" + serverId + "-" + result.getUserId();
                    long registerTime = System.currentTimeMillis();
                    WAUserCreateEvent userCreateEvent = new WAUserCreateEvent(serverId, newGameUserId, newNickname, registerTime);
                    WATrackProxy.trackEvent(MainActivity.this, userCreateEvent);
                }
            }

            // 玩家进服后申请通知权限
            WACommonProxy.requestNotificationPermission(this);
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
            // 发起支付
            productPay();
        } else if (id == R.id.btn_tracking) {
            // 数据采集
            startActivity(new Intent(this, TrackingEventActivity.class));
        } else if (id == R.id.btn_request_permission) {
            // 权限申请（通知权限）
            startActivity(new Intent(this, PermissionActivity.class));
        } else if (id == R.id.btn_account_manager) {
            // 账号管理
            if (isNotLoginAndTips()) return;
            startActivity(new Intent(this, AccountManagerActivity.class));
        } else if (id == R.id.btn_csc) {
            // AiHelp客服
            startActivity(new Intent(this, AiHelpActivity.class));
        } else if (id == R.id.btn_user_center) {
            // 用户中心
            startActivity(new Intent(this, UserCenterActivity.class));
        } else if (id == R.id.btn_account_deletion) {
            // 账号删除
            if (isNotLoginAndTips()) return;
            startActivity(new Intent(MainActivity.this, UserDeletionActivity.class));
        } else if (id == R.id.btn_open_game_review) {
            // 打开游戏评价
            openGameReview();
        } else if (id == R.id.btn_show_consent_preferences) {
            // 打开Consent同意设置（若无法显示弹窗，则需要开启英国等属于欧盟范围的VPN来测试）
            WACmpProxy.showConsentPreferences(this);
        } else if (id == R.id.btn_admob) {
            // AdMob 广告
            startActivity(new Intent(this, AdMobActivity.class));
        } else if (id == R.id.btn_rare_function) {
            // 不常用功能
            startActivity(new Intent(this, RareFunctionActivity.class));
        }
    }

    private void productPay() {
        if (!mPayInitialized) {
            showShortToast("Payment not initialize!");
            return;
        }
        final String sdkProductId = mEtProductId.getText().toString().trim();
        if (StringUtil.isEmpty(sdkProductId)) {
            showShortToast("sdk product id is empty!");
            return;
        }
        WAPayProxy.payUI(this, sdkProductId, "CpOrderId:12345", new WACallback<WAPurchaseResult>() {
            @Override
            public void onSuccess(int code, String message, WAPurchaseResult result) {
                cancelLoadingDialog();
                logD("Payment Success:\n" + result);
                showLongToast("Payment is successful. ProductId:" + result.getWAProductId() + " , ExtInfo:" + result.getExtInfo());
            }

            @Override
            public void onCancel() {
                cancelLoadingDialog();
                logD("Payment Cancel");
                showLongToast("Payment has been cancelled.");
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
                String text = "Open Game Review Failed：" + code + "," + message;
                showShortToast(text);
                logE(text);
            }

            @Override
            public void onReject() {
                String text = "Game Review Result：No, Thanks";
                showShortToast(text);
                logD(text);
            }

            @Override
            public void onOpenAiHelp() {
                String text = "Game Review Result：Feedback";
                showShortToast(text);
                logD(text);
            }

            @Override
            public void onReviewComplete() {
                // 如果需要好评发奖励，可以在这里处理
                String text = "Game Review Result：Rate Us";
                showShortToast(text);
                logD(text);
            }
        });
    }

    private void initView() {
        TitleBar tb = findViewById(R.id.tb_main);
        tb.setRightButton(android.R.drawable.ic_menu_close_clear_cancel, v -> finish());
        tb.setTitleText(R.string.title_main);
        tb.setTitleTextColor(R.color.color_white);

        ToggleButton tbtnDebug = findViewById(R.id.tbtn_debug);
        boolean enableDebug = getSpHelper().getBoolean(WADemoConfig.SP_KEY_ENABLE_DEBUG, true);
        tbtnDebug.setChecked(enableDebug);
        tbtnDebug.setOnCheckedChangeListener(mOnCheckedChangeListener);

        mEtProductId = findViewById(R.id.et_static_pay_product_id);
        mEtProductId.setText("1");

    }

    private final CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = (buttonView, isChecked) -> {
        int id = buttonView.getId();
        if (id == R.id.tbtn_debug) {
            getSpHelper().saveBoolean(WADemoConfig.SP_KEY_ENABLE_DEBUG, isChecked);
            WACoreProxy.setDebugMode(isChecked);
        }
    };

}
