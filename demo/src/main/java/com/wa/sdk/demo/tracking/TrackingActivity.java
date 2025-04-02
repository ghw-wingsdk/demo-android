package com.wa.sdk.demo.tracking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wa.sdk.WAConstants;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.WADemoConfig;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.track.WAEventParameterName;
import com.wa.sdk.track.WAEventType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Deprecated
public class TrackingActivity extends BaseActivity {

    private EventAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tracking);

        ListView lv = (ListView) findViewById(R.id.lv_events);
        mAdapter = new EventAdapter(this);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(onItemClickListener);

        TitleBar tb = (TitleBar) findViewById(R.id.tb_tracking);
        tb.setLeftButton(android.R.drawable.ic_menu_revert, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tb.setTitleText(R.string.tracking);
        tb.setTitleTextColor(R.color.color_white);
        tb.setRightButtonTextColorResource(R.color.color_white);
        tb.setRightButtonWithText("模拟", v -> startActivity(new Intent(this, TrackingSimulateActivity.class)));
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (null == mAdapter) {
                return;
            }
            Intent intent = new Intent(TrackingActivity.this, TrackingSendActivity.class);
            String eventName = mAdapter.getItem(position);
//            if(WAEventType.COMPLETE_REGISTRATION.equals(eventName)) {
//                completeRegistration(intent);
//            } else
            if (WAEventType.LOGIN.equals(eventName)) {
                login(intent);
            } else if (WAEventType.INITIATED_PAYMENT.equals(eventName)) {
                initPayment(intent);
            } else if (WAEventType.COMPLETE_PAYMENT.equals(eventName)) {
                payment(intent);
            } else if (WAEventType.INITIATED_PURCHASE.equals(eventName)) {
                initPurchase(intent);
            } else if (WAEventType.COMPLETE_PURCHASE.equals(eventName)) {
                purchase(intent);
            } else if (WAEventType.LEVEL_ACHIEVED.equals(eventName)) {
                levelAchieved(intent);
//            } else if(WAEventType.ADD_TO_CART.equals(eventName)) {
//                addToCart(intent);
//            } else if(WAEventType.ADD_TO_WISH_LIST.equals(eventName)) {
//                addToWishlist(intent);
//            } else if(WAEventType.SEARCH.equals(eventName)) {
//                search(intent);
//            } else if(WAEventType.SPENT_CREDITS.equals(eventName)) {
//                spentCredits(intent);
//            } else if(WAEventType.ACHIEVEMENT_UNLOCKED.equals(eventName)) {
//                achievementUnlocked(intent);
//            } else if(WAEventType.CONTENT_VIEW.equals(eventName)) {
//                contentView(intent);
//            } else if(WAEventType.SHARE.equals(eventName)) {
//                share(intent);
//            } else if(WAEventType.INVITE.equals(eventName)) {
//                invite(intent);
//            } else if(WAEventType.RE_ENGAGE.equals(eventName)) {
//                reEngage(intent);
//            } else if(WAEventType.UPDATE.equals(eventName)) {
//                update(intent);
//            } else if(WAEventType.OPENED_FROM_PUSH_NOTIFICATION.equals(eventName)) {
//                openedFromPushNotification(intent);
            } else if (WAEventType.USER_CREATED.equals(eventName)) {
                createUser(intent);
            } else if (WAEventType.USER_INFO_UPDATE.equals(eventName)) {
                updateUserInfo(intent);
            } else if (WAEventType.TASK_UPDATE.equals(eventName)) {
                taskUpdate(intent);
            } else if (WAEventType.GOLD_UPDATE.equals(eventName)) {
                goldUpdate(intent);
            } else if (WAEventType.IMPORT_USER.equals(eventName)) {
                userImport(intent);
            } else if (WAEventType.CUSTOM_EVENT_PREFIX.equals(eventName)) {
                selfEvent(intent);
            }

            startActivity(intent);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


//    private void completeRegistration(Intent intent) {
//        HashMap<String, Object> eventValues = new HashMap<>();
//        eventValues.put(WAEventParameterName.ACCOUNT_TYPE, WAConstants.CHANNEL_FACEBOOK);
//        eventValues.put(WAEventParameterName.GENDER, WAConstants.GENDER_FEMALE);
//        eventValues.put(WAEventParameterName.AGE, 20);
//        eventValues.put(WAEventParameterName.SUCCESS, true);
//        intent.putExtra(WADemoConfig.EXTRA_EVENT_NAME, WAEventType.COMPLETE_REGISTRATION);
//        intent.putExtra(WADemoConfig.EXTRA_EVENT_VALUES, eventValues);
//    }

    private void login(Intent intent) {
        HashMap<String, Object> eventValues = new HashMap<>();
//        eventValues.put(WAEventParameterName.LEVEL, 140);
        intent.putExtra(WADemoConfig.EXTRA_EVENT_NAME, WAEventType.LOGIN);
        intent.putExtra(WADemoConfig.EXTRA_EVENT_VALUES, eventValues);
    }

    private void initPayment(Intent intent) {
        intent.putExtra(WADemoConfig.EXTRA_EVENT_NAME, WAEventType.INITIATED_PAYMENT);
    }

    private void payment(Intent intent) {
        HashMap<String, Object> eventValues = new HashMap<>();
        eventValues.put(WAEventParameterName.TRANSACTION_ID, "13241893274981237");
        eventValues.put(WAEventParameterName.PAYMENT_TYPE, WAConstants.CHANNEL_GOOGLE);
        eventValues.put(WAEventParameterName.CURRENCY_TYPE, WAConstants.CURRENCY_USD);
        eventValues.put(WAEventParameterName.CURRENCY_AMOUNT, 50.234f);
        eventValues.put(WAEventParameterName.VERTUAL_COIN_AMOUNT, 20000);
        eventValues.put(WAEventParameterName.VERTUAL_COIN_CURRENCY, "gold");
        eventValues.put(WAEventParameterName.IAP_ID, "1111111");
        eventValues.put(WAEventParameterName.IAP_NAME, "GGGGGG");
        eventValues.put(WAEventParameterName.IAP_AMOUNT, 20);
        eventValues.put(WAEventParameterName.LEVEL, 120);
        intent.putExtra(WADemoConfig.EXTRA_EVENT_NAME, WAEventType.COMPLETE_PAYMENT);
        intent.putExtra(WADemoConfig.EXTRA_EVENT_VALUES, eventValues);

    }

    private void initPurchase(Intent intent) {
        intent.putExtra(WADemoConfig.EXTRA_EVENT_NAME, WAEventType.INITIATED_PURCHASE);
    }

    private void purchase(Intent intent) {
        HashMap<String, Object> eventValues = new HashMap<>();
        eventValues.put(WAEventParameterName.ITEM_NAME, "GGGGG");
        eventValues.put(WAEventParameterName.ITEM_AMOUNT, 20);
        eventValues.put(WAEventParameterName.PRICE, 50);
        eventValues.put(WAEventParameterName.LEVEL, 120);
        intent.putExtra(WADemoConfig.EXTRA_EVENT_NAME, WAEventType.COMPLETE_PURCHASE);
        intent.putExtra(WADemoConfig.EXTRA_EVENT_VALUES, eventValues);

    }

    public void levelAchieved(Intent intent) {
        HashMap<String, Object> eventValues = new HashMap<>();
        eventValues.put(WAEventParameterName.LEVEL, "120");
        eventValues.put(WAEventParameterName.SCORE, 3241234);
        eventValues.put(WAEventParameterName.FIGHTING, 1230020);

        intent.putExtra(WADemoConfig.EXTRA_EVENT_NAME, WAEventType.LEVEL_ACHIEVED);
        intent.putExtra(WADemoConfig.EXTRA_EVENT_VALUES, eventValues);

    }

//    public void addToCart(Intent intent) {
//        HashMap<String, Object> eventValues = new HashMap<>();
//        eventValues.put(WAEventParameterName.PRICE, 120);
//        eventValues.put(WAEventParameterName.CONTENT_TYPE, "GGGGG");
//        eventValues.put(WAEventParameterName.CONTENT_ID, "1234");
//        eventValues.put(WAEventParameterName.CURRENCY_TYPE, WAConstants.CURRENCY_USD);
//        eventValues.put(WAEventParameterName.QUANTITY, 5);
//
//        intent.putExtra(WADemoConfig.EXTRA_EVENT_NAME, WAEventType.ADD_TO_CART);
//        intent.putExtra(WADemoConfig.EXTRA_EVENT_VALUES, eventValues);
//
//    }
//
//    public void addToWishlist(Intent intent) {
//        HashMap<String, Object> eventValues = new HashMap<>();
//        eventValues.put(WAEventParameterName.PRICE, 120);
//        eventValues.put(WAEventParameterName.CONTENT_TYPE, "GGGGG");
//        eventValues.put(WAEventParameterName.CONTENT_ID, "1234");
//        eventValues.put(WAEventParameterName.CURRENCY_TYPE, WAConstants.CURRENCY_USD);
//        eventValues.put(WAEventParameterName.QUANTITY, 5);
//
//        intent.putExtra(WADemoConfig.EXTRA_EVENT_NAME, WAEventType.ADD_TO_WISH_LIST);
//        intent.putExtra(WADemoConfig.EXTRA_EVENT_VALUES, eventValues);
//
//    }
//
//    public void search(Intent intent) {
//        HashMap<String, Object> eventValues = new HashMap<>();
//        eventValues.put(WAEventParameterName.CONTENT_TYPE, "AAAAAAAA");
//        eventValues.put(WAEventParameterName.SEARCH_STRING, "Google");
//        eventValues.put(WAEventParameterName.SUCCESS, true);
//
//        intent.putExtra(WADemoConfig.EXTRA_EVENT_NAME, WAEventType.SEARCH);
//        intent.putExtra(WADemoConfig.EXTRA_EVENT_VALUES, eventValues);
//
//    }
//
//    public void spentCredits(Intent intent) {
//        HashMap<String, Object> eventValues = new HashMap<>();
//        eventValues.put(WAEventParameterName.PRICE, 50);
//        eventValues.put(WAEventParameterName.CONTENT_TYPE, "Google");
//        eventValues.put(WAEventParameterName.CONTENT_ID, "12345");
//
//        intent.putExtra(WADemoConfig.EXTRA_EVENT_NAME, WAEventType.SPENT_CREDITS);
//        intent.putExtra(WADemoConfig.EXTRA_EVENT_VALUES, eventValues);
//
//    }
//
//    public void achievementUnlocked(Intent intent) {
//        HashMap<String, Object> eventValues = new HashMap<>();
//        eventValues.put(WAEventParameterName.DESCRIPTION, "Unlocked AAAAA");
//
//        intent.putExtra(WADemoConfig.EXTRA_EVENT_NAME, WAEventType.ACHIEVEMENT_UNLOCKED);
//        intent.putExtra(WADemoConfig.EXTRA_EVENT_VALUES, eventValues);
//
//    }
//
//    public void contentView(Intent intent) {
//        HashMap<String, Object> eventValues = new HashMap<>();
//        eventValues.put(WAEventParameterName.PRICE, 20);
//        eventValues.put(WAEventParameterName.CONTENT_TYPE, "GGGGG");
//        eventValues.put(WAEventParameterName.CONTENT_ID, "12345");
//        eventValues.put(WAEventParameterName.CURRENCY_TYPE, WAConstants.CURRENCY_USD);
//
//        intent.putExtra(WADemoConfig.EXTRA_EVENT_NAME, WAEventType.CONTENT_VIEW);
//        intent.putExtra(WADemoConfig.EXTRA_EVENT_VALUES, eventValues);
//    }
//
//    public void share(Intent intent) {
//        HashMap<String, Object> eventValues = new HashMap<>();
//        eventValues.put(WAEventParameterName.DESCRIPTION, "Shared BBBBB");
//
//        intent.putExtra(WADemoConfig.EXTRA_EVENT_NAME, WAEventType.SHARE);
//        intent.putExtra(WADemoConfig.EXTRA_EVENT_VALUES, eventValues);
//
//    }
//
//    public void invite(Intent intent) {
//        HashMap<String, Object> eventValues = new HashMap<>();
//        eventValues.put(WAEventParameterName.DESCRIPTION, "Invite John");
//
//        intent.putExtra(WADemoConfig.EXTRA_EVENT_NAME, WAEventType.INVITE);
//        intent.putExtra(WADemoConfig.EXTRA_EVENT_VALUES, eventValues);
//    }
//
//    public void reEngage(Intent intent) {
//        HashMap<String, Object> eventValues = new HashMap<>();
//        eventValues.put(WAEventParameterName.DESCRIPTION, "Re_engage to game");
//
//        intent.putExtra(WADemoConfig.EXTRA_EVENT_NAME, WAEventType.RE_ENGAGE);
//        intent.putExtra(WADemoConfig.EXTRA_EVENT_VALUES, eventValues);
//    }
//
//    public void update(Intent intent) {
//        HashMap<String, Object> eventValues = new HashMap<>();
//        eventValues.put(WAEventParameterName.CONTENT_ID, "v1.2");
//
//        intent.putExtra(WADemoConfig.EXTRA_EVENT_NAME, WAEventType.UPDATE);
//        intent.putExtra(WADemoConfig.EXTRA_EVENT_VALUES, eventValues);
//    }
//
//    public void openedFromPushNotification(Intent intent) {
//        HashMap<String, Object> eventValues = new HashMap<>();
//
//        intent.putExtra(WADemoConfig.EXTRA_EVENT_NAME, WAEventType.OPENED_FROM_PUSH_NOTIFICATION);
//        intent.putExtra(WADemoConfig.EXTRA_EVENT_VALUES, eventValues);
//    }

    public void createUser(Intent intent) {
        HashMap<String, Object> eventValues = new HashMap<>();
        eventValues.put(WAEventParameterName.ROLE_TYPE, 1);
        eventValues.put(WAEventParameterName.GENDER, WAConstants.GENDER_FEMALE);
        eventValues.put(WAEventParameterName.NICKNAME, "霸气侧漏");
        eventValues.put(WAEventParameterName.REGISTER_TIME, System.currentTimeMillis());
        eventValues.put(WAEventParameterName.VIP, 8);
        eventValues.put(WAEventParameterName.BINDED_GAME_GOLD, 100000);
        eventValues.put(WAEventParameterName.GAME_GOLD, 10000);
        eventValues.put(WAEventParameterName.LEVEL, 100);
        eventValues.put(WAEventParameterName.FIGHTING, 1230020);
        eventValues.put(WAEventParameterName.STATUS, 1);

        intent.putExtra(WADemoConfig.EXTRA_EVENT_NAME, WAEventType.USER_CREATED);
        intent.putExtra(WADemoConfig.EXTRA_EVENT_VALUES, eventValues);
    }

    public void updateUserInfo(Intent intent) {
        HashMap<String, Object> eventValues = new HashMap<>();
        eventValues.put(WAEventParameterName.ROLE_TYPE, 1);
        eventValues.put(WAEventParameterName.NICKNAME, "霸气侧漏");
        eventValues.put(WAEventParameterName.VIP, 8);

        intent.putExtra(WADemoConfig.EXTRA_EVENT_NAME, WAEventType.USER_INFO_UPDATE);
        intent.putExtra(WADemoConfig.EXTRA_EVENT_VALUES, eventValues);
    }

    public void taskUpdate(Intent intent) {
        HashMap<String, Object> eventValues = new HashMap<>();
        eventValues.put(WAEventParameterName.TASK_ID, "123");
        eventValues.put(WAEventParameterName.TASK_NAME, "刺杀希特勒");
        eventValues.put(WAEventParameterName.TASK_TYPE, "等级任务");
        eventValues.put(WAEventParameterName.TASK_STATUS, 2);

        intent.putExtra(WADemoConfig.EXTRA_EVENT_NAME, WAEventType.TASK_UPDATE);
        intent.putExtra(WADemoConfig.EXTRA_EVENT_VALUES, eventValues);
    }

    public void goldUpdate(Intent intent) {
        HashMap<String, Object> eventValues = new HashMap<>();
        eventValues.put(WAEventParameterName.GOLD_TYPE, 1);
        eventValues.put(WAEventParameterName.APPROACH, "充值");
        eventValues.put(WAEventParameterName.AMOUNT, 100000);
        eventValues.put(WAEventParameterName.CURRENT_AMOUNT, 200000);

        intent.putExtra(WADemoConfig.EXTRA_EVENT_NAME, WAEventType.GOLD_UPDATE);
        intent.putExtra(WADemoConfig.EXTRA_EVENT_VALUES, eventValues);
    }

    public void userImport(Intent intent) {
        HashMap<String, Object> eventValues = new HashMap<>();
        eventValues.put(WAEventParameterName.IS_FIRST_ENTER, 0);

        intent.putExtra(WADemoConfig.EXTRA_EVENT_NAME, WAEventType.IMPORT_USER);
        intent.putExtra(WADemoConfig.EXTRA_EVENT_VALUES, eventValues);
    }


    public void selfEvent(Intent intent) {
        HashMap<String, Object> eventValues = new HashMap<>();
        eventValues.put(WAEventParameterName.LEVEL, 140);
        eventValues.put("to_level", 141);
        eventValues.put("fight_force", 1232320);
        eventValues.put("to_fight_force", 1220020);
        eventValues.put(WAEventParameterName.SUCCESS, true);

        intent.putExtra(WADemoConfig.EXTRA_EVENT_NAME, WAEventType.CUSTOM_EVENT_PREFIX + "fight");
        intent.putExtra(WADemoConfig.EXTRA_EVENT_VALUES, eventValues);

    }


    private class EventAdapter extends BaseAdapter {

        private Context mmContext;
        private List<String> mmEventNames = new ArrayList<>();

        public EventAdapter(Context context) {
            this.mmContext = context;
            initData();
        }

        @Override
        public int getCount() {
            return mmEventNames.size();
        }

        @Override
        public String getItem(int position) {
            return mmEventNames.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView tv = null;
            if (null == convertView) {
                tv = new TextView(mmContext);
                tv.setTextColor(getResourceColor(R.color.color_black_alpha_ee));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                tv.setPadding(15, 25, 15, 25);
                convertView = tv;
            } else {
                tv = (TextView) convertView;
            }

            tv.setText(getItem(position));
            return convertView;
        }

        private void initData() {
//            mmEventNames.add(WAEventType.INSTALL);
//            mmEventNames.add(WAEventType.STARTUP);
//            mmEventNames.add(WAEventType.COMPLETE_REGISTRATION);
            mmEventNames.add(WAEventType.LOGIN);
            mmEventNames.add(WAEventType.INITIATED_PAYMENT);
            mmEventNames.add(WAEventType.COMPLETE_PAYMENT);
            mmEventNames.add(WAEventType.INITIATED_PURCHASE);
            mmEventNames.add(WAEventType.COMPLETE_PURCHASE);
//            mmEventNames.add(WAEventType.ONLINE_TIME);
            mmEventNames.add(WAEventType.LEVEL_ACHIEVED);
//            mmEventNames.add(WAEventType.ADD_TO_CART);
//            mmEventNames.add(WAEventType.ADD_TO_WISH_LIST);
//            mmEventNames.add(WAEventType.SEARCH);
//            mmEventNames.add(WAEventType.SPENT_CREDITS);
//            mmEventNames.add(WAEventType.ACHIEVEMENT_UNLOCKED);
//            mmEventNames.add(WAEventType.CONTENT_VIEW);
//            mmEventNames.add(WAEventType.SHARE);
//            mmEventNames.add(WAEventType.INVITE);
//            mmEventNames.add(WAEventType.RE_ENGAGE);
//            mmEventNames.add(WAEventType.UPDATE);
//            mmEventNames.add(WAEventType.OPENED_FROM_PUSH_NOTIFICATION);
            mmEventNames.add(WAEventType.USER_CREATED);
            mmEventNames.add(WAEventType.USER_INFO_UPDATE);
            mmEventNames.add(WAEventType.TASK_UPDATE);
            mmEventNames.add(WAEventType.GOLD_UPDATE);
            mmEventNames.add(WAEventType.IMPORT_USER);
            mmEventNames.add(WAEventType.CUSTOM_EVENT_PREFIX);
        }
    }

}
