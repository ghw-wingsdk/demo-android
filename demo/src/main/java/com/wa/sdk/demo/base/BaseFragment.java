package com.wa.sdk.demo.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wa.sdk.common.utils.StringUtil;
import com.wa.sdk.demo.widget.LoadingDialog;


/**
 * Fragment基类
 * Created by yinglovezhuzhu@gmail.com on 2015/10/30.
 */
public class BaseFragment extends Fragment implements View.OnClickListener {

    protected LoadingDialog mLoadingDialog = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 取消异步线程任务
     * @param task
     */
    protected void cancelTask(AsyncTask task) {
        if(null != task && !task.isCancelled()) {
            task.cancel(true);
            task = null;
        }
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
        mLoadingDialog = LoadingDialog.showLoadingDialog(getActivity(), message,
                cancelable, canceledOnTouchOutside, cancelListener);
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
     * 显示一个短Toast
     * @param text
     */
    protected void showShortToast(CharSequence text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示一个短Toast
     * @param resId
     */
    protected void showShortToast(int resId) {
        Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示一个长Toast
     * @param text
     */
    protected void showLongToast(CharSequence text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示一个长Toast
     * @param resId
     */
    protected void showLongToast(int resId) {
        Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 获取资源id，如果没有找到，返回0
     * @param name
     * @param defType
     * @return
     */
    protected int getIdentifier(String name, String defType) {
        return getResources().getIdentifier(name, defType, getActivity().getPackageName());
    }

    protected int getResourceColor(int resId) {
        return getResources().getColor(resId);
    }


    /**
     * 加载图片
     * @param url 图片地址
     * @param imageView 图片显示控件
     * @param width 显示宽度
     * @param height 显示高度
     * @param placeHolder 加载中图片
     * @param error 加载错误图片
     */
    protected void loadImage(String url, ImageView imageView, int width, int height, int placeHolder, int error) {
        if(StringUtil.isEmpty(url)) {
            return;
        }
        Picasso.with(getActivity())
                .load(Uri.parse(url))
                .placeholder(placeHolder)
                .error(error)
                .resize(width, height)
                .into(imageView);
    }

    /**
     * 加载图片
     * @param url 图片地址
     * @param imageView 图片显示控件
     * @param width 显示宽度
     * @param height 显示高度
     */
    protected void loadImageCenterCrop(String url, ImageView imageView, int width, int height, Callback callback) {
        if(StringUtil.isEmpty(url)) {
            return;
        }
        Picasso.with(getActivity())
                .load(Uri.parse(url))
                .resize(width, height)
                .centerCrop()
                .into(imageView, callback);
    }

    /**
     * 加载图片
     * @param url 图片地址
     * @param imageView 图片显示控件
     */
    protected void loadImage(String url, ImageView imageView, Callback callback) {
        if(StringUtil.isEmpty(url)) {
            return;
        }
        Picasso.with(getActivity())
                .load(Uri.parse(url))
                .into(imageView, callback);
    }

    /**
     * 退出<br/><br/>
     */
    protected void exit() {
        Activity activity = getActivity();
        if(activity instanceof BaseActivity) {
            ((BaseActivity) activity).exit();
        }
    }
    @Override
    public void onClick(View v) {

    }
}
