package com.wa.sdk.demo.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.widget.TitleBar;

public class BaseGridActivity extends BaseActivity {

    protected int title = 0;
    protected int[] titles = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view);

        initViews();
    }

    protected void initViews() {
        // 设置顶部标题
        TitleBar titlebar = findViewById(R.id.tb_title);

        if (title != 0)
            titlebar.setTitleText(title);

        titlebar.setLeftButton(android.R.drawable.ic_menu_revert, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });
        titlebar.setTitleTextColor(R.color.color_white);

        // 设置内容
        LinearLayout llContent = findViewById(R.id.ll_content);

        int rowCount = titles.length / 2 + titles.length % 2;
        for (int i = 0; i < rowCount; i++) {
            if (i * 2 + 1 >= titles.length)
                addView(llContent, titles[i * 2], -1);
            else
                addView(llContent, titles[i * 2], titles[i * 2 + 1]);
        }

    }

    protected void addView (ViewGroup superView, int title1, int title2) {
        LinearLayout llItem = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_activity_view, null);
        superView.addView(llItem);

        Button btn1 = llItem.findViewById(R.id.btn_1);
        btn1.setTag(title1);
        btn1.setText(title1);
        btn1.setOnClickListener(this);

        /**
         *  title2  为 -1 时第二个按钮隐藏占位
         *          为 0 时第二个按钮隐藏不占位（即第一个按钮全行显示）
         */
        Button btn2 = llItem.findViewById(R.id.btn_2);
        if (title2 == -1) {
            btn2.setVisibility(View.INVISIBLE);
        } else if (title2 == 0) { //
            btn2.setVisibility(View.GONE);
        } else {
            btn2.setTag(title2);
            btn2.setText(title2);
            btn2.setOnClickListener(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!WACommonProxy.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void exit() {
        finish();
    }
}
