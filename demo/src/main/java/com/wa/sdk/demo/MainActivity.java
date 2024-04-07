package com.wa.sdk.demo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;

import com.wa.sdk.WAConstants;
import com.wa.sdk.ad.WAAdProxy;
import com.wa.sdk.ad.model.WAAdCachedCallback;
import com.wa.sdk.apw.WAApwProxy;
import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.WASharedPrefHelper;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.model.WAResult;
import com.wa.sdk.common.utils.LogUtil;
import com.wa.sdk.common.utils.StringUtil;
import com.wa.sdk.core.WACoreProxy;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.game.GameServiceActivity;
import com.wa.sdk.demo.share.ShareActivity;
import com.wa.sdk.demo.tracking.TrackingActivity;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.pay.WAPayProxy;
import com.wa.sdk.pay.model.WAPurchaseResult;
import com.wa.sdk.user.WAUserProxy;
import com.wa.sdk.user.model.WAGameReviewCallback;
import com.wa.sdk.user.model.WALoginResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    private WASharedPrefHelper mSharedPrefHelper;

    private PendingAction mPendingAction = PendingAction.NONE;

    private EditText mEtSkuId;
    private EditText mEdtClientId;

    private boolean mPayInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        WACoreProxy.setDebugMode(true);
        WACoreProxy.initialize(this);
        // Demo的初始化，跟SDK无关
        WASdkDemo.getInstance().initialize(this);

        WAPayProxy.initialize(this, new WACallback<WAResult>() {

            @Override
            public void onSuccess(int code, String message, WAResult result) {
                LogUtil.d(TAG, "WAPayProxy.initialize success");
                mPayInitialized = true;
            }

            @Override
            public void onCancel() {
                LogUtil.d(TAG, "PayUIActitivy:WAPayProxy.initialize has been cancelled.");
                mPayInitialized = false;
            }

            @Override
            public void onError(int code, String message, WAResult result, Throwable throwable) {
                LogUtil.d(TAG, "WAPayProxy.initialize error");
                showLongToast("PayUIActitivy:Payment initialization fail.");
                mPayInitialized = false;
            }
        });

        mSharedPrefHelper = WASharedPrefHelper.newInstance(this, WADemoConfig.SP_CONFIG_FILE_DEMO);
        WACoreProxy.setDebugMode(mSharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_DEBUG, true));

        setContentView(R.layout.activity_main);

        initView();

        if (mSharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_LOGCAT, true)) {
            WACommonProxy.enableLogcat(this);
        } else {
            WACommonProxy.disableLogcat(this);
        }
        if (mSharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_APW, true)) {
            WAApwProxy.showEntryFlowIcon(this);
        }

        WAAdProxy.setAdCachedCallback(new WAAdCachedCallback() {
            @Override
            public void onVideoCached(int validVideoCount) {
                String text = "有新的广告缓存成功，当前可用广告数： " + validVideoCount;
                LogUtil.e(WAConstants.TAG, text);
                showShortToast(text);
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (null != bundle) {
            Set<String> keys = bundle.keySet();
            for (String key : keys) {
                LogUtil.e("MainActivity", "Key-------" + key);
            }
        }

//        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
//        if (targetUrl != null) {
//            Log.i("Activity", "App Link Target URL: " + targetUrl.toString());
//            showLongToast("App Link Target URL: " + targetUrl.toString());
//        }

        showHashKey(this);
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
                LogUtil.e("MainActivity", "Key-------" + key);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        WACommonProxy.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void showHashKey(Context context) {

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES); //Your            package name here
//            PackageInfo info = context.getPackageManager().getPackageInfo("com.proficientcity.nyjjh", PackageManager.GET_SIGNATURES); //Your            package name here

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                LogUtil.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            // do nothing
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // do nothing
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
//        Chartboost.onStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i(TAG, "---onResume---");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.i(TAG, "---onPause---");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        switch (v.getId()) {
            case R.id.btn_request_permission:
                startActivity(new Intent(this, PermissionActivity.class));
                break;
            case R.id.btn_login:
                login(false);
                break;
            case R.id.btn_account_manager:
                openAccountManager();
                break;
            case R.id.btn_pay:
                payment();
                break;
            case R.id.btn_static_pay:
                staticPay();
                break;
            case R.id.btn_tracking:
                testTracking();
                break;
            case R.id.btn_share:
                startActivity(new Intent(this, ShareActivity.class));
                break;
            case R.id.btn_game_service:
                startActivity(new Intent(MainActivity.this, GameServiceActivity.class));
                break;
            case R.id.btn_csc:
                startActivity(new Intent(this, CscActivity.class));
                break;
            case R.id.btn_privacy:
                startActivity(new Intent(this, PrivacyActivity.class));
                break;
            case R.id.btn_user_center:
                startActivity(new Intent(this, UserCenterActivity.class));
                break;
            case R.id.btn_random_client_id:
                String clientId = UUID.randomUUID().toString().replaceAll("-", "");
                mEdtClientId.setText(clientId);
                break;
            case R.id.btn_create_client_id:
                String strClientId = mEdtClientId.getText().toString();
                if (TextUtils.isEmpty(strClientId)) {
                    showShortToast("ClientId不能为空");
                } else {
                    WACoreProxy.setClientId(strClientId);
                    showShortToast("Client设置成功：" + strClientId);
                }
                break;
            case R.id.btn_open_review:
                openReview();
                break;
            case R.id.btn_account_deletion:
                startActivity(new Intent(MainActivity.this, UserDeletionActivity.class));
                break;
            case R.id.btn_display_app_version_info:
                //app 信息
                String info;
                String versionName = "版本名称：" + BuildConfig.VERSION_NAME;
                String versionCode = "代码版本：" + BuildConfig.VERSION_CODE;
                String buildType = "打包类型：" + BuildConfig.FLAVOR + "_" + BuildConfig.BUILD_TYPE;
                String buildTime = "打包时间：" + BuildConfig.DEMO_BUILD_TIME;
                String isTestRepository = "是否测试仓库包：" + (BuildConfig.IS_TEST_REPOSITORY ? "是" : "否");

                info = versionName + "\n"
                        + versionCode + "\n"
                        + buildType + "\n"
                        + buildTime + "\n"
                        + isTestRepository + "\n"
                ;
                new AlertDialog.Builder(this)
                        .setMessage(info)
                        .show();
                break;
            case R.id.btn_open_game_review:
                openGameReview();
                break;
            case R.id.btn_show_open_url:
                WACoreProxy.showOpenUrl(this, new WACallback<WAResult>() {
                    @Override
                    public void onSuccess(int code, String message, WAResult result) {
                        showShortToast("打开链接成功");
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(int code, String message, WAResult result, Throwable throwable) {
                        showShortToast("打开链接失败：" + code + "," + message);
                    }
                });
                break;
            case R.id.btn_rare_function:
                startActivity(new Intent(this, RareFunctionActivity.class));
                break;
            default:
                break;
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
                LogUtil.d(TAG, "pay error");
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
        startActivity(new Intent(this, TrackingActivity.class));
    }

    private void initView() {

        TitleBar tb = findViewById(R.id.tb_main);
        tb.setRightButton(android.R.drawable.ic_menu_close_clear_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tb.setTitleText(R.string.app_name);
        tb.setTitleTextColor(R.color.color_white);

        ToggleButton tbtnLogcat = findViewById(R.id.tbtn_logcat);
        boolean enableLogcat = mSharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_LOGCAT, true);
        tbtnLogcat.setChecked(enableLogcat);
        tbtnLogcat.setOnCheckedChangeListener(mOnCheckedChangeListener);

        ToggleButton tbtnExtend = findViewById(R.id.tbtn_app_wall);
        boolean enableExtend = mSharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_APW, true);
        tbtnExtend.setChecked(enableExtend);
        tbtnExtend.setOnCheckedChangeListener(mOnCheckedChangeListener);

        ToggleButton tbtnDebug = findViewById(R.id.tbtn_debug);
        boolean enableDebug = mSharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_DEBUG, true);
        tbtnDebug.setChecked(enableDebug);
        tbtnDebug.setOnCheckedChangeListener(mOnCheckedChangeListener);

        mEtSkuId = findViewById(R.id.et_static_pay_sku_id);
        // FIXME 这里可以更改静态设置的购买商品id
        mEtSkuId.setText("123123");

        mEdtClientId = findViewById(R.id.edt_client_id);
    }

    private final CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.tbtn_logcat:
                    mSharedPrefHelper.saveBoolean(WADemoConfig.SP_KEY_ENABLE_LOGCAT, isChecked);
                    if (isChecked) {
                        WACommonProxy.enableLogcat(MainActivity.this);
                    } else {
                        WACommonProxy.disableLogcat(MainActivity.this);
                    }
                    break;
                case R.id.tbtn_app_wall:
                    mSharedPrefHelper.saveBoolean(WADemoConfig.SP_KEY_ENABLE_APW, isChecked);
                    if (isChecked) {
                        WAApwProxy.showEntryFlowIcon(MainActivity.this);
                    } else {
                        WAApwProxy.hideEntryFlowIcon(MainActivity.this);
                    }
                    break;
                case R.id.tbtn_debug:
                    mSharedPrefHelper.saveBoolean(WADemoConfig.SP_KEY_ENABLE_DEBUG, isChecked);
                    WACoreProxy.setDebugMode(isChecked);
                    break;
                default:
                    break;
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
