package com.wa.sdk.demo;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.track.WATrackProxy;
import com.wa.sdk.track.model.WAInitiatedPurchaseEvent;
import com.wa.sdk.track.model.WALevelAchievedEvent;
import com.wa.sdk.track.model.WALvXEvent;
import com.wa.sdk.track.model.WATutorialCompletedEvent;
import com.wa.sdk.track.model.WAUserCreateEvent;
import com.wa.sdk.track.model.WAUserImportEvent;
import com.wa.sdk.track.model.WAUserInfoUpdateEvent;

/**
 * 数据采集
 */
public class TrackingEventActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_simulate);

        TitleBar titleBar = findViewById(R.id.tb_main);
        titleBar.setTitleText("事件");
        titleBar.setTitleTextColor(R.color.color_white);
        titleBar.setLeftButton(android.R.drawable.ic_menu_revert, v -> finish());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_user_import_first) {
            // 用户首次进服（非常重要）
            ghw_user_import_first(this);
        } else if (id == R.id.btn_user_import) {
            // 用户进服
            ghw_user_import(this);
        } else if (id == R.id.btn_user_create) {
            // 用户创角
            ghw_user_create(this);
        } else if (id == R.id.btn_initiated_purchase) {
            // 点击购买
            ghw_initiated_purchase(this);
        } else if (id == R.id.btn_level_achieved) {
            // 升级（每次升级都要发，跨等级时可以忽略中间等级）
            ghw_level_achieved(this);
        } else if (id == R.id.btn_user_info_update) {
            // 用户信息更新（更改角色名时必须发送该事件）
            ghw_user_info_update(this);
        } else if (id == R.id.btn_lv_x) {
            // 关键等级（具体等级需要跟运营确定）
            ghw_self_lv_x(this);
        } else if (id == R.id.btn_tutorial_completed) {
            // 新手任务完成（具体时机需要跟运营确定）
            ghw_self_tutorial_completed(this);
        }
        showShortToast("事件已发送");
    }


    private void ghw_user_import(Context context) {
        String serverId = "1";  // 服务器ID
        String gameUserId = "1000229"; // 游戏角色ID
        String nickName = "Lucy"; // 游戏角色昵称
        int level = 1; // 游戏角色等级
        boolean isFirstEnter = false; // 是否首次进服

        WAUserImportEvent event = new WAUserImportEvent(serverId, gameUserId, nickName, level, isFirstEnter);
        WATrackProxy.trackEvent(context, event);
    }

    private void ghw_user_import_first(Context context) {
        String serverId = "1";  // 服务器ID
        String gameUserId = "-1"; // 游戏角色ID，未创角时，可以传入 -1
        String nickName = ""; // 游戏角色昵称，未创角时，可以传入空字符串
        int level = 1; // 游戏角色等级
        boolean isFirstEnter = true; // 是否首次进服（很重要，首次进服一定要为true）

        WAUserImportEvent event = new WAUserImportEvent(serverId, gameUserId, nickName, level, isFirstEnter);
        WATrackProxy.trackEvent(context, event);
    }

    private void ghw_user_create(Context context) {
        String serverId = "1";  // 服务器ID
        String gameUserId = "1000229"; // 游戏角色ID
        String nickName = "Lucy"; // 游戏角色昵称
        long registerTime = System.currentTimeMillis(); // 角色创建时间

        WAUserCreateEvent event = new WAUserCreateEvent(serverId, gameUserId, nickName, registerTime);
        // 可选
        // event.setRoleType("射手");
        // event.setGender(0);
        // event.setVip(10);
        // event.setBindGameGold(99);
        // event.setGameGold(999);
        // event.setFighting(45427);
        // event.setStatus(false);
        WATrackProxy.trackEvent(context, event);
    }

    private void ghw_user_info_update(Context context) {
        String nickname = "Lucy"; // 角色昵称

        WAUserInfoUpdateEvent event = new WAUserInfoUpdateEvent(nickname);
        // 可选
        // event.setVip(10);
        // vent.setRoleType("射手");
        // vent.setStatus(false);
        WATrackProxy.trackEvent(context, event);
    }

    private void ghw_level_achieved(Context context) {
        int currentLevel = 12; // 当前等级

        WALevelAchievedEvent event = new WALevelAchievedEvent(currentLevel);
        // 可选
        // event.setFighting(45427);
        // event.setScore(1025456);
        WATrackProxy.trackEvent(context, event);
    }

    private void ghw_initiated_purchase(Context context) {
        WAInitiatedPurchaseEvent event = new WAInitiatedPurchaseEvent();
        WATrackProxy.trackEvent(context, event);
    }

    private void ghw_self_lv_x(Context context) {
        int level = 12; // 关键等级

        WALvXEvent event = new WALvXEvent(level);
        WATrackProxy.trackEvent(context, event);
    }

    private void ghw_self_tutorial_completed(Context context) {
        WATutorialCompletedEvent event = new WATutorialCompletedEvent();
        WATrackProxy.trackEvent(context, event);
    }
}
