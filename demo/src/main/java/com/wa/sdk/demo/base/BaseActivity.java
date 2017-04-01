package com.wa.sdk.demo.base;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import com.wa.sdk.demo.widget.LoadingDialog;
import com.wa.sdk.track.WATrackProxy;


/**
 * Activity基类
 * Created by yinglovezhuzhu@gmail.com on 2015/12/28.
 */
public class BaseActivity extends FragmentActivity implements View.OnClickListener {

    protected FragmentManager mFragmentManager;

    protected int mContainerId = 0;

    protected LoadingDialog mLoadingDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideNavigationBar();

        mFragmentManager = getSupportFragmentManager();
    }

    @Override
    protected void onResume(){
        super.onResume();
//        WATrackProxy.startHeartBeat(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        WATrackProxy.stopHeartBeat(this);
    }

    @Override
    protected void onDestroy() {
        dismissLoadingDialog();
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            hideNavigationBar();
        }
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 入栈
     * @param fragment 入栈的Fragment
     */
    public void addFragmentToStack(Fragment fragment) {
        // Add the fragment to the activity, pushing this transaction
        // on to the back stack.
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(mContainerId, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * 带自定义动画的入栈
     * @param fragment
     * @param enter
     * @param exit
     * @param popEnter
     * @param popExit
     */
    public void addFragmentToStackWithAnimation(Fragment fragment,
                                                @android.support.annotation.AnimRes int enter,
                                                @android.support.annotation.AnimRes int exit,
                                                @android.support.annotation.AnimRes int popEnter,
                                                @android.support.annotation.AnimRes int popExit) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.setCustomAnimations(enter, exit, popEnter, popExit);
        ft.replace(mContainerId, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * 回退(如果入栈使用了动画，那么出栈自动回有动画)<br/>
     * 当栈内只有一个Fragment的时候，退出Activity
     */
    public void popBack() {
        if(mFragmentManager.getBackStackEntryCount() > 1) {
            // 栈内的Fragment大于1，退到上一个
            mFragmentManager.popBackStack(mFragmentManager.getBackStackEntryAt(mFragmentManager.getBackStackEntryCount() - 1).getId(),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            // 栈内的Fragment小于于1，退出Activity
            exit();
        }
    }

    /**
     * exit
     */
    public void exit() {

    }

    /**
     * 显示LoadingDialog
     * @param message
     * @param cancelable
     * @param canceledOnTouchOutside
     * @param cancelListener
     * @return
     */
    public LoadingDialog showLoadingDialog(String message,
                                           boolean cancelable,
                                           boolean canceledOnTouchOutside,
                                           DialogInterface.OnCancelListener cancelListener) {
        if(null == mLoadingDialog || !mLoadingDialog.isShowing()) {
            mLoadingDialog = LoadingDialog.showLoadingDialog(this, message,
                    cancelable, canceledOnTouchOutside, cancelListener);
        }
        return mLoadingDialog;
    }

    /**
     * 显示LoadingDialog
     * @param message
     * @param cancelListener
     * @return
     */
    public LoadingDialog showLoadingDialog(String message,
                                           DialogInterface.OnCancelListener cancelListener) {
        return showLoadingDialog(message, true, false, cancelListener);
    }

    /**
     * 隐藏LoadingDialog
     */
    public void cancelLoadingDialog() {
        if(null != mLoadingDialog && mLoadingDialog.isShowing()) {
            mLoadingDialog.cancel();
        }
        mLoadingDialog = null;
    }

    /**
     * 隐藏LoadingDialog
     */
    public void dismissLoadingDialog() {
        if(null != mLoadingDialog && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        mLoadingDialog = null;
    }

    /**
     * 显示一个短Toast
     * @param text
     */
    protected void showShortToast(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示一个短Toast
     * @param resId
     */
    protected void showShortToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示一个长Toast
     * @param text
     */
    protected void showLongToast(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示一个长Toast
     * @param resId
     */
    protected void showLongToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 获取资源id，如果没有找到，返回0
     * @param name
     * @param defType
     * @return
     */
    protected int getIdentifier(String name, String defType) {
        return getResources().getIdentifier(name, defType, getPackageName());
    }

    /**
     * 获取某个颜色
     * @param resId
     * @return
     */
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

    /**
     * 获取Resource中的ColorStateList
     * @param resId
     * @return
     */
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

    /**
     * 隐藏Navigation Bar
     */
    protected void hideNavigationBar() {
        int flags =View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        getWindow().getDecorView().setSystemUiVisibility(flags);
    }
}
