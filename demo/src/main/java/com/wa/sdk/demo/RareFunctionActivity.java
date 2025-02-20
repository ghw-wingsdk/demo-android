package com.wa.sdk.demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

import com.wa.sdk.WAConstants;
import com.wa.sdk.apw.WAApwProxy;
import com.wa.sdk.common.WASharedPrefHelper;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.model.WAResult;
import com.wa.sdk.common.utils.LogUtil;
import com.wa.sdk.core.WAComponentFactory;
import com.wa.sdk.core.WACoreProxy;
import com.wa.sdk.core.WAICore;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.community.CommunityActivity;
import com.wa.sdk.demo.invite.InviteActivity;
import com.wa.sdk.demo.share.ShareActivity;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.wa.core.sdkadid.WASdkAdIdHelper;

/**
 * 使用频率较低的功能
 */
public class RareFunctionActivity extends BaseActivity {

    private WASharedPrefHelper mSharedPrefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rare_function);

        TitleBar tb = findViewById(R.id.tb_rare_function);
        tb.setRightButton(android.R.drawable.ic_menu_close_clear_cancel, v -> finish());
        tb.setTitleText(R.string.rare_function);
        tb.setTitleTextColor(R.color.color_white);

        mSharedPrefHelper = WASharedPrefHelper.newInstance(this, WADemoConfig.SP_CONFIG_FILE_DEMO);

        ToggleButton tbtnExtend = findViewById(R.id.tbtn_app_wall);
        boolean enableExtend = mSharedPrefHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_APW, false);
        if (enableExtend) WAApwProxy.showEntryFlowIcon(this);
        tbtnExtend.setChecked(enableExtend);
        tbtnExtend.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mSharedPrefHelper.saveBoolean(WADemoConfig.SP_KEY_ENABLE_APW, isChecked);
            if (isChecked) {
                WAApwProxy.showEntryFlowIcon(RareFunctionActivity.this);
            } else {
                WAApwProxy.hideEntryFlowIcon(RareFunctionActivity.this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_community) {
            startActivity(new Intent(this, CommunityActivity.class));
        } else if (id == R.id.btn_invite) {
            startActivity(new Intent(this, InviteActivity.class));
        } else if (id == R.id.btn_update) {
            startActivity(new Intent(this, UpdateActivity.class));
        } else if (id == R.id.btn_share) {
            startActivity(new Intent(this, ShareActivity.class));
        } else if (id == R.id.btn_test_crash) {
            testCrash();
        } else if (id == R.id.btn_clear_campaign) {
            clearCampaign();
        } else if (id == R.id.btn_video_ad) {
            startActivity(new Intent(this, VideoAdActivity.class));
        } else if (id == R.id.btn_clear_adid) {
            WASdkAdIdHelper.clear(this);
        } else if (id == R.id.btn_read_adid) {
            String sdkAdId = WASdkAdIdHelper.readSdkAdId(this);
            LogUtil.i(WAConstants.TAG, "自身:" + sdkAdId);
        } else if (id == R.id.btn_read_other_adid) {
            String otherApp = WASdkAdIdHelper.readOtherApp(this);
            LogUtil.i(WAConstants.TAG, "其他:" + otherApp);
        } else if (id == R.id.btn_write_adid) {
            boolean isSuccess = WASdkAdIdHelper.writeSdkAppId(this);
            LogUtil.i(WAConstants.TAG, "写入:" + isSuccess);
        } else if (id == R.id.btn_upload_install) {
            // 清除，上报
            final WASharedPrefHelper helper = WASharedPrefHelper.newInstance(this, "sp_campaign_cache");
            boolean isClear = helper.clearAll();
            LogUtil.i(WAConstants.TAG, "清除所有:" + isClear);
            WAICore coreComponent = (WAICore) WAComponentFactory.createComponent(WAConstants.CHANNEL_WA, WAConstants.MODULE_CORE);
            if (null != coreComponent) {
                coreComponent.reportInstallCampaign(this);
            }
        } else if (id == R.id.btn_show_open_url) {
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
        }
    }

    /**
     * 闪退测试
     */
    public void testCrash() {
        new AlertDialog.Builder(this).setTitle(R.string.warming).setMessage(R.string.test_crash_warming).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Util.testCrash();
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    private void clearCampaign() {
//        GhwCampaignHelper.getInstance().clearCache(this);
//        Toast.makeText(this, "Campaign report cache clear success", Toast.LENGTH_SHORT).show();
    }
}