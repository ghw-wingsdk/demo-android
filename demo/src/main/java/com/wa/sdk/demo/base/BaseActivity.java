package com.wa.sdk.demo.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;
import com.wa.sdk.common.WASharedPrefHelper;
import com.wa.sdk.demo.LoginActivity;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.utils.WADemoConfig;
import com.wa.sdk.demo.utils.WASdkDemo;
import com.wa.sdk.demo.widget.LoadingDialog;
import com.wa.sdk.demo.widget.TitleBar;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;


/**
 * Activity基类
 */
public class BaseActivity extends FragmentActivity implements View.OnClickListener {
    protected static final String TAG = WADemoConfig.TAG;
    protected FragmentManager mFragmentManager;
    protected int mContainerId = 0;
    protected LoadingDialog mLoadingDialog = null;
    private static final boolean isSetFullScreen = true;
    private static final boolean isUseToast = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (isSetFullScreen) EdgeToEdge.enable((ComponentActivity) this);
        super.onCreate(savedInstanceState);
        mFragmentManager = getSupportFragmentManager();
        OnBackPressedCallback onBackPressedCallback = handleBackPressed();
        if (onBackPressedCallback != null) {
            // Log.w("zii-", this + " - demo -  getOnBackPressedDispatcher:" + onBackPressedCallback);
            getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        if (isSetFullScreen) setFullScreen();
    }


    @Override
    protected void onDestroy() {
        dismissLoadingDialog();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        hideKeyboard();
    }

    /**
     * exit
     */
    public void exit() {
        finish();
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

    protected void showShortToast(CharSequence text) {
        if (isUseToast) {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        } else {
            View view = findViewById(android.R.id.content).getRootView();
            Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * 显示一个短Toast
     *
     * @param resId
     */
    protected void showShortToast(int resId) {
        showShortToast(getString(resId));
    }

    protected void showLongToast(CharSequence text) {
        if (isUseToast) {
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        } else {
            View view = findViewById(android.R.id.content).getRootView();
            Snackbar.make(view, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * 显示一个长Toast
     *
     * @param resId
     */
    protected void showLongToast(int resId) {
        showLongToast(getString(resId));
    }

    protected void logIShortToast(String msg) {
        showShortToast(msg);
        logI(msg);
    }

    protected void logILongToast(String msg) {
        showLongToast(msg);
        logI(msg);
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
        if (this.isFinishing() || this.isDestroyed() || getWindow() == null) {
            return;
        }

        Window window = getWindow();

        // 刘海屏适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        View decorView = getWindow().getDecorView();
        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(window, decorView);
        // 浅色状态栏
        windowInsetsController.setAppearanceLightNavigationBars(true);
        // 设置全屏，隐藏系统栏，包括状态栏，导航栏
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        // 手势操作时短暂时间后隐藏（会影响操作返回）
        // windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        ViewCompat.setOnApplyWindowInsetsListener(decorView, (v, windowInsets) -> {
            // 获取 displayCutout 边衬size
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.displayCutout());
            Log.v(TAG, "setFullScreen - insets, left:" + insets.left + ", top:" + insets.top + ", right:" + insets.right + ", bottom:" + insets.bottom);

            TitleBar titleBar = findTitleBar(findViewById(android.R.id.content));
            if (titleBar != null) titleBar.setPadding(insets.left, insets.top, insets.right, 0);

            // Return CONSUMED if you don't want want the window insets to keep passing
            // down to descendant views.
            return WindowInsetsCompat.CONSUMED;
        });
    }

    /**
     * 判断是否已登录，若未登录则显示登录提示框引导登录
     */
    protected boolean isNotLoginAndTips() {
        boolean isNotLogin = !WASdkDemo.getInstance().isLogin();
        if (isNotLogin) showLoginTips();
        return isNotLogin;
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

    protected void logD(String text) {
        Log.d(TAG, text);
    }

    protected void logI(String text) {
        Log.i(TAG, text);
    }

    protected void logW(String text) {
        Log.w(TAG, text);
    }

    protected void logE(String text) {
        Log.e(TAG, text);
    }

    protected String getStackTrace(Throwable throwable) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        printWriter.close();
        String error = writer.toString();
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return error;

    }

    protected void setScreenOrientation() {
        int orientation = WASdkDemo.getInstance().getSpHelper().getInt(WADemoConfig.SP_KEY_SETTING_ORIENTATION, 0);
        if (orientation == 1) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (orientation == 2) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
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

    protected void setTitleBar(int titleResId) {
        TitleBar titleBar = findTitleBar(findViewById(android.R.id.content));
        if (titleBar != null) {
            titleBar.setTitleText(titleResId);
            titleBar.setLeftButton(android.R.drawable.ic_menu_revert, v -> exit());
            titleBar.setTitleTextColor(R.color.color_white);
        }
    }

    protected void delayCall(float delaySecond, Runnable runnable) {
        if (delaySecond <= 0) {
            runnable.run();
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(runnable, ((long) (delaySecond * 1000L)));
        }
    }

    protected WASharedPrefHelper getSpHelper() {
        return WASdkDemo.getInstance().getSpHelper();
    }

    protected void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /**
     * 返回手势处理
     */
    protected OnBackPressedCallback handleBackPressed() {
        return null;
    }
}

