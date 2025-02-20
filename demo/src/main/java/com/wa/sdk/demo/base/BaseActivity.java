package com.wa.sdk.demo.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.wa.sdk.common.WASharedPrefHelper;
import com.wa.sdk.common.utils.LogUtil;
import com.wa.sdk.core.WASdkProperties;
import com.wa.sdk.demo.LoginActivity;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.WADemoConfig;
import com.wa.sdk.demo.widget.LoadingDialog;
import com.wa.sdk.demo.widget.TitleBar;


/**
 * Activity基类
 * 
 */
public class BaseActivity extends FragmentActivity implements View.OnClickListener {
    protected static final String TAG = "DemoSdk2";
    protected FragmentManager mFragmentManager;
    protected int mContainerId = 0;
    protected LoadingDialog mLoadingDialog = null;
    protected boolean mEnableToastLog = false;

    private static final boolean isUseToast = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        mFragmentManager = getSupportFragmentManager();
    }

    @Override
    protected void onResume() {
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
        if (hasFocus) {
            hideNavigationBar();
        }
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 入栈
     *
     * @param fragment 入栈的Fragment
     */
    public void addFragmentToStack(Fragment fragment) {
        // Add the fragment to the activity, pushing this transaction
        // on to the back stack.
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(mContainerId, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * 带自定义动画的入栈
     *
     * @param fragment
     * @param enter
     * @param exit
     * @param popEnter
     * @param popExit
     */
    public void addFragmentToStackWithAnimation(Fragment fragment,
                                                int enter,
                                                int exit,
                                                int popEnter,
                                                int popExit) {
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
        if (mFragmentManager.getBackStackEntryCount() > 1) {
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
     *
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
        if (null == mLoadingDialog || !mLoadingDialog.isShowing()) {
            mLoadingDialog = LoadingDialog.showLoadingDialog(this, message,
                    cancelable, canceledOnTouchOutside, cancelListener);
        }
        return mLoadingDialog;
    }

    /**
     * 显示LoadingDialog
     *
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
        if (null != mLoadingDialog && mLoadingDialog.isShowing()) {
            mLoadingDialog.cancel();
        }
        mLoadingDialog = null;
    }

    /**
     * 隐藏LoadingDialog
     */
    public void dismissLoadingDialog() {
        if (null != mLoadingDialog && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        mLoadingDialog = null;
    }

    /**
     * 显示一个短Toast
     *
     * @param text
     */
    protected void showShortToast(CharSequence text) {
        if (isUseToast) {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        } else {
            View view = findViewById(android.R.id.content).getRootView();
            Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show();
        }
        logToast(text.toString());
    }

    /**
     * 显示一个短Toast
     *
     * @param resId
     */
    protected void showShortToast(int resId) {
        if (isUseToast) {
            Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
        } else {
            View view = findViewById(android.R.id.content).getRootView();
            Snackbar.make(view, resId, Snackbar.LENGTH_SHORT).show();
        }
        logToast(getString(resId));
    }

    /**
     * 显示一个长Toast
     *
     * @param text
     */
    protected void showLongToast(CharSequence text) {
        if (isUseToast) {
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        } else {
            View view = findViewById(android.R.id.content).getRootView();
            Snackbar.make(view, text, Snackbar.LENGTH_LONG).show();
        }
        logToast(text.toString());
    }

    /**
     * 显示一个长Toast
     *
     * @param resId
     */
    protected void showLongToast(int resId) {
        if (isUseToast) {
            Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
        } else {
            View view = findViewById(android.R.id.content).getRootView();
            Snackbar.make(view, resId, Snackbar.LENGTH_LONG).show();
        }
        logToast(getString(resId));
    }

    /**
     * 获取资源id，如果没有找到，返回0
     *
     * @param name
     * @param defType
     * @return
     */
    protected int getIdentifier(String name, String defType) {
        return getResources().getIdentifier(name, defType, getPackageName());
    }

    /**
     * 获取某个颜色
     *
     * @param resId
     * @return
     */
    protected int getResourceColor(int resId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
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
     *
     * @param resId
     * @return
     */
    protected ColorStateList getResourceColorStateList(int resId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
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
        int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    /**
     * 设置全屏，兼容刘海屏
     */
    private void setFullScreen() {
        // WASharedPrefHelper sp = WASharedPrefHelper.newInstance(this, WADemoConfig.SP_CONFIG_FILE_DEMO);
        // int orientation = sp.getInt(WADemoConfig.SP_KEY_SETTING_ORIENTATION, 0);
        // // 横屏，或者强制竖屏，使用全面屏
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
        //         && (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE || orientation ==1)
        // ) {
        //     getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        //     getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        // }

        setDisplayCountFullScreen(getWindow(), (left, top, right, bottom) -> {
            TitleBar titleBar = findTitleBar(findViewById(android.R.id.content));
            if (titleBar == null) {
                return;
            }

            if (top > 0 || left > 0 || right > 0) {
                titleBar.setPadding(left, top, right, 0);
            }
        });
    }

    protected boolean isLoginAndTips() {
        boolean isLogin = WASdkProperties.getInstance().isLogin();
        if (!isLogin)
            showLoginTips();
        return isLogin;
    }

    protected void showLoginTips() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.warming)
                .setMessage(R.string.not_login_yet)
                .setPositiveButton(R.string.login_now, (dialog, which) -> {
                    Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                    intent.putExtra("auto_finish", true);
                    startActivity(intent);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel())
                .show();
    }

    protected void logToast(String text) {
        if (mEnableToastLog) {
            Log.d("DemoSdk2", text);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setFullScreen();
    }

    protected void setScreenOrientation() {
        WASharedPrefHelper sp = WASharedPrefHelper.newInstance(this, WADemoConfig.SP_CONFIG_FILE_DEMO);
        int orientation = sp.getInt(WADemoConfig.SP_KEY_SETTING_ORIENTATION, 0);
        if (orientation == 1) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (orientation == 2) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
    }

    private void setDisplayCountFullScreen(Window window, final HandleDisplayCutout handleDisplayCutout) {
        if (window == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        View decorView = window.getDecorView();
        decorView.post(() -> {
            DisplayCutout displayCutout;
            if (handleDisplayCutout != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                displayCutout = decorView.getRootWindowInsets().getDisplayCutout();
                if (displayCutout == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    displayCutout = window.getWindowManager().getDefaultDisplay().getCutout();
                }
                if (displayCutout != null) {
                    int left = displayCutout.getSafeInsetLeft();
                    int top = displayCutout.getSafeInsetTop();
                    int right = displayCutout.getSafeInsetRight();
                    int bottom = displayCutout.getSafeInsetBottom();
                    LogUtil.v(TAG, "displayCutout size, left:" + left + ", top:" + top + ", right:" + right + ", bottom:" + bottom);
                    handleDisplayCutout.handle(left, top, right, bottom);
                }

            }
        });
    }

    private TitleBar findTitleBar(View view) {
        TitleBar titleBar = null;
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();

            for (int i = 0; i < childCount; i++) {
                View childView = viewGroup.getChildAt(i);

                if (childView instanceof TitleBar) {
                    titleBar = (TitleBar) childView;
                    break;
                }

                // 递归遍历子 View
                titleBar = findTitleBar(childView);
                if (titleBar != null) {
                    break; // 如果找到了，直接跳出循环
                }
            }
        }
        return titleBar;
    }

    public interface HandleDisplayCutout {
        void handle(int left, int top, int right, int bottom);
    }

}

