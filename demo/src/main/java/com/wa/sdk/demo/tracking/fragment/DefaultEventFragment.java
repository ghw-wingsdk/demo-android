package com.wa.sdk.demo.tracking.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.wa.sdk.common.utils.StringUtil;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.Util;
import com.wa.sdk.demo.WADemoConfig;
import com.wa.sdk.demo.base.BaseFragment;
import com.wa.sdk.demo.tracking.TrackingSendActivity;
import com.wa.sdk.demo.widget.EventItemView;
import com.wa.sdk.track.WAEventType;

import java.util.HashMap;
import java.util.Set;

/**
 * 
 */
public class DefaultEventFragment extends BaseFragment {

    private String mDefaultEventName;
    private float mDefaultValue = 0.0f;
    private HashMap<String, Object> mDefaultEventValues = new HashMap<String, Object>();

    private LinearLayout mLlParamsContent;
    private Button mBtnAddParameter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if(null != args) {
            if(args.containsKey(WADemoConfig.EXTRA_EVENT_NAME)) {
                mDefaultEventName = args.getString(WADemoConfig.EXTRA_EVENT_NAME);
            }
            if(args.containsKey(WADemoConfig.EXTRA_COUNT_VALUE)) {
                mDefaultValue = args.getFloat(WADemoConfig.EXTRA_COUNT_VALUE, 0.0f);
            }
            if(args.containsKey(WADemoConfig.EXTRA_EVENT_VALUES)) {
                HashMap<String, Object> values = (HashMap<String, Object>) args.getSerializable(WADemoConfig.EXTRA_EVENT_VALUES);
                if(null == values || values.isEmpty()) {
                    return;
                }
                mDefaultEventValues.putAll(values);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_send_event, container, false);

        initView(contentView);

        Set<String> valuesKey = mDefaultEventValues.keySet();
        boolean isCustom = mDefaultEventName.startsWith(WAEventType.CUSTOM_EVENT_PREFIX);
        for (String valueKey :valuesKey) {
            addParameterItemView(valueKey, String.valueOf(mDefaultEventValues.get(valueKey)), isCustom, isCustom);
        }
        mBtnAddParameter.setEnabled(isCustom);

        return contentView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_add_parameter) {
            addParameterItemView(null, null, true, true);
        }
    }

    private void initView(View contentView) {
        EditText eventName = (EditText) contentView.findViewById(R.id.et_event_name);
        eventName.setEnabled(mDefaultEventName.startsWith(WAEventType.CUSTOM_EVENT_PREFIX));

        EditText countValue = (EditText) contentView.findViewById(R.id.et_event_count_value);

        eventName.setText(mDefaultEventName);
        countValue.setText(String.valueOf(mDefaultValue));

        eventName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newEventName = s.toString();
                onEventNameChanged(mDefaultEventName, newEventName);
                mDefaultEventName = newEventName;
            }
        });

        countValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newValue = s.toString();
                try {
                    float value = Float.valueOf(newValue);
                    onEventValueChanged(mDefaultValue, value);
                    mDefaultValue = value;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mLlParamsContent = (LinearLayout) contentView.findViewById(R.id.ll_event_parameter_content);
        mBtnAddParameter = (Button) contentView.findViewById(R.id.btn_add_parameter);
        mBtnAddParameter.setOnClickListener(this);
    }

    private EventItemView addParameterItemView(final String defaultKey, final String defaultValue,
                                               boolean keyEditable, boolean deletable) {
        EventItemView itemView = new EventItemView(getActivity());
        itemView.setType(TrackingSendActivity.TYPE_DEFAULT);
        itemView.setKeyEditable(keyEditable);
        if(!StringUtil.isEmpty(defaultKey)) {
            itemView.setKey(defaultKey);
            itemView.setValueInputType(Util.getInputType(defaultKey));
        }
        if(!StringUtil.isEmpty(defaultValue)) {
            itemView.setValue(defaultValue);
        }
        itemView.setOnDeleteListener(new EventItemView.OnDeleteListener() {
            @Override
            public void onDelete(EventItemView view) {
                mLlParamsContent.removeView(view);
                onParameterChanged(view.getKey(), false, view.getValue(), null);
            }
        });
        itemView.setDeletable(deletable);
        itemView.setOnDataChangedListener(new EventItemView.OnDataChangedListener() {
            @Override
            public void onDataChanged(int type, String key, boolean isKey, Object oldValue, Object newValue) {
                switch (type) {
                    case TrackingSendActivity.TYPE_DEFAULT:
                        onParameterChanged(key, isKey, oldValue, newValue);
                        break;
                    case TrackingSendActivity.TYPE_APPSFLYERS:
                    case TrackingSendActivity.TYPE_FACEBOOK:
                    case TrackingSendActivity.TYPE_HUAWEIHMS:
                    case TrackingSendActivity.TYPE_FIREBASE:
                    default:
                        break;
                }
            }
        });
        mLlParamsContent.addView(itemView);
        return itemView;
    }

    public void onEventNameChanged(String oldName, String newName) {
        Activity activity = getActivity();
        if(activity instanceof TrackingSendActivity) {
            ((TrackingSendActivity) activity).onEventNameChanged(TrackingSendActivity.TYPE_DEFAULT,
                    oldName, newName);
        }
    }

    public void onEventValueChanged(float oldValue, float newValue) {
        Activity activity = getActivity();
        if(activity instanceof TrackingSendActivity) {
            ((TrackingSendActivity) activity).onCountValueChanged(TrackingSendActivity.TYPE_DEFAULT,
                    oldValue, newValue);
        }
    }

    public void onParameterChanged(String key, boolean isKey, Object oldValue, Object newValue) {
        Activity activity = getActivity();
        if(activity instanceof TrackingSendActivity) {
            ((TrackingSendActivity) activity).onParameterChanged(TrackingSendActivity.TYPE_DEFAULT,
                    key, isKey, oldValue, newValue);
        }
    }

}
