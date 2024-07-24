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

import java.util.HashMap;
import java.util.Set;

/**
 * Created by yinglovezhuzhu@gmail.com on 2016/1/29.
 */
public class CustomEventFragment extends BaseFragment {

    private int mType = -1;

    private LinearLayout mLlParamsContent;
    private Button mBtnAddParameter;

    private String mEventName;
    private HashMap<String, Object> mEventValues = new HashMap<>();
    private float mValue = 0.0f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if(null != args) {
            if(args.containsKey(WADemoConfig.EXTRA_DATA)) {
                mType = args.getInt(WADemoConfig.EXTRA_DATA, -1);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_send_event, container, false);

        initView(contentView);

        Set<String> valuesKey = mEventValues.keySet();
        for (String valueKey :valuesKey) {
            addParameterItemView(valueKey, String.valueOf(mEventValues.get(valueKey)), true, true);
        }
        mBtnAddParameter.setEnabled(true);

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

        EditText countValue = (EditText) contentView.findViewById(R.id.et_event_count_value);

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
                onEventNameChanged(mEventName, newEventName);
                mEventName = newEventName;
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
                    onEventValueChanged(mValue, value);
                    mValue = value;
                } catch (Exception e) {
                    mValue = 0.0f;
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
                    case TrackingSendActivity.TYPE_APPSFLYERS:
                    case TrackingSendActivity.TYPE_FACEBOOK:
                    case TrackingSendActivity.TYPE_HUAWEIHMS:
                    case TrackingSendActivity.TYPE_FIREBASE:
                        onParameterChanged(key, isKey, oldValue, newValue);
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
            ((TrackingSendActivity) activity).onEventNameChanged(mType,
                    oldName, newName);
        }
    }

    public void onEventValueChanged(float oldValue, float newValue) {
        Activity activity = getActivity();
        if(activity instanceof TrackingSendActivity) {
            ((TrackingSendActivity) activity).onCountValueChanged(mType,
                    oldValue, newValue);
        }
    }

    public void onParameterChanged(String key, boolean isKey, Object oldValue, Object newValue) {
        Activity activity = getActivity();
        if(activity instanceof TrackingSendActivity) {
            ((TrackingSendActivity) activity).onParameterChanged(mType,
                    key, isKey, oldValue, newValue);
        }

        if(isKey) {
            if(null == oldValue || StringUtil.isEmpty(String.valueOf(oldValue))) {
                mEventValues.put(String.valueOf(newValue), "");
            } else if(mEventValues.containsKey(key)){
                Object value = mEventValues.get(key);
                mEventValues.remove(key);
                mEventValues.put(String.valueOf(newValue), value);
            } else {
                mEventValues.put(String.valueOf(newValue), "");
            }
        } else {
            if(null == newValue) { // newValue 为null，删除
                if(mEventValues.containsKey(key)) {
                    mEventValues.remove(key);
                }
            } else {
                mEventValues.put(key, newValue);
            }
        }
    }

}
