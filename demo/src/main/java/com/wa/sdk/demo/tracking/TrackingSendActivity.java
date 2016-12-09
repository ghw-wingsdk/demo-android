package com.wa.sdk.demo.tracking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.View;

import com.wa.sdk.WAConstants;
import com.wa.sdk.common.utils.StringUtil;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.WADemoConfig;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.tracking.fragment.CustomEventFragment;
import com.wa.sdk.demo.tracking.fragment.DefaultEventFragment;
import com.wa.sdk.demo.widget.TabView;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.track.model.WAEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by yinglovezhuzhu@gmail.com on 2016/1/28.
 */
public class TrackingSendActivity extends BaseActivity {

    public static final int TYPE_DEFAULT = 1;

    public static final int TYPE_APPSFLYERS = 2;

    public static final int TYPE_FACEBOOK = 3;

    private FragmentTabHost mFtTabHost;

    private String mDefaultEventName;
    private float mDefaultValue = 0.0f;
    private HashMap<String, Object> mDefaultEventValues = new HashMap<String, Object>();

    private Map<String, Boolean> mChannelSwitchMap = new HashMap<String, Boolean>();
    private Map<String, String> mEventNameMap = new HashMap<String, String>();
    private Map<String, Float> mValueMap = new HashMap<String, Float>();
    private Map<String, Map<String, Object>> mEventValueMap = new HashMap<String, Map<String,Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_send);

        initData();

        initView();

//        WASharedPrefHelper sharedPrefHelper = WASharedPrefHelper.getInstance(this, WADemoConfig.SP_CONFIG_FILE_DEMO);
//        if (sharedPrefHelper.getBoolean("enable_logcat", true)) {
//            Logcat.enableLogcat(this);
//        }
//        if (sharedPrefHelper.getBoolean("enable_extend", true)) {
//            GhwSdkExtend.showEntryFlowIcon(this);
//        }
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
        if(intent.hasExtra(WADemoConfig.EXTRA_EVENT_NAME)) {
            mDefaultEventName = intent.getStringExtra(WADemoConfig.EXTRA_EVENT_NAME);
        } else {
            finish();
            showShortToast("缺少事件名称参数");
        }
        if(intent.hasExtra(WADemoConfig.EXTRA_COUNT_VALUE)) {
            mDefaultValue = intent.getFloatExtra(WADemoConfig.EXTRA_EVENT_VALUES, 0.0f);
        }
        if(intent.hasExtra(WADemoConfig.EXTRA_EVENT_VALUES)) {
            HashMap<String, Object> eventValues = (HashMap<String, Object>) intent.getSerializableExtra(WADemoConfig.EXTRA_EVENT_VALUES);
            if(null != eventValues && !eventValues.isEmpty()) {
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

        mFtTabHost.setCurrentTab(0);
    }

    private void sendTracking() {
        WAEvent.Builder eventBuilder = new WAEvent.Builder()
                .setDefaultEventName(mDefaultEventName)
                .setDefaultValue(mDefaultValue)
                .setDefaultEventValues(mDefaultEventValues);

        Set<String> channelSwitchKeys = mChannelSwitchMap.keySet();
        for(String channel : channelSwitchKeys) {
            if(mChannelSwitchMap.get(channel)) {
                eventBuilder.disableChannel(channel);
            }
        }

        Set<String> channelEventKeys = mEventNameMap.keySet();
        for(String channel : channelEventKeys) {
            eventBuilder.setChannelEventName(channel, mEventNameMap.get(channel));
        }

        Set<String> channelValueKeys = mValueMap.keySet();
        for(String channel : channelValueKeys) {
            eventBuilder.setChannelValue(channel, mValueMap.get(channel));
        }

        Set<String> channelValuesKeys = mEventValueMap.keySet();
        for(String channel : channelValuesKeys) {
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
                if(StringUtil.isEmpty(newName)) {
                    mEventNameMap.remove(WAConstants.CHANNEL_APPSFLYER);
                } else {
                    mEventNameMap.put(WAConstants.CHANNEL_APPSFLYER, newName);
                }
                break;
            case TYPE_FACEBOOK:
                if(StringUtil.isEmpty(newName)) {
                    mEventNameMap.remove(WAConstants.CHANNEL_FACEBOOK);
                } else {
                    mEventNameMap.put(WAConstants.CHANNEL_FACEBOOK, newName);
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
            default:
                break;
        }
    }

    public void onParameterChanged(int type, String key, boolean isKey, Object oldValue, Object newValue) {
        switch (type) {
            case TYPE_DEFAULT:
                if(isKey) {
                    if(StringUtil.isEmpty(key)) {
                        mDefaultEventValues.put(String.valueOf(newValue), "");
                    } else {
                        Object value = mDefaultEventValues.get(key);
                        mDefaultEventValues.remove(key);
                        mDefaultEventValues.put(String.valueOf(newValue), value);
                    }
                } else {
                    if(null == newValue) { // newValue 为null，删除
                        mDefaultEventValues.remove(key);
                    } else {
                        mDefaultEventValues.put(key, newValue);
                    }
                }
                break;
            case TYPE_APPSFLYERS:
                Map<String, Object> afValues = mEventValueMap.get(WAConstants.CHANNEL_APPSFLYER);
                if(null == afValues) {
                    afValues = new HashMap<>();
                }
                if(isKey) {
                    if(null == oldValue || StringUtil.isEmpty(String.valueOf(oldValue))) {
                        afValues.put(String.valueOf(newValue), "");
                    } else if(afValues.containsKey(key)){
                        Object value = afValues.get(key);
                        afValues.remove(key);
                        afValues.put(String.valueOf(newValue), value);
                    } else {
                        afValues.put(String.valueOf(newValue), "");
                    }
                } else {
                    if(null == newValue) { // newValue 为null，删除
                        if(afValues.containsKey(key)) {
                            afValues.remove(key);
                        }
                    } else {
                        afValues.put(key, newValue);
                    }
                }
                if(afValues.isEmpty()) {
                    mEventValueMap.remove(WAConstants.CHANNEL_APPSFLYER);
                } else {
                    mEventValueMap.put(WAConstants.CHANNEL_APPSFLYER, afValues);
                }
                break;
            case TYPE_FACEBOOK:
                Map<String, Object> fbValues = mEventValueMap.get(WAConstants.CHANNEL_FACEBOOK);
                if(null == fbValues) {
                    fbValues = new HashMap<>();
                }
                if(isKey) {
                    if(null == oldValue || StringUtil.isEmpty(String.valueOf(oldValue))) {
                        fbValues.put(String.valueOf(newValue), "");
                    } else if(fbValues.containsKey(key)){
                        Object value = fbValues.get(key);
                        fbValues.remove(key);
                        fbValues.put(String.valueOf(newValue), value);
                    } else {
                        fbValues.put(String.valueOf(newValue), "");
                    }
                } else {
                    if(null == newValue) { // newValue 为null，删除
                        if(fbValues.containsKey(key)) {
                            fbValues.remove(key);
                        }
                    } else {
                        fbValues.put(key, newValue);
                    }
                }
                if(fbValues.isEmpty()) {
                    mEventValueMap.remove(WAConstants.CHANNEL_FACEBOOK);
                } else {
                    mEventValueMap.put(WAConstants.CHANNEL_FACEBOOK, fbValues);
                }
                break;
            default:
                break;
        }
    }

}
