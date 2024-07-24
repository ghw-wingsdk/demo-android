package com.wa.sdk.demo.tracking;

import android.os.Bundle;
import android.view.View;

import com.wa.sdk.core.WACoreProxy;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.track.WAEventParameterName;
import com.wa.sdk.track.WAEventType;
import com.wa.sdk.track.WATrackProxy;
import com.wa.sdk.track.model.WAEvent;

public class TrackingSimulateActivity extends BaseActivity {

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
        if (id == R.id.btn_user_import) {/* ghw_user_import  导入用户
                    事件描述    : 玩家登录游戏服
                    事件作用    : 记录玩家登录游戏服的动作，后台根据该事件统计导入数、登录数、导入留存等数据
                    建议触发点  : 玩家登录游戏服成功后
                    调用前提    : 需要先调用setServerId、setGameUserId接口
                    必填字段    : isFirstEnter  类型int  是否第一次进服 0→否 1→是； 默认为0
                 */

            // 调用前，需要先设置setServerId、setGameUserId
            WACoreProxy.setServerId("serverid001");     //设置用户选择的服务器id
            WACoreProxy.setGameUserId("gameUserId001");// 设置游戏的用户id

            WAEvent event = new WAEvent.Builder()
                    .setDefaultEventName(WAEventType.IMPORT_USER)
                    //必填字段
                    .addDefaultEventValue(WAEventParameterName.IS_FIRST_ENTER, 0) //是否第一次进服 0→否 1→是； 默认为0
                    .build();
            WATrackProxy.trackEvent(this, event);
        } else if (id == R.id.btn_user_create) {/* ghw_user_create  创角事件
                    事件描述    : 玩家创建角色
                    事件作用    : 记录玩家创建角色的动作，后台根据该事件统计创角数
                    建议触发点  : 玩家创建角色成功后
                    调用前提    : 需要先调用setServerId、setGameUserId、setLevel接口
                    必填字段    : nickname      昵称
                                 registerTime  注册时间戳 单位为毫秒(1970以后)
                    可选字段    : roleType、gender、vip、bindGameGold、gameGold、fighting、status 具体参考博客
                */

            // 调用前，需要先设置setServerId、setGameUserId、setLevel
            WACoreProxy.setServerId("serverid001");     //设置用户选择的服务器id
            WACoreProxy.setGameUserId("gameUserId001");// 设置游戏的用户id
            WACoreProxy.setLevel(1);                   // 设置用户等级

            WAEvent event = new WAEvent.Builder()
                    .setDefaultEventName(WAEventType.USER_CREATED)
                    //必填字段
                    .addDefaultEventValue(WAEventParameterName.NICKNAME, "总裁女友1222") //昵称
                    .addDefaultEventValue(WAEventParameterName.REGISTER_TIME, System.currentTimeMillis())// 注册时间戳，单位为毫秒(1970以后)
                    //可选
                    //.addDefaultEventValue(WAEventParameterName.ROLE_TYPE, 1)
                    //.addDefaultEventValue(WAEventParameterName.GENDER, 1)
                    //.addDefaultEventValue(WAEventParameterName.VIP, 1)
                    //.addDefaultEventValue(WAEventParameterName.BINDED_GAME_GOLD, 10000)
                    //.addDefaultEventValue(WAEventParameterName.GAME_GOLD, 100)
                    //.addDefaultEventValue(WAEventParameterName.FIGHTING, 1230020)
                    //.addDefaultEventValue(WAEventParameterName.STATUS, 1)
                    .build();

            WATrackProxy.trackEvent(this, event);
        } else if (id == R.id.btn_initiated_purchase) {/* ghw_initiated_purchase 点击购买
                    事件描述    : 点击购买（虚拟货币）
                    事件作用    : 用于游戏内部虚拟交易统计
                    建议触发点  : 点击购买的时候调用
                    调用前提    : 无
                    必填字段    : 无
                */
            WAEvent event = new WAEvent.Builder()
                    .setDefaultEventName(WAEventType.INITIATED_PURCHASE)
                    .build();
            WATrackProxy.trackEvent(this, event);
        } else if (id == R.id.btn_purchase) {/* ghw_purchase 购买完成
                    事件描述    : 购买完成（虚拟货币）
                    事件作用    : 用于游戏内部虚拟交易统计
                    建议触发点  : 购买完成的时候调用
                    调用前提    : 无
                    必填字段    : itemName      String    游戏内虚拟物品的名称/ID
                                 itemAmount    int       交易的数量
                                 price         float     交易的总价
                 */
            WAEvent event = new WAEvent.Builder()
                    .setDefaultEventName(WAEventType.COMPLETE_PURCHASE)
                    //必选字段
                    .addDefaultEventValue(WAEventParameterName.ITEM_NAME, "GGGGG")
                    .addDefaultEventValue(WAEventParameterName.ITEM_AMOUNT, 1)
                    .addDefaultEventValue(WAEventParameterName.PRICE, 50)
                    .build();
            WATrackProxy.trackEvent(this, event);
        } else if (id == R.id.btn_level_achieved) {/* ghw_level_achieved  更新玩家等级
                    事件描述    : 更新玩家等级
                    事件作用    : 更新玩家等级，后台根据此字段更新玩家等级
                    建议触发点  : 玩家达到新的等级时
                    调用前提    : 需要先调用setLevel接口更新玩家等级
                    必填字段    : 无
                    可选字段    : score      int     账户分数
                                 fighting   int     战斗力
                 */

            WACoreProxy.setLevel(3);// 设置用户等级
            WAEvent event = new WAEvent.Builder()
                    .setDefaultEventName(WAEventType.LEVEL_ACHIEVED)
                    //可选字段
                    //.addDefaultEventValue(WAEventParameterName.SCORE, 3241234)
                    //.addDefaultEventValue(WAEventParameterName.FIGHTING, 10)
                    .build();
            WATrackProxy.trackEvent(this, event);
        } else if (id == R.id.btn_user_info_update) {/* ghw_user_info_update 更新用户信息
                    事件描述    : 更新用户信息
                    事件作用    : 更新用户信
                    建议触发点  : 玩家信息更新时
                    调用前提    : 需要先调用setServerId、setGameUserId、setNickname接口
                    必填字段    : nickname      String    昵称
                    可选字段    : roleType      String    角色类型
                                 vip           int       等级
                                 status        int       状态	 状态标识，-1：锁定，1：未锁定
                 */

            // 调用前，需要先设置setServerId、setGameUserId、setNickname
            WACoreProxy.setServerId("serverid001");     //设置用户选择的服务器id
            WACoreProxy.setGameUserId("gameUserId001");// 设置游戏的用户id
            WACoreProxy.setNickname("总裁女友");        // 设置昵称

            WAEvent event = new WAEvent.Builder()
                    .setDefaultEventName(WAEventType.USER_INFO_UPDATE)
                    //必选字段
                    .addDefaultEventValue(WAEventParameterName.NICKNAME, "总裁女友")
                    //可选字段
                    //.addDefaultEventValue(WAEventParameterName.ROLE_TYPE, 1)
                    //.addDefaultEventValue(WAEventParameterName.STATUS, 1)
                    //.addDefaultEventValue(WAEventParameterName.VIP, 1)
                    .build();
            WATrackProxy.trackEvent(this, event);
        } else if (id == R.id.btn_lv_x) {/* ghw_self_lv_x  关键等级
                    事件描述    : 关键等级
                    事件作用    : 统计
                    建议触发点  : 到达关键等级时
                    调用前提    : 无
                    必填字段    : 无
                */
            int level = 10;
            WAEvent event = new WAEvent.Builder()
                    .setDefaultEventName("lv_" + level)
                    .build();
            WATrackProxy.trackEvent(this, event);
        } else if (id == R.id.btn_tutorial_completed) {/* ghw_self_tutorial_completed  完成新手任务
                    事件描述    : 完成新手任务
                    事件作用    : 统计
                    建议触发点  : 新手完成新手任务时调用
                    调用前提    : 无
                    必填字段    : 无
                */
            WAEvent event = new WAEvent.Builder()
                    .setDefaultEventName("tutorial_completed")
                    .build();
            WATrackProxy.trackEvent(this, event);
        }
    }
}
