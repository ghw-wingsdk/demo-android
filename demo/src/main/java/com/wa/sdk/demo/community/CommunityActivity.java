package com.wa.sdk.demo.community;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wa.sdk.demo.R;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.widget.TitleBar;

/**
 * 社区/群组等功能测试界面
 * Created by yinglovezhuzhu@gmail.com on 2016/7/14.
 */
public class CommunityActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_community);

        initView();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_vk_community:
                startActivity(new Intent(this, VKCommunityActivity.class));
                break;
            default:
                break;
        }
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.tb_community);
        titleBar.setTitleText(R.string.community);
        titleBar.setLeftButton(android.R.drawable.ic_menu_revert, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.setTitleTextColor(R.color.color_white);
    }
}
