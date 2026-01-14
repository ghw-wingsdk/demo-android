package com.wa.sdk.demo;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.utils.WADemoConfig;
import com.wa.sdk.demo.utils.WASdkDemo;
import com.wa.sdk.track.WATrackProxy;
import com.wa.sdk.track.model.WACustomEvent;
import com.wa.sdk.track.model.WAInitiatedPurchaseEvent;
import com.wa.sdk.track.model.WALevelAchievedEvent;
import com.wa.sdk.track.model.WALvXEvent;
import com.wa.sdk.track.model.WAStageEvent;
import com.wa.sdk.track.model.WATutorialCompletedEvent;
import com.wa.sdk.track.model.WAUserCreateEvent;
import com.wa.sdk.track.model.WAUserImportEventV2;
import com.wa.sdk.track.model.WAUserInfoUpdateEvent;
import com.wa.sdk.user.model.WALoginResultV2;

/**
 * 数据采集
 */
public class TrackingEventActivity extends BaseActivity {
    private static final String DEFAULT_NICKNAME = "Lucy-" + Build.MODEL.replace(" ", "-");
    private static final String DEFAULT_SERVER_ID = "s1";
    private static final String DEFAULT_LEVEL = "1";

    private EditText mEdtCurrentServerId;
    private EditText mEdtCurrentLevel;
    private EditText mEdtCurrentNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_event);
        setTitleBar(R.string.tracking);

        mEdtCurrentServerId = findViewById(R.id.edt_current_server_id);
        mEdtCurrentLevel = findViewById(R.id.edt_current_level);
        mEdtCurrentNickname = findViewById(R.id.edt_current_nickname);
        mEdtCurrentLevel.setText("" + getCurrentLevel());
        mEdtCurrentServerId.setText(getCurrentServerId());
        mEdtCurrentNickname.setText(getCurrentNickname());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (clickSetInfo(id)) return;

        if (id == R.id.btn_user_import) {
            // 用户进服（新版V2）
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
        String serverId = getCurrentServerId();  // 服务器ID
        String gameUserId = getCurrentGameUserId(); // 游戏角色ID
        String nickName = getCurrentNickname(); // 游戏角色昵称
        int level = getCurrentLevel(); // 游戏角色当前等级

        WAUserImportEventV2 event = new WAUserImportEventV2(serverId, gameUserId, nickName, level);
        WATrackProxy.trackEvent(context, event);
    }

    private void ghw_user_create(Context context) {
        String serverId = getCurrentServerId();  // 服务器ID
        String gameUserId = getCurrentGameUserId(); // 游戏角色ID
        String nickName = getCurrentNickname(); // 游戏角色昵称
        long registerTime = System.currentTimeMillis(); // 角色创建时的时间戳，单位为毫秒(1970以后)，长度13位

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
        String nickname = getCurrentNickname(); // 角色昵称

        WAUserInfoUpdateEvent event = new WAUserInfoUpdateEvent(nickname);
        // 可选
        // event.setVip(10);
        // vent.setRoleType("射手");
        // vent.setStatus(false);
        WATrackProxy.trackEvent(context, event);
    }

    private void ghw_level_achieved(Context context) {
        int currentLevel = getCurrentLevel(); // 当前等级

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
        int level = getCurrentLevel(); // 关键等级

        WALvXEvent event = new WALvXEvent(level);
        WATrackProxy.trackEvent(context, event);
    }

    private void ghw_self_tutorial_completed(Context context) {
        WATutorialCompletedEvent event = new WATutorialCompletedEvent();
        WATrackProxy.trackEvent(context, event);
    }

    private void ghw_self_stage_x(Context context) {
        String stage = "2_3"; // 关卡
        WAStageEvent event = new WAStageEvent(stage);
        WATrackProxy.trackEvent(context, event);
    }

    private void ghw_self_x(Context context) {
        String event = "test_event"; // 自定义事件的名称
        WACustomEvent customEvent = new WACustomEvent(event);
        WATrackProxy.trackEvent(context, customEvent);
    }


    private boolean clickSetInfo(int id) {
        if (id == R.id.btn_set_server_id) {
            String serverId = mEdtCurrentServerId.getText().toString();
            serverId = TextUtils.isEmpty(serverId) ? DEFAULT_SERVER_ID : serverId;
            getSpHelper().saveString(WADemoConfig.SP_KEY_CURRENT_SERVER_ID, serverId);

            mEdtCurrentServerId.setText(serverId);
            hideKeyboard();
            showShortToast("设置成功，当前服务器ID：" + serverId);
            return true;
        } else if (id == R.id.btn_set_level) {
            String level = mEdtCurrentLevel.getText().toString();
            level = TextUtils.isEmpty(level) ? DEFAULT_LEVEL : level;
            getSpHelper().saveString(WADemoConfig.SP_KEY_CURRENT_LEVEL, level);

            mEdtCurrentLevel.setText(level);
            hideKeyboard();
            showShortToast("设置成功，当前等级：" + level);
            return true;
        } else if (id == R.id.btn_set_nickname) {
            String nickname = mEdtCurrentNickname.getText().toString();
            nickname = TextUtils.isEmpty(nickname) ? DEFAULT_NICKNAME : nickname;
            getSpHelper().saveString(WADemoConfig.SP_KEY_CURRENT_NICKNAME, nickname);

            mEdtCurrentNickname.setText(nickname);
            hideKeyboard();
            showShortToast("设置成功，当前昵称：" + nickname);
            return true;
        }
        return false;
    }

    public static int getCurrentLevel() {
        return Integer.parseInt(WASdkDemo.getInstance().getSpHelper().getString(WADemoConfig.SP_KEY_CURRENT_LEVEL, DEFAULT_LEVEL));
    }

    public static String getCurrentServerId() {
        return WASdkDemo.getInstance().getSpHelper().getString(WADemoConfig.SP_KEY_CURRENT_SERVER_ID, DEFAULT_SERVER_ID);
    }

    public static String getCurrentGameUserId() {
        WALoginResultV2 account = WASdkDemo.getInstance().getLoginAccount();
        String userId = account != null ? account.getUserId() : "-1";
        return getCurrentServerId() + "-role1-" + userId;
    }

    public static String getCurrentNickname() {
        return WASdkDemo.getInstance().getSpHelper().getString(WADemoConfig.SP_KEY_CURRENT_NICKNAME, DEFAULT_NICKNAME);
    }

}
