package com.wa.sdk.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.model.WAPermissionCallback;
import com.wa.sdk.demo.base.BaseActivity;

public class PermissionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        setTitleBar(R.string.request_permission);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_notification_permission) {
            // 通知权限申请，建议在玩家进服后调用该方法进行权限申请
            WACommonProxy.requestNotificationPermission(this);
        } else if (id == R.id.btn_request_permission) {
            // 通用的权限申请示例（通知权限请参考：通知权限申请示例）
            callSamplePermission();
        }
    }

    /**
     * 完整的权限使用示例。如需申请通知权限，请查看{@link WACommonProxy#requestNotificationPermission(Activity)}方法
     */
    private void callSamplePermission() {
        // 用户拒绝权限申请后弹窗显示的文案。强制申请为false时，不会有提示，可以传null
        String denyConfirmMsg = "如果您不允许游戏访问位置，您将无法使用 xxx 功能";
        // 用户拒绝权限申请并勾选不再询问后，弹出要求到设置中打开权限对话框中显示的消息文字。强制申请为false时，不会有提示，可以传null
        String permissionSettingMsg = "游戏需要获取您的位置来使用 xxx 功能，请在设置中开启位置权限";
        WACommonProxy.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, true,
                denyConfirmMsg, permissionSettingMsg, new WAPermissionCallback() {
                    @Override
                    public void onCancel() {
                        // 取消授权
                        showShortToast("check permission canceled");
                    }

                    @Override
                    public void onRequestPermissionResult(String[] permissions, boolean[] grantedResults) {
                        // 处理授权结果，判断是否通过授权
                        String msg = "Request permission result:\n";
                        if (permissions.length > 0) {
                            for (int i = 0; i < permissions.length; i++) {
                                msg += permissions[i] + "--" + (grantedResults[i] ? "granted" : "denied");
                            }
                        }
                        showShortToast(msg);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        WACommonProxy.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        WACommonProxy.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

}
