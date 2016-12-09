package com.wa.sdk.demo.share;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wa.sdk.common.WASharedPrefHelper;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.WADemoConfig;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.widget.TitleBar;


public class ShareActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        TitleBar tb = (TitleBar) findViewById(R.id.tb_share);
        tb.setLeftButton(android.R.drawable.ic_menu_revert, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tb.setTitleText(R.string.share);
        tb.setTitleTextColor(R.color.color_white);

        WASharedPrefHelper sharedPrefHelper = WASharedPrefHelper.newInstance(this, WADemoConfig.SP_CONFIG_FILE_DEMO);
//        if (sharedPrefHelper.getBoolean("enable_logcat", true)) {
//            Logcat.enableLogcat(this);
//        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_fb_share:
                startActivity(new Intent(this, FBShareActivity.class));
                break;
            default:
                break;
        }
    }
}
