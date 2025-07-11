package com.wa.sdk.demo.rare.tracking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentTabHost;

import com.wa.sdk.WAConstants;
import com.wa.sdk.common.utils.StringUtil;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.rare.tracking.fragment.CustomEventFragment;
import com.wa.sdk.demo.rare.tracking.fragment.DefaultEventFragment;
import com.wa.sdk.demo.utils.WADemoConfig;
import com.wa.sdk.demo.widget.TabView;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.track.model.WAEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TrackingSendActivity extends BaseActivity {

    public static final int TYPE_DEFAULT = 1;

    public static final int TYPE_APPSFLYERS = 2;

    public static final int TYPE_FACEBOOK = 3;

    public static final int TYPE_HUAWEIHMS = 4;

    public static final int TYPE_FIREBASE = 5;


    private FragmentTabHost mFtTabHost;

    private String mDefaultEventName;
    private float mDefaultValue = 0.0f;
    private HashMap<String, Object> mDefaultEventValues = new HashMap<String, Object>();

    private Map<String, Boolean> mChannelSwitchMap = new HashMap<String, Boolean>();
    private Map<String, String> mEventNameMap = new HashMap<String, String>();
    private Map<String, Float> mValueMap = new HashMap<String, Float>();
    private Map<String, Map<String, Object>> mEventValueMap = new HashMap<String, Map<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_send);

        initData();

        initView();

    }

    private void initView() {
        initTitlebar();

        mContainerId = R.id.fl_tracking_channel_container;

        mFtTabHost = (FragmentTabHost) findViewById(R.id.tabs_tracking_channel);

        mFtTabHost.setup(this, mFragmentManager, mContainerId);

        addTabs();


    }

    private void initData() {
        Intent intent = getIntent();
        if (intent.hasExtra(WADemoConfig.EXTRA_EVENT_NAME)) {
            mDefaultEventName = intent.getStringExtra(WADemoConfig.EXTRA_EVENT_NAME);
        } else {
            finish();
            showShortToast("缺少事件名称参数");
        }
        if (intent.hasExtra(WADemoConfig.EXTRA_COUNT_VALUE)) {
            mDefaultValue = intent.getFloatExtra(WADemoConfig.EXTRA_EVENT_VALUES, 0.0f);
        }
        if (intent.hasExtra(WADemoConfig.EXTRA_EVENT_VALUES)) {
            HashMap<String, Object> eventValues = (HashMap<String, Object>) intent.getSerializableExtra(WADemoConfig.EXTRA_EVENT_VALUES);
            if (null != eventValues && !eventValues.isEmpty()) {
                mDefaultEventValues.putAll(eventValues);
            }
        }

    }

    private void initTitlebar() {
        TitleBar tb = (TitleBar) findViewById(R.id.tb_tracking_send);
        tb.setLeftButton(android.R.drawable.ic_menu_revert, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tb.setTitleText(R.string.send_tracking);
        tb.setTitleTextColor(R.color.color_white);
        tb.setRightButtonTextColorResource(R.color.color_white);
        tb.setRightButtonWithText(R.string.send, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTracking();
                showShortToast(R.string.event_send);
            }
        });
    }

    private void addTabs() {
        TabView waTracking = new TabView(this);
        waTracking.setTitle("WATrack");
        TabView appsflyers = new TabView(this);
        appsflyers.setTitle("AppsFlyer");
        TabView facebook = new TabView(this);
        facebook.setTitle("Facebook");
        TabView huawei = new TabView(this);
        huawei.setTitle("HuaweiHMS");
        TabView firebase = new TabView(this);
        firebase.setTitle("Firebase");


        Bundle defaultEvent = new Bundle();
        defaultEvent.putString(WADemoConfig.EXTRA_EVENT_NAME, mDefaultEventName);
        defaultEvent.putFloat(WADemoConfig.EXTRA_COUNT_VALUE, mDefaultValue);
        defaultEvent.putSerializable(WADemoConfig.EXTRA_EVENT_VALUES, mDefaultEventValues);
        mFtTabHost.addTab(mFtTabHost.newTabSpec("Default").setIndicator(waTracking), DefaultEventFragment.class, defaultEvent);

        Bundle afArgs = new Bundle();
        afArgs.putInt(WADemoConfig.EXTRA_DATA, TrackingSendActivity.TYPE_APPSFLYERS);
        mFtTabHost.addTab(mFtTabHost.newTabSpec("AppsFlyer").setIndicator(appsflyers), CustomEventFragment.class, afArgs);

        Bundle fbArgs = new Bundle();
        fbArgs.putInt(WADemoConfig.EXTRA_DATA, TrackingSendActivity.TYPE_FACEBOOK);
        mFtTabHost.addTab(mFtTabHost.newTabSpec("Facebook").setIndicator(facebook), CustomEventFragment.class, fbArgs);


        Bundle hwArgs = new Bundle();
        hwArgs.putInt(WADemoConfig.EXTRA_DATA, TrackingSendActivity.TYPE_HUAWEIHMS);
        mFtTabHost.addTab(mFtTabHost.newTabSpec("HuaweiHMS").setIndicator(huawei), CustomEventFragment.class, hwArgs);

        Bundle firebaseArgs = new Bundle();
        firebaseArgs.putInt(WADemoConfig.EXTRA_DATA, TrackingSendActivity.TYPE_FIREBASE);
        mFtTabHost.addTab(mFtTabHost.newTabSpec("Firebase").setIndicator(firebase), CustomEventFragment.class, firebaseArgs);


        mFtTabHost.setCurrentTab(0);
    }

    private void sendTracking() {
        WAEvent.Builder eventBuilder = new WAEvent.Builder()
                .setDefaultEventName(mDefaultEventName)
                .setDefaultValue(mDefaultValue)
                .setDefaultEventValues(mDefaultEventValues);

        Set<String> channelSwitchKeys = mChannelSwitchMap.keySet();
        for (String channel : channelSwitchKeys) {
            if (mChannelSwitchMap.get(channel)) {
                eventBuilder.disableChannel(channel);
            }
        }

        Set<String> channelEventKeys = mEventNameMap.keySet();
        for (String channel : channelEventKeys) {
            eventBuilder.setChannelEventName(channel, mEventNameMap.get(channel));
        }

        Set<String> channelValueKeys = mValueMap.keySet();
        for (String channel : channelValueKeys) {
            eventBuilder.setChannelValue(channel, mValueMap.get(channel));
        }

        Set<String> channelValuesKeys = mEventValueMap.keySet();
        for (String channel : channelValuesKeys) {
            eventBuilder.setChannelEventValues(channel, mEventValueMap.get(channel));
        }

        eventBuilder.build().track(TrackingSendActivity.this);

    }

    public void onEventNameChanged(int type, String oldName, String newName) {
        switch (type) {
            case TYPE_DEFAULT:
                mDefaultEventName = newName;
                break;
            case TYPE_APPSFLYERS:
                if (StringUtil.isEmpty(newName)) {
                    mEventNameMap.remove(WAConstants.CHANNEL_APPSFLYER);
                } else {
                    mEventNameMap.put(WAConstants.CHANNEL_APPSFLYER, newName);
                }
                break;
            case TYPE_FACEBOOK:
                if (StringUtil.isEmpty(newName)) {
                    mEventNameMap.remove(WAConstants.CHANNEL_FACEBOOK);
                } else {
                    mEventNameMap.put(WAConstants.CHANNEL_FACEBOOK, newName);
                }
                break;

            case TYPE_HUAWEIHMS:
                if (StringUtil.isEmpty(newName)) {
                    mEventNameMap.remove(WAConstants.CHANNEL_HUAWEI_HMS);
                } else {
                    mEventNameMap.put(WAConstants.CHANNEL_HUAWEI_HMS, newName);
                }
                break;
            case TYPE_FIREBASE:
                if (StringUtil.isEmpty(newName)) {
                    mEventNameMap.remove(WAConstants.CHANNEL_FIREBASE);
                } else {
                    mEventNameMap.put(WAConstants.CHANNEL_FIREBASE, newName);
                }
                break;
            default:
                break;
        }
    }

    public void onCountValueChanged(int type, float oldValue, float newValue) {
        switch (type) {
            case TYPE_DEFAULT:
                mDefaultValue = newValue;
                break;
            case TYPE_APPSFLYERS:
                mValueMap.put(WAConstants.CHANNEL_APPSFLYER, newValue);
                break;
            case TYPE_FACEBOOK:
                mValueMap.put(WAConstants.CHANNEL_FACEBOOK, newValue);
                break;
            case TYPE_HUAWEIHMS:
                mValueMap.put(WAConstants.CHANNEL_HUAWEI_HMS, newValue);
                break;
            case TYPE_FIREBASE:
                mValueMap.put(WAConstants.CHANNEL_FIREBASE, newValue);
                break;
            default:
                break;
        }
    }

    public void onParameterChanged(int type, String key, boolean isKey, Object oldValue, Object newValue) {
        switch (type) {
            case TYPE_DEFAULT:
                if (isKey) {
                    if (StringUtil.isEmpty(key)) {
                        mDefaultEventValues.put(String.valueOf(newValue), "");
                    } else {
                        Object value = mDefaultEventValues.get(key);
                        mDefaultEventValues.remove(key);
                        mDefaultEventValues.put(String.valueOf(newValue), value);
                    }
                } else {
                    if (null == newValue) { // newValue 为null，删除
                        mDefaultEventValues.remove(key);
                    } else {
                        mDefaultEventValues.put(key, newValue);
                    }
                }
                break;
            case TYPE_APPSFLYERS:
                Map<String, Object> afValues = mEventValueMap.get(WAConstants.CHANNEL_APPSFLYER);
                if (null == afValues) {
                    afValues = new HashMap<>();
                }
                if (isKey) {
                    if (null == oldValue || StringUtil.isEmpty(String.valueOf(oldValue))) {
                        afValues.put(String.valueOf(newValue), "");
                    } else if (afValues.containsKey(key)) {
                        Object value = afValues.get(key);
                        afValues.remove(key);
                        afValues.put(String.valueOf(newValue), value);
                    } else {
                        afValues.put(String.valueOf(newValue), "");
                    }
                } else {
                    if (null == newValue) { // newValue 为null，删除
                        if (afValues.containsKey(key)) {
                            afValues.remove(key);
                        }
                    } else {
                        afValues.put(key, newValue);
                    }
                }
                if (afValues.isEmpty()) {
                    mEventValueMap.remove(WAConstants.CHANNEL_APPSFLYER);
                } else {
                    mEventValueMap.put(WAConstants.CHANNEL_APPSFLYER, afValues);
                }
                break;
            case TYPE_FACEBOOK:
                Map<String, Object> fbValues = mEventValueMap.get(WAConstants.CHANNEL_FACEBOOK);
                if (null == fbValues) {
                    fbValues = new HashMap<>();
                }
                if (isKey) {
                    if (null == oldValue || StringUtil.isEmpty(String.valueOf(oldValue))) {
                        fbValues.put(String.valueOf(newValue), "");
                    } else if (fbValues.containsKey(key)) {
                        Object value = fbValues.get(key);
                        fbValues.remove(key);
                        fbValues.put(String.valueOf(newValue), value);
                    } else {
                        fbValues.put(String.valueOf(newValue), "");
                    }
                } else {
                    if (null == newValue) { // newValue 为null，删除
                        if (fbValues.containsKey(key)) {
                            fbValues.remove(key);
                        }
                    } else {
                        fbValues.put(key, newValue);
                    }
                }
                if (fbValues.isEmpty()) {
                    mEventValueMap.remove(WAConstants.CHANNEL_FACEBOOK);
                } else {
                    mEventValueMap.put(WAConstants.CHANNEL_FACEBOOK, fbValues);
                }
                break;

            case TYPE_HUAWEIHMS:
                Map<String, Object> hwValues = mEventValueMap.get(WAConstants.CHANNEL_HUAWEI_HMS);
                if (null == hwValues) {
                    hwValues = new HashMap<>();
                }
                if (isKey) {
                    if (null == oldValue || StringUtil.isEmpty(String.valueOf(oldValue))) {
                        hwValues.put(String.valueOf(newValue), "");
                    } else if (hwValues.containsKey(key)) {
                        Object value = hwValues.get(key);
                        hwValues.remove(key);
                        hwValues.put(String.valueOf(newValue), value);
                    } else {
                        hwValues.put(String.valueOf(newValue), "");
                    }
                } else {
                    if (null == newValue) { // newValue 为null，删除
                        if (hwValues.containsKey(key)) {
                            hwValues.remove(key);
                        }
                    } else {
                        hwValues.put(key, newValue);
                    }
                }
                if (hwValues.isEmpty()) {
                    mEventValueMap.remove(WAConstants.CHANNEL_HUAWEI_HMS);
                } else {
                    mEventValueMap.put(WAConstants.CHANNEL_HUAWEI_HMS, hwValues);
                }
                break;
            case TYPE_FIREBASE:
                Map<String, Object> firebaseValues = mEventValueMap.get(WAConstants.CHANNEL_FIREBASE);
                if (null == firebaseValues) {
                    firebaseValues = new HashMap<>();
                }
                if (isKey) {
                    if (null == oldValue || StringUtil.isEmpty(String.valueOf(oldValue))) {
                        firebaseValues.put(String.valueOf(newValue), "");
                    } else if (firebaseValues.containsKey(key)) {
                        Object value = firebaseValues.get(key);
                        firebaseValues.remove(key);
                        firebaseValues.put(String.valueOf(newValue), value);
                    } else {
                        firebaseValues.put(String.valueOf(newValue), "");
                    }
                } else {
                    if (null == newValue) { // newValue 为null，删除
                        if (firebaseValues.containsKey(key)) {
                            firebaseValues.remove(key);
                        }
                    } else {
                        firebaseValues.put(key, newValue);
                    }
                }
                if (firebaseValues.isEmpty()) {
                    mEventValueMap.remove(WAConstants.CHANNEL_FIREBASE);
                } else {
                    mEventValueMap.put(WAConstants.CHANNEL_FIREBASE, firebaseValues);
                }
                break;

            default:
                break;
        }
    }

}
