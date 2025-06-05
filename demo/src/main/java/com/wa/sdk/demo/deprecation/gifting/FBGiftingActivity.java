package com.wa.sdk.demo.deprecation.gifting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentTabHost;

import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.deprecation.gifting.fragment.FBAskforGiftFragment;
import com.wa.sdk.demo.deprecation.gifting.fragment.FBGiftingFragment;
import com.wa.sdk.demo.deprecation.gifting.fragment.FBReceivedGiftFragment;
import com.wa.sdk.demo.widget.TabView;
import com.wa.sdk.demo.widget.TitleBar;


/**
 * 邀请好友
 */
public class FBGiftingActivity extends BaseActivity {

    private TitleBar mTitleBar;

    private FragmentTabHost mFtTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fb_gifting);

        initView();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!WACommonProxy.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initView() {

        mTitleBar = (TitleBar) findViewById(R.id.tb_gifting);
        mTitleBar.setTitleText(R.string.fb_gifting);
        mTitleBar.setLeftButton(android.R.drawable.ic_menu_revert, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleBar.setTitleTextColor(R.color.color_white);

        mContainerId = R.id.fl_gifting_content;

        mFtTabHost = (FragmentTabHost) findViewById(R.id.tabs_gifting);

        mFtTabHost.setup(this, mFragmentManager, mContainerId);

        addTabs();
    }

    private void addTabs() {
        TabView gifting = new TabView(this);
        gifting.setTitle(R.string.gifting);

        TabView receivedGift = new TabView(this);
        receivedGift.setTitle(R.string.received_gift);

        TabView askForGift = new TabView(this);
        askForGift.setTitle(R.string.ask_for_gift);

        mFtTabHost.addTab(mFtTabHost.newTabSpec("Gifting").setIndicator(gifting), FBGiftingFragment.class, null);

        mFtTabHost.addTab(mFtTabHost.newTabSpec("Received Gift").setIndicator(receivedGift), FBReceivedGiftFragment.class, null);

        mFtTabHost.addTab(mFtTabHost.newTabSpec("Ask for gift").setIndicator(askForGift), FBAskforGiftFragment.class, null);

        mFtTabHost.setCurrentTab(0);
    }


}
