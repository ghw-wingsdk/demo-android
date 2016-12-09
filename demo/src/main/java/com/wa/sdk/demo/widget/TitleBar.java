package com.wa.sdk.demo.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wa.sdk.demo.R;


/**
 * 标题（简单的标题栏，两边有按钮，中间文字）
 * Created by yinglovezhuzhu@gmail.com on 2016/1/12.
 */
public class TitleBar extends ALinearLayout {

    private Button mBtnLeft;
    private Button mBtnRight;

    private View mLeftDivider;
    private View mRightDivider;

    private TextView mTvTitle;

    public TitleBar(Context context) {
        super(context);

        initView(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    /**
     * 设置标题背景
     * @param resId
     */
    public TitleBar setTitleBackGroundResource(int resId) {
        this.setBackgroundResource(resId);
        return this;
    }

    /**
     * 设置标题文字颜色
     * @param resId
     */
    public TitleBar setTitleTextColor(int resId) {
        ColorStateList textColor = getResourceColorStateList(resId);
        if(null != textColor) {
            mTvTitle.setTextColor(textColor);
        }
        return this;
    }

    /**
     * 设置标题文字
     * @param resId
     */
    public TitleBar setTitleText(int resId) {
        mTvTitle.setText(resId);
        return this;
    }

    /**
     * 设置标题文字
     * @param text
     */
    public TitleBar setTitleText(CharSequence text) {
        mTvTitle.setText(text);
        return this;
    }


    /**
     * 配置左按钮
     * @param iconResId 图标资源id
     * @param listener 点击监听
     * @return 当前标题栏对象
     */
    public TitleBar setLeftButton(int iconResId, View.OnClickListener listener) {
        setLeftButton(iconResId);
        mBtnLeft.setOnClickListener(listener);
        return this;
    }

    /**
     * 启用/禁用左1按钮
     * @param enabled
     * @return
     */
    public TitleBar setLeftButtonEnabled(boolean enabled) {
        mBtnLeft.setEnabled(enabled);
        return this;
    }

    /**
     * 配置右按钮
     * @param iconResId 图标资源id
     * @param listener 点击监听
     * @return 当前标题栏对象
     */
    public TitleBar setRightButton(int iconResId, View.OnClickListener listener) {
        setRightButton(iconResId);
        mBtnRight.setOnClickListener(listener);
        return this;
    }

    /**
     * 配置右按钮
     * @param text 文字
     * @return 当前标题栏对象
     */
    public TitleBar setRightButtonWithText(CharSequence text, View.OnClickListener listener) {
        mBtnRight.setText(text);
        mBtnRight.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        mBtnRight.setVisibility(View.VISIBLE);
        mBtnRight.setOnClickListener(listener);
        mRightDivider.setVisibility(VISIBLE);
        if(VISIBLE != mBtnLeft.getVisibility()) {
            mBtnLeft.setVisibility(INVISIBLE);
            mLeftDivider.setVisibility(GONE);
        }
        return this;
    }

    /**
     * 配置右按钮
     * @param resId 文字资源id
     * @return 当前标题栏对象
     */
    public TitleBar setRightButtonWithText(int resId, View.OnClickListener listener) {
        mBtnRight.setText(resId);
        mBtnRight.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        mBtnRight.setVisibility(View.VISIBLE);
        mBtnRight.setOnClickListener(listener);
        mRightDivider.setVisibility(VISIBLE);
        if(VISIBLE != mBtnLeft.getVisibility()) {
            mBtnLeft.setVisibility(INVISIBLE);
            mLeftDivider.setVisibility(GONE);
        }
        return this;
    }

    public TitleBar setRightButtonTextColorResource(int resId) {
        mBtnRight.setTextColor(getResourceColor(resId));
        return this;
    }

    public TitleBar setRightButtonBackgroundResource(int resId) {
        mBtnRight.setBackgroundResource(resId);
        return this;
    }

    /**
     * 启用/禁用右1按钮
     * @param enabled
     * @return
     */
    public TitleBar setRightButtonEnabled(boolean enabled) {
        mBtnRight.setEnabled(enabled);
        return this;
    }

    /**
     * 初始化UI
     * @param context
     * @param attrs
     */
    private void initView(Context context, AttributeSet attrs) {
        inflate(context, R.layout.layout_titlebar, this);

        mBtnLeft = (Button) findViewById(R.id.btn_titlebar_left);
        mBtnRight = (Button) findViewById(R.id.btn_titlebar_right);
        mTvTitle = (TextView) findViewById(R.id.tv_titlebar_title);

        mLeftDivider = findViewById(R.id.view_left_divider);
        mRightDivider = findViewById(R.id.view_right_divider);
    }

    /**
     * 配置左按钮
     * @param iconResId 图标资源id
     * @return 当前标题栏对象
     */
    private TitleBar setLeftButton(int iconResId) {
        mBtnLeft.setText("");
        mBtnLeft.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);
        mBtnLeft.setVisibility(VISIBLE);
        mLeftDivider.setVisibility(VISIBLE);
        if(VISIBLE != mBtnRight.getVisibility()) {
            mBtnRight.setVisibility(INVISIBLE);
            mRightDivider.setVisibility(GONE);
        }
        return this;
    }

    /**
     * 配置右按钮
     * @param iconResId 图标资源id
     * @return 当前标题栏对象
     */
    private TitleBar setRightButton(int iconResId) {
        mBtnRight.setText("");
        mBtnRight.setCompoundDrawablesWithIntrinsicBounds(0, 0, iconResId, 0);
        mBtnRight.setVisibility(VISIBLE);
        mRightDivider.setVisibility(VISIBLE);
        if(VISIBLE != mBtnLeft.getVisibility()) {
            mBtnLeft.setVisibility(INVISIBLE);
            mLeftDivider.setVisibility(GONE);
        }
        return this;
    }
}
