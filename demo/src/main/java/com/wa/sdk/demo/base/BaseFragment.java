package com.wa.sdk.demo.base;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wa.sdk.demo.widget.LoadingDialog;


/**
 * Fragment基类
 *
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

    @Override
    public void onClick(View v) {

    }
}
