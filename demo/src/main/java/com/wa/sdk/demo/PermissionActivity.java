package com.wa.sdk.demo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.model.WAPermissionCallback;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.widget.TitleBar;

public class PermissionActivity extends BaseActivity {

    private final int REQUEST_CODE_NOTIFICATION_PERMISSION = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_permission);

        TitleBar titleBar = findViewById(R.id.tb_permission);
        titleBar.setTitleText(R.string.request_permisssion);
        titleBar.setLeftButton(android.R.drawable.ic_menu_revert, v -> finish());
        titleBar.setTitleTextColor(R.color.color_white);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_request_permission:
                callNotificationPermission(this);
                break;
            case R.id.btn_notification_permission:
                testNotificationPermission();
                break;
            default:
                break;
        }
    }

    private void testNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            WACommonProxy.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS, true,
                    "通知权限被拒，为了您方便接收礼包推送消息，请开启通知权限",
                    "测试权限申请:POST_NOTIFICATIONS，已经被拒绝，请到设置中开启该权限。", new WAPermissionCallback() {
                        @Override
                        public void onCancel() {
                            showShortToast("check permission canceled");
                        }

                        @Override
                        public void onRequestPermissionResult(String[] permissions, boolean[] grantedResults) {
                            String msg = "Request permission result:\n";
                            if (permissions.length > 0) {
                                for (int i = 0; i < permissions.length; i++) {
                                    msg += permissions[i] + "--" + (grantedResults[i] ? "granted" : "denied");
                                }
                            }
                            showShortToast(msg);
                        }
                    });
        }else {
            //系统低于 Android 13 无需授权通知权限。应用默认开启通知
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("系统版本低于Android 13，无需授权通知权限");
            dialog.setPositiveButton("确定", null);
            dialog.show();
        }
    }

    private void testPermissionNative() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("通知权限申请");
        if (Build.VERSION.SDK_INT >= 33) {
            String permission = Manifest.permission.POST_NOTIFICATIONS;
            int checkSelfPermission = ContextCompat.checkSelfPermission(this, permission);
            if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {
                //已经授权
                dialog.setMessage("用户已授权该权限");
                dialog.setPositiveButton("确定", null);
            } else if (shouldShowRequestPermissionRationale(permission)) {
                //应该对权限进行说明
                dialog.setMessage("未授权，应该给予用户权限说明\n示例说明：该权限用于通知提醒用户每日领取奖励");
                dialog.setNegativeButton("不，谢谢", null);
                dialog.setPositiveButton("授权", (dialogInterface, i) -> {
                    requestPermissions(new String[]{permission}, REQUEST_CODE_NOTIFICATION_PERMISSION);
                });
            }else {
                //未授权
                dialog.setMessage("未授权，请点击授权进行权限申请");
                dialog.setNegativeButton("取消", null);
                dialog.setPositiveButton("授权", (dialogInterface, i) -> {
                    requestPermissions(new String[]{permission}, REQUEST_CODE_NOTIFICATION_PERMISSION);
                });
            }
        } else {
            dialog.setMessage("系统版本低于Android 13，无需授权通知权限");
            dialog.setPositiveButton("确定", null);
        }
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        WACommonProxy.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("通知权限申请");
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dialog.setMessage("权限授权成功");
            } else {
                dialog.setMessage("已拒绝授权，应用将无法使用通知功能");
            }
            dialog.setPositiveButton("确定", null);
            dialog.show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        WACommonProxy.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    public static void callNotificationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 33) {
            WACommonProxy.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS, true,
                    "通知权限被拒，为了您方便接收礼包推送消息，请开启通知权限",
                    "测试权限申请:POST_NOTIFICATIONS，已经被拒绝，请到设置中开启该权限。", new WAPermissionCallback() {
                        @Override
                        public void onCancel() {
                            View view = activity.findViewById(android.R.id.content).getRootView();
                            Snackbar.make(view,"check permission canceled",Snackbar.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onRequestPermissionResult(String[] permissions, boolean[] grantedResults) {
                            String msg = "Request permission result:\n";
                            if (permissions.length > 0) {
                                for (int i = 0; i < permissions.length; i++) {
                                    msg += permissions[i] + "--" + (grantedResults[i] ? "granted" : "denied");
                                }
                            }
                            View view = activity.findViewById(android.R.id.content).getRootView();
                            Snackbar.make(view,msg,Snackbar.LENGTH_SHORT).show();
                        }
                    });
        }else {
            //系统低于 Android 13 无需授权通知权限。应用默认开启通知
            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setMessage("系统版本低于Android 13，无需授权通知权限");
            dialog.setPositiveButton("确定", null);
            dialog.show();
        }
    }

}
