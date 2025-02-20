package com.wa.sdk.demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.wa.sdk.demo.R;


/**
 * Logcat tab
 * 
 */
public class TabView extends ALinearLayout {

    private TextView mTvTitle;

    public TabView(Context context) {
        super(context);
        initView(context);
    }

    public TabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public TabView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    public void setTitle(CharSequence title) {
        mTvTitle.setText(title);
    }

    public void setTitle(int resId) {
        mTvTitle.setText(resId);
    }

    private void initView(Context context) {
        inflate(context, R.layout.layout_tab_view, this);

        mTvTitle = (TextView) findViewById(R.id.tv_tab_view_title);
    }
}
