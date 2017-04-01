package com.wa.sdk.demo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.wa.sdk.apw.WAApwProxy;
import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.WASharedPrefHelper;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.model.WAResult;
import com.wa.sdk.common.utils.LogUtil;
import com.wa.sdk.common.utils.StringUtil;
import com.wa.sdk.core.WACoreProxy;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.community.CommunityActivity;
import com.wa.sdk.demo.game.GameServiceActivity;
import com.wa.sdk.demo.gifting.GiftingActivity;
import com.wa.sdk.demo.invite.InviteActivity;
import com.wa.sdk.demo.share.ShareActivity;
import com.wa.sdk.demo.tracking.TrackingActivity;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.pay.WAPayProxy;
import com.wa.sdk.pay.model.WAPurchaseResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import bolts.AppLinks;

//import bolts.AppLinks;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    private WASharedPrefHelper mSharedPrefHelper;

    private PendingAction mPendingAction = PendingAction.NONE;

    private EditText mEtSkuId;
    private boolean mPayInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        WACoreProxy.setClientId("client123456789");
        WACoreProxy.setDebugMode(true);
        WACoreProxy.initialize(this);

        // Demo的初始化，跟SDK无关
        WASdkDemo.getInstance().initialize(this);

        WAPayProxy.initialize(this, new WACallback<WAResult>(){

            @Override
            public void onSuccess(int code, String message, WAResult result) {
                LogUtil.d(TAG,"WAPayProxy.initialize success");
                showLongToast("PayUIActitivy:Payment is successful.");
                mPayInitialized = true;
            }

            @Override
            public void onCancel() {
                LogUtil.d(TAG,"PayUIActitivy:WAPayProxy.initialize has been cancelled.");
                mPayInitialized = false;
            }

            @Override
            public void onError(int code, String message, WAResult result, Throwable throwable) {
                LogUtil.d(TAG,"WAPayProxy.initialize error");
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

//        IInAppBillingService.Stub.

        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null) {
            Log.i("Activity", "App Link Target URL: " + targetUrl.toString());
            showLongToast("App Link Target URL: " + targetUrl.toString());
        }

        showHashKey(this);

//        WACommonProxy.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS, new WAPermissionCallback() {
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onRequestPermissionResult(String[] permissions, boolean[] grantedResults) {
//
//            }
//        });
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

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            // do nothing
        } catch (NoSuchAlgorithmException e) {
            // do nothing
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
        Log.i(TAG, "---onResume---");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "---onPause---");
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
                if(RESULT_OK == resultCode) {
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
            case R.id.btn_login:
                login(false);
                break;
            case R.id.btn_account_manager:
                if (WASdkDemo.getInstance().isLogin()) {
                    accountManager();
                } else {
                    showLongToast("Not login! Please login first!");
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
                if(!mPayInitialized) {
                    showShortToast("Payment not initialize!");
                    return;
                }
                final String skuId = mEtSkuId.getText().toString().trim();
                if(StringUtil.isEmpty(skuId)) {
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
                        showLongToast("Billing service is not available at this moment.");
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
            case R.id.btn_gifting: // 礼物
                startActivity(new Intent(this, GiftingActivity.class));
                break;
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
            default:
                break;
        }
    }

    String userId;
    ProgressDialog mLoadingDialog = null;

    private void payment(){
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
