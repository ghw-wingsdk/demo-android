package com.wa.sdk.demo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;

import com.wa.sdk.admob.WAAdMobPublicProxy;
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
import com.wa.sdk.track.model.WAUserImportEventV2;
import com.wa.sdk.user.WAUserProxy;
import com.wa.sdk.user.model.WAAccountCallbackV2;
import com.wa.sdk.user.model.WABindResultV2;
import com.wa.sdk.user.model.WACertificationInfo;
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
                        .setNegativeButton("继续(测试)", null) // 正常情况下不应该让用户继续操作
                        .show();
            }
        });
    }

    private void doAfterInitSuccess() {
        // AdMob 横幅广告
        boolean isEnableBannerAd = getSpHelper().getBoolean(WADemoConfig.SP_KEY_ENABLE_BANNER_AD, AdMobActivity.DEFAULT_BANNER_AD_STATE);
        if (isEnableBannerAd && WAAdMobPublicProxy.isOpenBannerAd()) {
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

                // 登录成功后，用户会开始进服，创角，需要发送这两个事件 以及 申请通知权限
                userEnterGame();
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
     * 登录成功后，用户会开始进服，创角，需要发送这两个事件 以及 申请通知权限
     */
    private void userEnterGame() {
        // 用户选服之后，点击进服，需要发送进服事件
        delayCall(2, () -> {
            String serverId = TrackingEventActivity.getCurrentServerId(); // 当前服务器ID
            int level; // 游戏角色等级
            String gameUserId; // 角色ID
            String nickname; // 角色昵称

            // 是否新用户，新用户无角色信息，旧用户有角色信息
            boolean isNewUser = getSpHelper().getBoolean(WADemoConfig.SP_KEY_IS_FIRST_ENTER, true);
            // 无需创角的游戏（这类型游戏一进服就会有角色信息）
            boolean isNoCreateUserGame = getSpHelper().getBoolean(WADemoConfig.SP_KEY_IS_FIRST_IMPORT_IN_CREATE, false);

            if (isNewUser && !isNoCreateUserGame) {
                // 未创角，缺少信息可以按下面设置默认值
                gameUserId = "-1"; // 可以设置为 "-1"
                nickname = ""; // 可以设置为空字符串""
                level = 1; // 填入游戏角色初始等级，一般为 1
            } else {
                // 已创角，或者进服就有角色信息，直接获取当前用户角色的对应信息
                gameUserId = TrackingEventActivity.getCurrentGameUserId();
                nickname = TrackingEventActivity.getCurrentNickname();
                level = TrackingEventActivity.getCurrentLevel();
            }
            // 发送进服事件
            WAUserImportEventV2 userImportEvent = new WAUserImportEventV2(serverId, gameUserId, nickname, level);
            WATrackProxy.trackEvent(MainActivity.this, userImportEvent);

            // 新用户进服后，用户会进行创角操作，需要发送创角事件
            if (isNewUser) {
                getSpHelper().saveBoolean(WADemoConfig.SP_KEY_IS_FIRST_ENTER, false);
                if (!isNoCreateUserGame) {
                    delayCall(2, () -> {
                        // 需要创角的游戏，在创角后有游戏角色ID和昵称，才发送创角事件
                        String newGameUserId = TrackingEventActivity.getCurrentGameUserId();
                        String newNickname = TrackingEventActivity.getCurrentNickname();
                        long registerTime = System.currentTimeMillis(); // 创角时的时间戳，单位为毫秒(1970以后)，长度13位
                        WAUserCreateEvent userCreateEvent = new WAUserCreateEvent(serverId, newGameUserId, newNickname, registerTime);
                        WATrackProxy.trackEvent(MainActivity.this, userCreateEvent);
                    });
                } else {
                    // 不需要创角的游戏，进服后直接发送创角事件
                    long registerTime = System.currentTimeMillis(); // 创角时的时间戳，单位为毫秒(1970以后)，长度13位
                    WAUserCreateEvent userCreateEvent = new WAUserCreateEvent(serverId, gameUserId, nickname, registerTime);
                    WATrackProxy.trackEvent(MainActivity.this, userCreateEvent);
                }
            }

            // 玩家进服后申请通知权限
            WACommonProxy.requestNotificationPermission(MainActivity.this);
        });
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
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
        } else if (id == R.id.btn_csc) {
            // AiHelp客服
            startActivity(new Intent(this, AiHelpActivity.class));
        } else if (id == R.id.btn_open_game_review) {
            // 打开游戏评价
            openGameReview();
        } else if (id == R.id.btn_customer_center) {
            // 客服中心（包含账号管理，账号删除，AiHelp客服，用户中心，Consent设置入口）
            showCustomerCenter();
        } else if (id == R.id.btn_admob) {
            // AdMob 广告
            startActivity(new Intent(this, AdMobActivity.class));
        } else if (id == R.id.btn_social_func) {
            // 社交功能
            startActivity(new Intent(this, SocialActivity.class));
        } else if (id == R.id.btn_rare_function) {
            // 不常用功能
            startActivity(new Intent(this, RareFunctionActivity.class));
        }
    }

    private void showCustomerCenter() {
        if (isNotLoginAndTips()) return;

        // 账号管理回调处理
        WAAccountCallbackV2 accountManagerCallback = new WAAccountCallbackV2() {
            @Override
            public void onLoginAccountChanged(WALoginResultV2 currentAccount) {
                WASdkDemo.getInstance().updateLoginAccount(currentAccount);
                String text = "登录的账号发生变更（SDK已切换到另一个账号，或登录到新账号），当前新的登录的账号信息："
                        + "\nplatform:" + currentAccount.getPlatform()
                        + "\nuserId:" + currentAccount.getUserId()
                        + "\ntoken:" + currentAccount.getToken()
                        + "\nisBindAccount: " + currentAccount.getIsBindAccount()
                        + "\nisGuestAccount: " + currentAccount.getIsGuestAccount();

                showLongToast(text);
                logI(text);

                // 游戏需要回到登录界面，然后可以直接使用最新的账号信息完成游戏登录，无需重新走SDK登录过程，也可以重新自动走一遍SDK登录
                // backToLogin()
            }

            @Override
            public void onBoundAccountChanged(boolean binding, WABindResultV2 result) {
                String sb = "绑定账户信息发生变更（绑定或解绑其他平台账号成功）:" +
                        "\n" + "状态: " + (binding ? "绑定" : "解绑") +
                        "\n" + "code: " + result.getCode() +
                        "\n" + "message: " + result.getMessage() +
                        "\n" + "platform: " + result.getPlatform() +
                        "\n" + "email: " + result.getEmail() +
                        "\n" + "mobile: " + result.getMobile();

                showShortToast(sb);
                logI(sb);

                if (binding && WACallback.CODE_SUCCESS == result.getCode()) {
                    // 绑定成功，如果需要绑定账号发奖励，可以在这里处理
                } else if (!binding && WACallback.CODE_SUCCESS == result.getCode()) {
                    // 解绑成功
                }
            }

            @Override
            public void onRealNameAuthChanged(WAResult<WACertificationInfo> waResult) {
                // 忽略，无需处理
            }
        };
        // 账号删除回调处理
        WACallback<WAResult> accountDeleteCallback = new WACallback<WAResult>() {
            @Override
            public void onSuccess(int code, String message, WAResult result) {
                showLongToast("申请账号注销成功!\nCP需要退出游戏到登录页");
                // CP需要退出游戏到登录页
                WASdkDemo.getInstance().logout();
            }

            @Override
            public void onCancel() {
                showShortToast("取消");
            }

            @Override
            public void onError(int code, String message, @Nullable WAResult result, @Nullable Throwable throwable) {
                showShortToast("错误：" + code + " , " + message);
            }
        };

        WAUserProxy.showCustomerCenter(this, accountManagerCallback, accountDeleteCallback);
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
