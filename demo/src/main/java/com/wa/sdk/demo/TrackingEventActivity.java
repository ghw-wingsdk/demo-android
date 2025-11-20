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
import com.wa.sdk.track.model.WAUserImportEvent;
import com.wa.sdk.track.model.WAUserImportEventV2;
import com.wa.sdk.track.model.WAUserInfoUpdateEvent;
import com.wa.sdk.user.model.WALoginResultV2;

/**
 * 数据采集
 */
public class TrackingEventActivity extends BaseActivity {

    private EditText mEdtCurrentServerId;
    private EditText mEdtCurrentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_simulate);
        setTitleBar(R.string.tracking);

        mEdtCurrentServerId = findViewById(R.id.edt_current_server_id);
        mEdtCurrentLevel = findViewById(R.id.edt_current_level);
        mEdtCurrentLevel.setText("" + getCurrentLevel());
        mEdtCurrentServerId.setText(getCurrentServerId());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (clickSetInfo(id)) return;

        if (id == R.id.btn_user_import_first) {
            // 用户首次进服（旧版，已经废弃）
            ghw_user_import_first(this);
        } else if (id == R.id.btn_user_import) {
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

    private void ghw_user_import_first(Context context) {
        String serverId = getCurrentServerId();  // 服务器ID
        String gameUserId = "-1"; // 游戏角色ID，未创角时，可以传入 -1
        String nickName = ""; // 游戏角色昵称，未创角时，可以传入空字符串
        int level = getCurrentLevel(); // 游戏角色等级，未创角时，填入游戏角色初始等级，一般为 1
        boolean isFirstEnter = true;

        WAUserImportEvent event = new WAUserImportEvent(serverId, gameUserId, nickName, level, isFirstEnter);
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
            serverId = TextUtils.isEmpty(serverId) ? "s1" : serverId;
            getSpHelper().saveString(WADemoConfig.SP_KEY_CURRENT_SERVER_ID, serverId);

            mEdtCurrentServerId.setText(serverId);
            hideKeyboard();
            showShortToast("设置成功，当前服务器ID：" + serverId);
            return true;
        } else if (id == R.id.btn_set_level) {
            String level = mEdtCurrentLevel.getText().toString();
            level = TextUtils.isEmpty(level) ? "1" : level;
            getSpHelper().saveString(WADemoConfig.SP_KEY_CURRENT_LEVEL, level);

            mEdtCurrentLevel.setText(level);
            hideKeyboard();
            showShortToast("设置成功，当前等级：" + level);
            return true;
        }
        return false;
    }

    public static int getCurrentLevel() {
        return Integer.parseInt(WASdkDemo.getInstance().getSpHelper().getString(WADemoConfig.SP_KEY_CURRENT_LEVEL, "1"));
    }


    public static String getCurrentServerId() {
        return WASdkDemo.getInstance().getSpHelper().getString(WADemoConfig.SP_KEY_CURRENT_SERVER_ID, "s1");
    }


    public static String getCurrentGameUserId() {
        WALoginResultV2 account = WASdkDemo.getInstance().getLoginAccount();
        String userId = account != null ? account.getUserId() : "-1";
        return getCurrentServerId() + "-role1-" + userId;
    }


    public static String getCurrentNickname() {
        return "Lucy-" + Build.MODEL.replace(" ", "-");
    }

}
