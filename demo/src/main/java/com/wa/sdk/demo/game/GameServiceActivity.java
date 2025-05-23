package com.wa.sdk.demo.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wa.sdk.demo.R;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.widget.TitleBar;

/**
 * Game Service入口Activity
 * 
 */
public class GameServiceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_service);

        initView();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btn_gg_game_service) {
            startActivity(new Intent(this, GoogleGameActivity.class));
        }
    }

    private void initView() {
        TitleBar tb = (TitleBar) findViewById(R.id.tb_game_service);
        tb.setLeftButton(android.R.drawable.ic_menu_revert, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tb.setTitleText(R.string.game_service);
        tb.setTitleTextColor(R.color.color_white);
    }
}
