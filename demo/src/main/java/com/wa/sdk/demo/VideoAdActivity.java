package com.wa.sdk.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.wa.sdk.WAConstants;
import com.wa.sdk.ad.WAAdProxy;
import com.wa.sdk.ad.model.WAAdCallback;
import com.wa.sdk.common.utils.LogUtil;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.widget.TitleBar;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 视频广告
 * Created by yinglovezhuzhu@gmail.com on 2017/8/21.
 */

public class VideoAdActivity extends BaseActivity {

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM/dd HH:mm:ss_sss", Locale.getDefault());

    private TextView mTvAmount;
    private TextView mTvDisplayMsg;

    private String mAmountFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_ad);

        mAmountFormat = getString(R.string.wa_cached_video_ad_amount_format);

        TitleBar titlebar= (TitleBar) findViewById(R.id.tb_video_ad);
        titlebar.setTitleText(R.string.wa_video_ad);
        titlebar.setLeftButton(android.R.drawable.ic_menu_revert, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });
        titlebar.setTitleTextColor(R.color.color_white);

        mTvAmount = (TextView) findViewById(R.id.tv_video_ad_amount);
        mTvDisplayMsg = (TextView) findViewById(R.id.tv_display_video_msg);

        mTvAmount.setText(String.format(Locale.getDefault(), mAmountFormat, 0));

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_get_cached_video_amount) {
            mTvAmount.setText(String.format(Locale.getDefault(), mAmountFormat, WAAdProxy.checkRewardedVideo()));
        } else if (id == R.id.btn_display_video_ad) {
            WAAdProxy.displayRewardedVideo(VideoAdActivity.this, new WAAdCallback() {
                @Override
                public void onPreDisplayRewardedVideo(String campaignId, String adSetId, int rewardAmount, String rewardType, String extra) {
                    String text = "进入广告页面(播放视频前)--onPreDisplayRewardedVideo()"
                            + "\ncampaignId = " + campaignId
                            + "\nadSetId = " + adSetId
                            + "\nrewardType = " + rewardType
                            + "\nrewardAmount = " + rewardAmount
                            + "\nextra = " + extra + "\n";
                    LogUtil.e(WAConstants.TAG, text);
                    showShortToast(text);
                }

                @Override
                public void onDisplayRewardedVideo(String campaignId, String adSetId, int rewardAmount, String rewardType, String extra) {
                    String text = "播放广告视频结束--onDisplayRewardedVideo()"
                            + "\ncampaignId = " + campaignId
                            + "\nadSetId = " + adSetId
                            + "\nrewardType = " + rewardType
                            + "\nrewardAmount = " + rewardAmount
                            + "\nextra = " + extra + "\n";
                    LogUtil.e(WAConstants.TAG, text);
                    showShortToast(text);
                }

                @Override
                public void onCancelRewardedVideo(int process, String campaignId, String adSetId, String extra) {
                    String text = process == 0 ? "播放视频前页面关闭广告" : "播放视频过程中关闭广告";
                    text += "--onCancelRewardedVideo()"
                            + "\nprocess = " + process
                            + "\ncampaignId = " + campaignId
                            + "\nadSetId = " + adSetId
                            + "\nextra = " + extra + "\n";
                    LogUtil.e(WAConstants.TAG, text);
                    showShortToast(text);
                }

                @Override
                public void onLoadRewardedVideoFail(String campaignId, String adSetId, String extra) {
                    String text = "加载广告视频失败--onLoadRewardedVideoFail()"
                            + "\ncampaignId = " + campaignId
                            + "\nadSetId = " + adSetId
                            + "\nextra = " + extra + "\n";
                    LogUtil.e(WAConstants.TAG, text);
                    showShortToast(text);
                }

                @Override
                public void onClickRewardedVideo(String campaignId, String adSetId, int rewardAmount, String rewardType, String extra) {
                    String text = "点击广告推广信息链接--onClickRewardedVideo()"
                            + "\ncampaignId = " + campaignId
                            + "\nadSetId = " + adSetId
                            + "\nrewardType = " + rewardType
                            + "\nrewardAmount = " + rewardAmount
                            + "\nextra = " + extra + "\n";
                    LogUtil.e(WAConstants.TAG, text);
                    showShortToast(text);
                }
            }, "extra");
        }
    }

    @Override
    public void exit() {
        finish();
    }
}
