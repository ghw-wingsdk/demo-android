package com.wa.sdk.demo.deprecation;

import android.content.Intent;
import android.os.Bundle;

import com.wa.sdk.demo.R;
import com.wa.sdk.demo.base.BaseGridActivity;
import com.wa.sdk.demo.deprecation.community.CommunityActivity;
import com.wa.sdk.demo.deprecation.gifting.GiftingActivity;
import com.wa.sdk.demo.deprecation.invite.InviteActivity;
import com.wa.sdk.demo.deprecation.share.ShareActivity;

@Deprecated
public class DeprecationFunctionActivity extends BaseGridActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 应用墙
        // WASharedPrefHelper spfHelper = WASharedPrefHelper.newInstance(this, WADemoConfig.SP_CONFIG_FILE_DEMO);
        // ToggleButton tbtnExtend = findViewById(R.id.tbtn_app_wall);
        // boolean enableExtend = spfHelper.getBoolean(WADemoConfig.SP_KEY_ENABLE_APW, false);
        // if (enableExtend) WAApwProxy.showEntryFlowIcon(this);
        // tbtnExtend.setChecked(enableExtend);
        // tbtnExtend.setOnCheckedChangeListener((buttonView, isChecked) -> {
        //     spfHelper.saveBoolean(WADemoConfig.SP_KEY_ENABLE_APW, isChecked);
        //     if (isChecked) {
        //         WAApwProxy.showEntryFlowIcon(DeprecationFunctionActivity.this);
        //     } else {
        //         WAApwProxy.hideEntryFlowIcon(DeprecationFunctionActivity.this);
        //     }
        // });
    }

    @Override
    protected int definedTitleResId() {
        return R.string.deprecation_function;
    }

    @Override
    protected int[] definedButtonResIds() {
        return new int[]{R.string.wa_video_ad, R.string.update, R.string.share, R.string.community, R.string.invite, R.string.gifting, R.string.update};
    }

    @Override
    protected void onClickButton(int textResId) {
        if (textResId == R.string.wa_video_ad) {
            startActivity(new Intent(this, VideoAdActivity.class));
        } else if (textResId == R.string.update) {
            startActivity(new Intent(this, UpdateActivity.class));
        } else if (textResId == R.string.share) {
            startActivity(new Intent(this, ShareActivity.class));
        } else if (textResId == R.string.community) {
            startActivity(new Intent(this, CommunityActivity.class));
        } else if (textResId == R.string.invite) {
            startActivity(new Intent(this, InviteActivity.class));
        } else if (textResId == R.string.gifting) {
            startActivity(new Intent(this, GiftingActivity.class));
        } else if (textResId == R.string.update) {
            startActivity(new Intent(this, UpdateActivity.class));
        }
    }


}