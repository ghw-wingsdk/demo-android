package com.wa.sdk.demo.deprecation.gifting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wa.sdk.demo.R;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.widget.TitleBar;

/**
 * 礼物总入口
 */
@Deprecated
public class GiftingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gifting);

        initView();
    }


    private void initView() {
        TitleBar tb = (TitleBar) findViewById(R.id.tb_gifting);
        tb.setLeftButton(android.R.drawable.ic_menu_revert, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tb.setTitleText(R.string.gifting);
        tb.setTitleTextColor(R.color.color_white);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btn_fb_gifting) { //
            startActivity(new Intent(this, FBGiftingActivity.class));
        }
    }

}
