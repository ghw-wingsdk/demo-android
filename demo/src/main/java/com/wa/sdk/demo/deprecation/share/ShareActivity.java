package com.wa.sdk.demo.deprecation.share;

import android.content.Intent;

import com.wa.sdk.demo.R;
import com.wa.sdk.demo.base.BaseGridActivity;

@Deprecated
public class ShareActivity extends BaseGridActivity {

    @Override
    protected int definedTitleResId() {
        return R.string.share;
    }

    @Override
    protected int[] definedButtonResIds() {
        return new int[]{R.string.fb_share};
    }

    @Override
    protected void onClickButton(int textResId) {
        if (textResId == R.string.fb_share) {
            startActivity(new Intent(this, FBShareActivity.class));
        }
    }

}
