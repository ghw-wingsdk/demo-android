package com.wa.sdk.demo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.wa.sdk.WAConstants;
import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.model.WAResult;
import com.wa.sdk.common.utils.StringUtil;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.utils.WASdkDemo;
import com.wa.sdk.social.WASocialProxy;
import com.wa.sdk.user.WAUserProxy;
import com.wa.sdk.user.model.WAAccount;
import com.wa.sdk.user.model.WAAccountCallbackV2;
import com.wa.sdk.user.model.WAAccountResult;
import com.wa.sdk.user.model.WABindCallback;
import com.wa.sdk.user.model.WABindResult;
import com.wa.sdk.user.model.WABindResultV2;
import com.wa.sdk.user.model.WACertificationInfo;
import com.wa.sdk.user.model.WALoginResult;
import com.wa.sdk.user.model.WALoginResultV2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 账号管理，包括账号管理界面，账号绑定，账号解绑，账号切换，新建账号
 */
public class AccountManagerActivity extends BaseActivity {

    private final String[] mAccountTypeArray = new String[]{
            WAConstants.CHANNEL_FACEBOOK,
            WAConstants.CHANNEL_GOOGLE,
            WAConstants.CHANNEL_VK,
            WAConstants.CHANNEL_TWITTER,
            WAConstants.CHANNEL_INSTAGRAM,
            WAConstants.CHANNEL_GHG,
            WAConstants.CHANNEL_R2,
            WAConstants.CHANNEL_GUEST,
            WAConstants.CHANNEL_WA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manager);
        setTitleBar(R.string.account_manager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!WACommonProxy.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        // 常用接入（一般只接入 openAccountManagerV2 接口即可）
        if (id == R.id.btn_account_manager_page) {
            // 打开账号管理界面，内部包含账号绑定，解绑，切换，新建账号等功能
            openAccountManager();
        }

        // 其他接口功能
        if (id == R.id.btn_bind_account) {
            // 绑定第三方平台账户
            bindAccount();
        } else if (id == R.id.btn_create_account) {
            // 新建账号（游客）
            createAccount();
        } else if (id == R.id.btn_switch_account) {
            // 切换账号
            switchAccount();
        } else if (id == R.id.btn_query_bound_account) {
            // 查询已绑定的第三方平台账户
            queryBoundAccount();
        }
    }

    /**
     * 绑定第三方平台账户
     */
    private void bindAccount() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.bind_account)
                .setItems(mAccountTypeArray, (dialog, which) -> {
                    String platform = mAccountTypeArray[which];
                    showLoadingDialog(String.format("Login %s account...", platform), null);
                    WAUserProxy.bindingAccount(AccountManagerActivity.this, platform, "", new WABindCallback() {
                        @Override
                        public void onLoginAccount(String platform) {
                            if (null != mLoadingDialog) {
                                mLoadingDialog.setMessage(String.format("Login %s account...", platform));
                            }
                        }

                        @Override
                        public void onBindingAccount(String accessToken, String platform) {
                            if (null != mLoadingDialog) {
                                mLoadingDialog.setMessage(String.format("Binding %s account...", platform));
                            }
                        }

                        @Override
                        public void onSuccess(int code, String message, WABindResult result) {
                            cancelLoadingDialog();
                            String msg = "绑定成功:"
                                    + "\ncode:" + result.getCode()
                                    + "\nmessage:" + result.getMessage()
                                    + "\nplatform:" + result.getPlatform()
                                    + "\naccess_token:" + result.getAccessToken()
                                    + "\nplatform_uid:" + result.getPlatformUserId();
                            showLongToast(msg);
                            Log.i(TAG, msg);
                            if (WAConstants.CHANNEL_FACEBOOK.equals(result.getPlatform())) {
                                WASocialProxy.inviteInstallReward(AccountManagerActivity.this, WAConstants.CHANNEL_FACEBOOK, new WACallback<WAResult>() {
                                    @Override
                                    public void onSuccess(int code, String message, WAResult result) {
                                        showShortToast("invite install reward success: ");
                                    }

                                    @Override
                                    public void onCancel() {
                                        showShortToast("invite install reward canceled: ");

                                    }

                                    @Override
                                    public void onError(int code, String message, WAResult result, Throwable throwable) {
                                        showShortToast("invite install reward error: " + code + "<>" + message);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancel() {
                            cancelLoadingDialog();
                            String msg = "Binding canceled";
                            showLongToast(msg);
                            Log.i(TAG, msg);
                        }

                        @Override
                        public void onError(int code, String message, WABindResult result, Throwable throwable) {
                            cancelLoadingDialog();
                            String msg = "Binding error: " + message + "->" + (null == throwable ? "" : throwable);
                            showLongToast(msg);
                            Log.i(TAG, msg);
                        }
                    });
                })
                .setPositiveButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }


    /**
     * 创建新用户
     */
    private void createAccount() {
        new AlertDialog.Builder(this)
                .setTitle("Create Account")
                .setMessage("This operation may be lost you user data, go ahead?")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.cancel();
                    showLoadingDialog("Creating new account...", null);
                    WAUserProxy.createNewAccount(new WACallback<WALoginResult>() {
                        @Override
                        public void onSuccess(int code, String message, WALoginResult result) {
                            String text = "code:" + code + "\nmessage:" + message;
                            if (null == result) {
                                text = "Login failed->" + text;
                            } else {
                                text = "Login success->" + text
                                        + "\nplatform:" + result.getPlatform()
                                        + "\nuserId:" + result.getUserId()
                                        + "\ntoken:" + result.getToken()
                                        + "\nplatformUserId:" + result.getPlatformUserId()
                                        + "\nplatformToken:" + result.getPlatformToken();

                                WASdkDemo.getInstance().updateLoginAccount(result);
                            }
                            logI(text);
                            cancelLoadingDialog();
                            showLongToast(text);
                        }

                        @Override
                        public void onCancel() {
                            cancelLoadingDialog();
                            logI("Create account canceled");
                            showShortToast("Create account canceled");
                        }

                        @Override
                        public void onError(int code, String message, WALoginResult result, Throwable throwable) {
                            cancelLoadingDialog();
                            String text = "code:" + code + "\nmessage:" + message;
                            logI("create account failed->" + text);
                            showShortToast("Login failed->" + text);
                        }
                    });
                })
                .show();
    }


    /**
     * 查询已绑定的账号
     */
    private void queryBoundAccount() {
        showLoadingDialog(getString(R.string.loading), null);
        WAUserProxy.queryBoundAccount(new WACallback<WAAccountResult>() {
            @Override
            public void onSuccess(int code, String message, WAAccountResult result) {
                cancelLoadingDialog();
                if (null == result) {
                    showShortToast(R.string.error_loading_data);
                    return;
                }
                final List<WAAccount> boundAccounts = result.getAccounts();
                if (null == boundAccounts || boundAccounts.isEmpty()) {
                    showShortToast(R.string.not_bound_any_account);
                    return;
                }
                final BoundAccountAdapter adapter = new BoundAccountAdapter(AccountManagerActivity.this);
                adapter.addAll(boundAccounts, true);
                new AlertDialog.Builder(AccountManagerActivity.this)
                        .setTitle(R.string.bound_accounts)
                        .setAdapter(adapter, (dialog, which) -> {
                        })
                        .setPositiveButton(R.string.ok, (dialog, which) -> dialog.cancel())
                        .show();
            }

            @Override
            public void onCancel() {
                cancelLoadingDialog();

            }

            @Override
            public void onError(int code, String message, WAAccountResult result, Throwable throwable) {
                cancelLoadingDialog();
                showLongToast("Query bound account error: " + message + "->" + (null == throwable ? "" : throwable));
            }
        });
    }


    /**
     * 切换账号
     */
    private void switchAccount() {
        new AlertDialog.Builder(this)
                .setTitle("Choose account type")
                .setItems(mAccountTypeArray, (dialog, which) -> {
                    String platform = mAccountTypeArray[which];
                    showLoadingDialog("Login", null);
                    WAUserProxy.switchAccount(AccountManagerActivity.this, platform, new WACallback<WALoginResult>() {
                        @Override
                        public void onSuccess(int code, String message, WALoginResult result) {
                            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                                mLoadingDialog.dismiss();
                            }
                            if (null == result) {
                                return;
                            }
                            String text = "code:" + code + "\nmessage:" + message;
                            text = "Login success->" + text
                                    + "\nplatform:" + result.getPlatform()
                                    + "\nuserId:" + result.getUserId()
                                    + "\ntoken:" + result.getToken()
                                    + "\nplatformUserId:" + result.getPlatformUserId()
                                    + "\nplatformToken:" + result.getPlatformToken();
                            WASdkDemo.getInstance().updateLoginAccount(result);
                            cancelLoadingDialog();
                            showShortToast(text);
                        }

                        @Override
                        public void onCancel() {
                            cancelLoadingDialog();
                            showLongToast("Cancel to login with google");
                        }

                        @Override
                        public void onError(int code, String message, WALoginResult result, Throwable throwable) {
                            cancelLoadingDialog();
                            showLongToast(message + "\n" + (null == throwable ? "" : throwable.getMessage()));
                        }
                    });
                })
                .setPositiveButton("Cancel", (dialog, which) -> {
                }).show();
    }

    private void openAccountManager() {
        WAUserProxy.openAccountManagerV2(this, new WAAccountCallbackV2() {
            @Override
            public void onLoginAccountChanged(WALoginResultV2 currentAccount) {
                WASdkDemo.getInstance().updateLoginAccount(currentAccount);
                String text = "登录的账号发生变更（SDK已切换到另一个账号，或登录到新账号），当前新的登录的账号信息："
                        + "\nplatform:" + currentAccount.getPlatform()
                        + "\nuserId:" + currentAccount.getUserId()
                        + "\ntoken:" + currentAccount.getToken()
                        + "\nisBindAccount: " + currentAccount.getIsBindAccount()
                        + "\nisGuestAccount: " + currentAccount.getIsGuestAccount();

                showLongToast(text);
                logI(text);

                // 游戏需要回到登录界面，然后可以直接使用最新的账号信息完成游戏登录，无需重新走SDK登录过程，也可以重新自动走一遍SDK登录
                // backToLogin()
            }

            @Override
            public void onBoundAccountChanged(boolean binding, final WABindResultV2 result) {
                String sb = "绑定账户信息发生变更（绑定或解绑其他平台账号成功）:" +
                        "\n" + "状态: " + (binding ? "绑定" : "解绑") +
                        "\n" + "code: " + result.getCode() +
                        "\n" + "message: " + result.getMessage() +
                        "\n" + "platform: " + result.getPlatform() +
                        "\n" + "email: " + result.getEmail() +
                        "\n" + "mobile: " + result.getMobile();

                showShortToast(sb);
                logI(sb);

                if (binding && WACallback.CODE_SUCCESS == result.getCode()) {
                    // 绑定成功，如果需要绑定账号发奖励，可以在这里处理
                } else if (!binding && WACallback.CODE_SUCCESS == result.getCode()) {
                    // 解绑成功
                }
            }

            @Override
            public void onRealNameAuthChanged(WAResult<WACertificationInfo> waResult) {
                // 忽略，无需处理
            }
        });
    }

    private class BoundAccountAdapter extends BaseAdapter {

        private final Context mmContext;
        private final List<WAAccount> mmBoundAccounts = new ArrayList<>();

        public BoundAccountAdapter(Context context) {
            this.mmContext = context;
        }

        public void addAll(Collection<WAAccount> accounts, boolean notifyDataSetChanged) {
            if (null == accounts || accounts.isEmpty()) {
                return;
            }
            mmBoundAccounts.addAll(accounts);
            if (notifyDataSetChanged) {
                notifyDataSetChanged();
            }
        }

        public void remove(int position, boolean notifyDataSetChange) {
            if (position < 0) {
                return;
            }
            mmBoundAccounts.remove(position);
            if (notifyDataSetChange) {
                notifyDataSetChanged();
            }
        }

        public void remove(WAAccount account, boolean notifyDataSetChange) {
            if (null == account) {
                return;
            }
            mmBoundAccounts.remove(account);
            if (notifyDataSetChange) {
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return mmBoundAccounts.size();
        }

        @Override
        public WAAccount getItem(int position) {
            return mmBoundAccounts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ItemView itemView = null;
            if (null == convertView) {
                convertView = View.inflate(mmContext, R.layout.item_bound_account, null);
                itemView = new ItemView();
                itemView.account = convertView.findViewById(R.id.tv_item_bound_account_id);
                itemView.type = convertView.findViewById(R.id.tv_item_bound_account_type);
                itemView.unbind = convertView.findViewById(R.id.btn_item_bound_account_unbind);
                convertView.setTag(itemView);
            } else {
                itemView = (ItemView) convertView.getTag();
            }
            final WAAccount account = getItem(position);
            itemView.account.setText(account.getPlatformUserId());
            String platform = account.getPlatform();
            itemView.type.setText(StringUtil.isEmpty(platform) ? "Unknown" : platform);
            itemView.unbind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unbindAccount(account);
                }
            });
            return convertView;
        }

        private void unbindAccount(final WAAccount account) {
            showLoadingDialog(getString(R.string.loading), null);
            WAUserProxy.unBindAccount(account.getPlatform(), account.getPlatformUserId(), new WACallback<WAResult>() {
                @Override
                public void onSuccess(int code, String message, WAResult result) {
                    BoundAccountAdapter.this.remove(account, true);
                    cancelLoadingDialog();
                    showShortToast(R.string.unbind_account_success);
                }

                @Override
                public void onCancel() {
                    cancelLoadingDialog();

                }

                @Override
                public void onError(int code, String message, WAResult result, Throwable throwable) {
                    cancelLoadingDialog();
                    showShortToast(getString(R.string.unbind_account_error) + ": " + message + "->"
                            + (null == throwable ? "" : throwable));
                }
            });
        }
    }

    private class ItemView {
        TextView account;
        TextView type;
        Button unbind;
    }
}
