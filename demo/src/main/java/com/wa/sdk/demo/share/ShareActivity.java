package com.wa.sdk.demo.share;

import android.content.Intent;
import android.view.View;

import com.wa.sdk.demo.R;
import com.wa.sdk.demo.base.BaseGridActivity;


public class ShareActivity extends BaseGridActivity {

    @Override
    protected void initViews() {
        title = R.string.share;
        titles = new int[]{R.string.fb_share};

        super.initViews();
    }

    @Override
    public void onClick(View v) {
        int tag = (int) v.getTag();

        switch (tag) {
            case R.string.fb_share:
                startActivity(new Intent(this, FBShareActivity.class));
                break;
            default:
                break;
        }
    }
}
