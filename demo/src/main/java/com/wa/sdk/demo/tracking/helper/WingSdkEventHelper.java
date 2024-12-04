package com.wa.sdk.demo.tracking.helper;

import android.content.Context;

import com.wa.sdk.track.WATrackProxy;
import com.wa.sdk.track.model.WAInitiatedPurchaseEvent;
import com.wa.sdk.track.model.WALevelAchievedEvent;
import com.wa.sdk.track.model.WALvXEvent;
import com.wa.sdk.track.model.WAPurchaseEvent;
import com.wa.sdk.track.model.WATutorialCompletedEvent;
import com.wa.sdk.track.model.WAUserCreateEvent;
import com.wa.sdk.track.model.WAUserImportEvent;
import com.wa.sdk.track.model.WAUserInfoUpdateEvent;

public class WingSdkEventHelper {

    public static void ghw_user_import(Context context) {
        String serverId = "1";  // 服务器ID
        String gameUserId = "1000229"; // 游戏角色ID
        String nickName = "Lucy"; // 游戏角色昵称
        int level = 1; // 游戏角色等级
        boolean isFirstEnter = false; // 是否首次进服

        WAUserImportEvent event = new WAUserImportEvent(serverId, gameUserId, nickName, level, isFirstEnter);
        WATrackProxy.trackEvent(context, event);
    }

    public static void ghw_user_create(Context context) {
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

    public static void ghw_user_info_update(Context context) {
        String nickname = "Lucy"; // 角色昵称

        WAUserInfoUpdateEvent event = new WAUserInfoUpdateEvent(nickname);
        // 可选
        // event.setVip(10);
        // vent.setRoleType("射手");
        // vent.setStatus(false);
        WATrackProxy.trackEvent(context, event);
    }

    public static void ghw_level_achieved(Context context) {
        int currentLevel = 12; // 当前等级

        WALevelAchievedEvent event = new WALevelAchievedEvent(currentLevel);
        // 可选
        // event.setFighting(45427);
        // event.setScore(1025456);
        WATrackProxy.trackEvent(context, event);
    }

    public static void ghw_initiated_purchase(Context context) {
        WAInitiatedPurchaseEvent event = new WAInitiatedPurchaseEvent();
        WATrackProxy.trackEvent(context, event);
    }

    public static void ghw_purchase(Context context) {
        String itemName = "wood_01"; // 商品名称 或 商品ID
        int itemAmount = 1; // 购买数量
        float price = 999f; // 交易价格

        WAPurchaseEvent event = new WAPurchaseEvent(itemName, itemAmount, price);
        WATrackProxy.trackEvent(context, event);
    }

    public static void ghw_self_lv_x(Context context) {
        int level = 12; // 关键等级

        WALvXEvent event = new WALvXEvent(level);
        WATrackProxy.trackEvent(context, event);
    }

    public static void ghw_self_tutorial_completed(Context context) {
        WATutorialCompletedEvent event = new WATutorialCompletedEvent();
        WATrackProxy.trackEvent(context, event);
    }

}
