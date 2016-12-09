package com.wa.sdk.demo.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import com.wa.sdk.demo.R;

/**
 * 自定义的加载框
 * Create by yinglovezhuzhu@gmail.com on 2015/09/26
 */
// 注意：这个方法有反射调用，请勿改动，如需改动，请同时更改相应的反射调用
public class LoadingDialog extends Dialog {

    private TextView mTvMessage;

    public LoadingDialog(Context context) {
        super(context, getIdentifier(context, "WASdkLoadingDialogTheme", "style"));
        initView(context);
    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
        initView(context);
    }

    public void setMessage(CharSequence message) {
        if(null == message || message.length() == 0) {
            mTvMessage.setVisibility(View.GONE);
            return;
        }
        mTvMessage.setVisibility(View.VISIBLE);
        mTvMessage.setText(message);
    }

    /**
     * 返回一个自定义的加载框，可配置显示消息，默认可以取消，点击外部不会取消
     * @param context Context
     * @param message ic_message_tab_normal
     * @return LoadingDialog 对象
     */
    public static LoadingDialog showLoadingDialog(Context context, String message) {
        return showLoadingDialog(context, message, true, false, null);
    }

    /**
     * 返回一个自定义加载框对象，可配置消息，是否可取消，点击外部是否可以取消，消失监听
     * @param context Context
     * @param message 可配置消息
     * @param cancelable 是否可取消
     * @param canceledOnTouchOutside 点击外部是否可以取消
     * @param cancelListener 消失监听
     * @return LoadingDialog 对象
     */
    // 注意：这个方法有反射调用，请勿改动，如需改动，请同时更改相应的反射调用
    public static LoadingDialog showLoadingDialog(Context context, String message, boolean cancelable,
                                           boolean canceledOnTouchOutside,
                                           OnCancelListener cancelListener) {
        LoadingDialog dialog = new LoadingDialog(context);
        dialog.setMessage(message);
        dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        dialog.show();
        return dialog;
    }

    // 继承父类，方便反射调用
    // 注意：这个方法有反射调用，请勿改动，如需改动，请同时更改相应的反射调用
    @Override
    public boolean isShowing() {
        return super.isShowing();
    }

    // 继承父类，方便反射调用
    // 注意：这个方法有反射调用，请勿改动，如需改动，请同时更改相应的反射调用
    @Override
    public void cancel() {
        super.cancel();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        if(hasFocus) {
//            hideNavigationBar();
//        }
    }

    private void initView(Context context) {

//        hideNavigationBar();

        setContentView(R.layout.layout_loading_dialog);

        mTvMessage = (TextView) findViewById(R.id.tv_loading_dialog_message);
    }

    private static int getIdentifier(Context context, String name, String type) {
        return context.getResources().getIdentifier(name, type, context.getPackageName());
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
