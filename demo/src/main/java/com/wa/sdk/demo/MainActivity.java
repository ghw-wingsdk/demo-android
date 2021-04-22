package com.wa.sdk.demo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;

import com.appsflyer.AppsFlyerLib;
import com.wa.sdk.WAConstants;
import com.wa.sdk.ad.WAAdProxy;
import com.wa.sdk.ad.model.WAAdCachedCallback;
import com.wa.sdk.apw.WAApwProxy;
import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.WASharedPrefHelper;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.model.WAPermissionCallback;
import com.wa.sdk.common.model.WAResult;
import com.wa.sdk.common.utils.LogUtil;
import com.wa.sdk.common.utils.StringUtil;
import com.wa.sdk.common.utils.ToastUtils;
import com.wa.sdk.core.WACoreProxy;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.community.CommunityActivity;
import com.wa.sdk.demo.game.GameServiceActivity;
import com.wa.sdk.demo.invite.InviteActivity;
import com.wa.sdk.demo.share.ShareActivity;
import com.wa.sdk.demo.tracking.TrackingActivity;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.pay.WAPayProxy;
import com.wa.sdk.pay.model.WAPurchaseResult;
import com.wa.sdk.user.WAUserProxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    private WASharedPrefHelper mSharedPrefHelper;

    private PendingAction mPendingAction = PendingAction.NONE;

    private EditText mEtSkuId;
    private boolean mPayInitialized = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            WACommonProxy.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE, true,
                    "如果您不允许WASdkDemo访问你的账户信息，您将无法使用Google登录",
                    "WASdkDemo需要获取您的联系人信息来登录您的Google账号", new WAPermissionCallback() {
                        @Override
                        public void onCancel() {
                            // TODO 取消授权
                            showShortToast("check permission canceled");
                        }

                        @Override
                        public void onRequestPermissionResult(String[] permissions, boolean[] grantedResults) {
                            // TODO 处理授权结果，判断是否通过授权
                            String msg = "Request permission result:\n";
                            if (permissions.length > 0) {
                                for (int i = 0; i < permissions.length; i++) {
                                    msg += permissions[i] + "--" + (grantedResults[i] ? "granted" : "denied");
                                }
                            }
                            showShortToast(msg);
                        }
                    });
        }
    };

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
                showLongToast("PayUIActitivy:Payment is successful.");
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


//        WACommonProxy.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS, true,
//                "如果您不允许WASdkDemo访问你的账户信息，您将无法使用Google登录",
//                "WASdkDemo需要获取您的联系人信息来登录您的Google账号", new WAPermissionCallback() {
//            @Override
//            public void onCancel() {
//                // TODO 取消授权
//                showShortToast("check permission canceled");
//                handler.sendEmptyMessage(0);
//            }
//
//            @Override
//            public void onRequestPermissionResult(String[] permissions, boolean[] grantedResults) {
//                // TODO 处理授权结果，判断是否通过授权
//                String msg = "Request permission result:\n";
//                if(permissions.length > 0) {
//                    for(int  i = 0; i < permissions.length; i++) {
//                        msg += permissions[i] + "--" + (grantedResults[i] ? "granted" : "denied");
//                    }
//                }
//                showShortToast(msg);
//                handler.sendEmptyMessage(0);
//            }
//        });


//        startActivity(new Intent(this, SplashActivity.class));

//        executeCommand("su");

//        new WAEvent.Builder().setDefaultEventName("lv01")
//                .build().track(this);


    }

    public static ArrayList<String> executeCommand(String... shellCmd) {
        String line = null;
        ArrayList<String> fullResponse = new ArrayList<String>();
        Process localProcess = null;
        try {
            LogUtil.i(LogUtil.TAG, "to shell exec which for find su :");
            localProcess = Runtime.getRuntime().exec(shellCmd);
        } catch (Exception e) {
            return null;
        }
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(localProcess.getOutputStream()));
        BufferedReader in = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));
        try {
            while ((line = in.readLine()) != null) {
                LogUtil.i(LogUtil.TAG, "–> Line received: " + line);
                fullResponse.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LogUtil.i(LogUtil.TAG, "–> Full response was: " + fullResponse);
        return fullResponse;
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
                testRequestPermission();
                break;
            case R.id.btn_login:
                login(false);
                break;
            case R.id.btn_account_manager:
                if (WASdkDemo.getInstance().isLogin()) {
                    accountManager();
                } else {
                    showLongToast("Not loginAccount! Please loginAccount first!");
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.warming)
                            .setMessage(R.string.not_login_yet)
                            .setPositiveButton(R.string.login_now, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mPendingAction = PendingAction.GO_ACCOUNT_MANAGER;
                                    login(true);
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                }
                break;
            case R.id.btn_pay:
                payment();
                break;
            case R.id.btn_static_pay:
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
                        showLongToast("Payment is successful.");
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
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle(R.string.warming)
                                    .setMessage(R.string.not_login_yet)
                                    .setPositiveButton(R.string.login_now, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            login(true);
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    })
                                    .show();
                        }
                        showLongToast(StringUtil.isEmpty(message) ? "Billing service is not available at this moment." : message);
                    }
                });
                break;
            case R.id.btn_tracking:
                testTracking();
                break;
            case R.id.btn_share:
                testShare();
                break;
            case R.id.btn_invite:
                invite();
                break;
//            case R.id.btn_gifting: // 礼物  //礼物功能已被废弃
//                startActivity(new Intent(this, GiftingActivity.class));
//                break;
            case R.id.btn_test_crash:
                testCrash();
                break;
            case R.id.btn_clear_campaign:
                clearCampaign();
                break;
            case R.id.btn_game_service:
                startActivity(new Intent(MainActivity.this, GameServiceActivity.class));
                break;
            case R.id.btn_update:
                startActivity(new Intent(this, UpdateActivity.class));
                break;
            case R.id.btn_community:
                startActivity(new Intent(this, CommunityActivity.class));
                break;
            case R.id.btn_video_ad:
                startActivity(new Intent(this, VideoAdActivity.class));
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
            case R.id.btn_create_client_id:
                String clientId = UUID.randomUUID().toString().replaceAll("-", "");
                WACoreProxy.setClientId(clientId);
                showShortToast(clientId);
                break;
            case R.id.btn_open_review:
                //不管回掉结果是什么，都需要统一当成成功处理后续逻辑
                WAUserProxy.openReview(this, new WACallback<Boolean>() {
                    @Override
                    public void onSuccess(int code, String message, Boolean result) {
                        showShortToast("api调用流程已经完成，无法获取用户是否评分，是否弹出评分框,"+message);
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(int code, String message, Boolean result, Throwable throwable) {


                    }
                });

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
            case R.id.btn_temp_test:
                ToastUtils.showLongToast(this,"暂无功能");
                break;
            default:
                break;
        }
    }

    private void testRequestPermission() {
        WACommonProxy.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE, true,
                "测试权限申请",
                "测试权限申请:READ_PHONE_STATE", new WAPermissionCallback() {
                    @Override
                    public void onCancel() {
                        showShortToast("check permission canceled");
                    }

                    @Override
                    public void onRequestPermissionResult(String[] permissions, boolean[] grantedResults) {
                        String msg = "Request permission result:\n";
                        if (permissions.length > 0) {
                            for (int i = 0; i < permissions.length; i++) {
                                msg += permissions[i] + "--" + (grantedResults[i] ? "granted" : "denied");
                            }
                        }
                        showShortToast(msg);
                    }
                });
    }

    String userId;
    ProgressDialog mLoadingDialog = null;

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

    /**
     * Facebook邀请点击
     */
    public void invite() {
        startActivity(new Intent(this, InviteActivity.class));
    }

    /**
     * Facebook分享
     */
    public void testShare() {
        startActivity(new Intent(this, ShareActivity.class));
    }


    /**
     * 闪退测试
     */
    public void testCrash() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.warming)
                .setMessage(R.string.test_crash_warming)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Util.testCrash();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void clearCampaign() {
//        GhwCampaignHelper.getInstance().clearCache(this);
//        Toast.makeText(this, "Campaign report cache clear success", Toast.LENGTH_SHORT).show();
    }

    private void initView() {

        TitleBar tb = (TitleBar) findViewById(R.id.tb_main);
        tb.setRightButton(android.R.drawable.ic_menu_close_clear_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tb.setTitleText(R.string.app_name);
        tb.setTitleTextColor(R.color.color_white);

        ToggleButton tbtnLogcat = (ToggleButton) findViewById(R.id.tbtn_logcat);
        boolean enableLogcat = mSharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_LOGCAT, true);
        tbtnLogcat.setChecked(enableLogcat);
        tbtnLogcat.setOnCheckedChangeListener(mOnCheckedChangeListener);

        ToggleButton tbtnExtend = (ToggleButton) findViewById(R.id.tbtn_app_wall);
        boolean enableExtend = mSharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_APW, true);
        tbtnExtend.setChecked(enableExtend);
        tbtnExtend.setOnCheckedChangeListener(mOnCheckedChangeListener);

        ToggleButton tbtnDebug = (ToggleButton) findViewById(R.id.tbtn_debug);
        boolean enableDebug = mSharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_DEBUG, true);
        tbtnDebug.setChecked(enableDebug);
        tbtnDebug.setOnCheckedChangeListener(mOnCheckedChangeListener);

        mEtSkuId = (EditText) findViewById(R.id.et_static_pay_sku_id);
        // FIXME 这里可以更改静态设置的购买商品id
        mEtSkuId.setText("123123");
    }

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
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
}
