package com.wa.sdk.demo.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wa.sdk.demo.R;

/**
 * 
 */
public class EventItemView extends ALinearLayout implements View.OnClickListener {

    private EditText mEtKey;
    private EditText mEtValue;
    private Button mBtnDel;

    private OnDeleteListener mOnDeleteListener;
    private OnDataChangedListener mDataChangedListener;

    private int mType = -1;
    private String mKey = "";
    private String mValue = "";

    public EventItemView(Context context) {
        super(context);
        initView(context);
    }

    public EventItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public EventItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public EventItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_item_event_delete) {
            if (null != mOnDeleteListener) {
                mOnDeleteListener.onDelete(EventItemView.this);
            }
        }
    }

    public EventItemView setOnDeleteListener(OnDeleteListener listener) {
        this.mOnDeleteListener = listener;
        setDeletable(null != listener);
        return this;
    }

    public EventItemView setDeletable(boolean deletable) {
        mBtnDel.setVisibility(deletable ? VISIBLE : INVISIBLE);
        return this;
    }

    public EventItemView setKeyEditable(boolean editable) {
        mEtKey.setEnabled(editable);
        return this;
    }

    public EventItemView setValueEditable(boolean editable) {
        mEtValue.setEnabled(editable);
        return this;
    }

    public EventItemView setValueInputType(int inputType) {
        mEtValue.setInputType(inputType);
        return this;
    }

    public EventItemView setOnDataChangedListener(OnDataChangedListener listener) {
        this.mDataChangedListener = listener;
        return this;
    }

    public EventItemView setKey(String key) {
        this.mKey = key;
        mEtKey.setText(mKey);
        return this;
    }

    public EventItemView setValue(String value) {
        this.mValue = value;
        mEtValue.setText(value);
        return this;
    }

    public EventItemView setType(int type) {
        this.mType = type;
        return this;
    }


    public String getKey() {
        return mKey;
    }

    public String getValue() {
        return mValue;
    }

    private void initView(Context context) {
        inflate(context, R.layout.item_event_send, this);

        mEtKey = (EditText) findViewById(R.id.et_item_event_key);
        mEtValue = (EditText) findViewById(R.id.et_item_event_value);
        mBtnDel = (Button) findViewById(R.id.btn_item_event_delete);

        mBtnDel.setOnClickListener(this);
        mEtKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(null != mDataChangedListener) {
                    mDataChangedListener.onDataChanged(mType, mKey, true, mKey, s.toString());
                }
                mKey = s.toString();
            }
        });
        mEtValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(null != mDataChangedListener) {
                    mDataChangedListener.onDataChanged(mType, mKey, false, mValue, s.toString());
                }
                mValue = s.toString();
            }
        });

    }

    public interface OnDeleteListener {
        public void onDelete(EventItemView view);
    }

    public interface OnDataChangedListener {

        public void onDataChanged(int type, String key, boolean isKey, Object oldValue, Object newValue);
    }
}
