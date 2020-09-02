package com.wa.sdk.demo.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wa.sdk.common.utils.StringUtil;

/**
 * 自定义控件基类
 * Created by yinglovezhuzhu@gmail.com on 2015/10/18.
 */
public class ALinearLayout extends LinearLayout {

    protected final Context mContext;

    protected final Resources mResources;

    protected final String mPackageName;

    public ALinearLayout(Context context) {
        super(context);
        mContext = getContext();
        mResources = mContext.getResources();
        mPackageName = mContext.getPackageName();
    }

    public ALinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = getContext();
        mResources = mContext.getResources();
        mPackageName = mContext.getPackageName();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ALinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = getContext();
        mResources = mContext.getResources();
        mPackageName = mContext.getPackageName();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ALinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = getContext();
        mResources = mContext.getResources();
        mPackageName = mContext.getPackageName();
    }

    protected ColorStateList getResourceColorStateList(int resId) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return getResources().getColorStateList(resId);
        } else {
            try {
                return getResources().getColorStateList(resId, null);
            } catch (NoSuchMethodError e) {
                return getResources().getColorStateList(resId);
            }
        }
    }

    protected int getResourceColor(int resId) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return getResources().getColor(resId);
        } else {
            try {
                return getResources().getColor(resId, null);
            } catch (NoSuchMethodError e) {
                return getResources().getColor(resId);
            }
        }
    }

    protected Drawable getResourceDrawable(int resId) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            drawable = getResources().getDrawable(resId);
        } else {
            try {
                drawable = getResources().getDrawable(resId, null);
            } catch (NoSuchMethodError e) {
                drawable = getResources().getDrawable(resId);
            }
        }
        return drawable;
    }

    protected int getIdentifier(String name, String defType) {
        int resId = mResources.getIdentifier(name, defType, mPackageName);
        if(0 == resId) {
            throw new IllegalStateException("'" + name  + "'"  + defType
                    + " resource dismissed, please put it to res/layout folder");
        }
        return resId;
    }

    protected int [] getIntArray(int id) {
        return mResources.getIntArray(id);
    }


}
