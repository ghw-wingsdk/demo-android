package com.wa.sdk.demo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wa.sdk.WAConstants;
import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.WASharedPrefHelper;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.model.WAResult;
import com.wa.sdk.common.utils.LogUtil;
import com.wa.sdk.common.utils.StringUtil;
import com.wa.sdk.core.WACoreProxy;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.social.WASocialProxy;
import com.wa.sdk.track.WAEventParameterName;
import com.wa.sdk.track.WAEventType;
import com.wa.sdk.track.model.WAEvent;
import com.wa.sdk.user.WAUserProxy;
import com.wa.sdk.user.model.WAAccount;
import com.wa.sdk.user.model.WAAccountCallback;
import com.wa.sdk.user.model.WAAccountResult;
import com.wa.sdk.user.model.WABindCallback;
import com.wa.sdk.user.model.WABindResult;
import com.wa.sdk.user.model.WALoginResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * Created by yinglovezhuzhu@gmail.com on 2015/11/25.
 */
public class AccountManagerActivity extends BaseActivity {

    private TitleBar mTitlebar;

    private String [] mAccountTypeArray = new String [] {"Facebook", "Google", "VK", };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_manager);

        initView();

//        if (sharedPrefHelper.getBoolean("enable_extend", true)) {
//            GhwSdkExtend.showEntryFlowIcon(this);
//        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!WACommonProxy.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bind_account:
                bindAccount();
                break;
            case R.id.btn_create_account:
                createAccount();
                break;
            case R.id.btn_switch_account:
                switchAccount();
                break;
            case R.id.btn_query_bound_account:
                queryBoundAccount();
                break;
            case R.id.btn_get_account_info:
                startActivity(new Intent(this, GetAccountInfoActivity.class));
                break;
            case R.id.btn_account_manager_page:
                WAUserProxy.openAccountManager(this, new WAAccountCallback() {
                    @Override
                    public void onLoginAccountChanged(WALoginResult currentAccount) {
                        WASdkDemo.getInstance().updateLoginAccount(currentAccount);
                        String text = "Login success->"
                                + "\nplatform:" + currentAccount.getPlatform()
                                + "\nuserId:" + currentAccount.getUserId()
                                + "\ntoken:" + currentAccount.getToken()
                                + "\nplatformUserId:" + currentAccount.getPlatformUserId()
                                + "\nplatformToken:" + currentAccount.getPlatformToken();

                        LogUtil.i(LogUtil.TAG, text);
                        showLongToast(text);

                        // 数据收集
                        WACoreProxy.setServerId("165");
                        WACoreProxy.setGameUserId("gUid01");

                        WAEvent event = new WAEvent.Builder()
                                .setDefaultEventName(WAEventType.LOGIN)
                                .addDefaultEventValue(WAEventParameterName.LEVEL, 140)
                                .build();
                        event.track(AccountManagerActivity.this);
                    }

                    @Override
                    public void onBoundAccountChanged(boolean binding, final WABindResult result) {
                        final StringBuilder sb = new StringBuilder();

                        sb.append("-------onBoundAccountChanged------\n")
                                .append(binding ? "Bind Account" : "Unbind Account")
                                .append("\n")
                                .append("code: " + result.getCode())
                                .append("\n")
                                .append("message: " + result.getMessage())
                                .append("\n")
                                .append("platform: ")
                                .append(result.getPlatform())
                                .append("\n")
                                .append("platformUserId: ")
                                .append(result.getPlatformUserId())
                                .append("\n")
                                .append("platformToken: ")
                                .append(result.getAccessToken());

                        showShortToast(sb.toString());

                        if(binding && WACallback.CODE_SUCCESS == result.getCode()) {
                            WASocialProxy.inviteInstallReward(AccountManagerActivity.this, result.getPlatform(), new WACallback<WAResult>() {
                                @Override
                                public void onSuccess(int code, String message, WAResult r) {
                                    showShortToast(result.getPlatform() + " invite install reward send succeed");
                                }

                                @Override
                                public void onCancel() {
                                    showShortToast(result.getPlatform() + " invite install reward send canceled");

                                }

                                @Override
                                public void onError(int code, String message, WAResult result, Throwable throwable) {
                                    showShortToast("result.getPlatform() + \" invite install reward send error");
                                }
                            });
                        }

                    }
                });
                break;
            default:
                break;
        }
    }

    private void initView() {
        mTitlebar = (TitleBar) findViewById(R.id.tb_account_manager);
        mTitlebar.setTitleText(R.string.account_manager);
        mTitlebar.setLeftButton(android.R.drawable.ic_menu_revert, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitlebar.setTitleTextColor(R.color.color_white);


    }

    /**
     * 绑定第三方平台账户
     */
    private void bindAccount() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.bind_account)
                .setItems(mAccountTypeArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String platform = WAConstants.CHANNEL_WA;
                        switch (which) {
                            case 0:
                                platform = WAConstants.CHANNEL_FACEBOOK;
                                break;
                            case 1:
                                platform = WAConstants.CHANNEL_GOOGLE;
                                break;
                            case 2:
                                platform = WAConstants.CHANNEL_VK;
                                break;
                            default:
                                break;
                        }
                        showLoadingDialog(String.format("Login %s account...", platform), new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                            }
                        });
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
                                Toast.makeText(AccountManagerActivity.this, "Binding account success: " + result.getMessage(), Toast.LENGTH_LONG).show();
                                if(WAConstants.CHANNEL_FACEBOOK.equals(result.getPlatform())) {
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
                                Toast.makeText(AccountManagerActivity.this, "Binding canceled", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onError(int code, String message, WABindResult result, Throwable throwable) {
                                cancelLoadingDialog();
                                Toast.makeText(AccountManagerActivity.this, "Binding error: " + message + "->"
                                        + (null == throwable ? "" : throwable), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


    /**
     * 创建新用户
     */
    private void createAccount() {
        new AlertDialog.Builder(this)
                .setTitle("Create Account")
                .setMessage("This operation may be lost you user data, go ahead?")
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
                                        LogUtil.i(LogUtil.TAG, text);
                                        cancelLoadingDialog();
                                        showLongToast(text);
                                    }

                                    @Override
                                    public void onCancel() {
                                        cancelLoadingDialog();
                                        LogUtil.i(LogUtil.TAG, "Create account canceled");
                                        showShortToast("Create account canceled");
                                    }

                                    @Override
                                    public void onError(int code, String message, WALoginResult result, Throwable throwable) {
                                        cancelLoadingDialog();
                                        String text = "code:" + code + "\nmessage:" + message;
                                        LogUtil.i(LogUtil.TAG, "create account failed->" + text);
                                        showShortToast("Login failed->" + text);
                                    }
                                });
                            }
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
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }

            @Override
            public void onCancel() {
                cancelLoadingDialog();

            }

            @Override
            public void onError(int code, String message, WAAccountResult result, Throwable throwable) {
                cancelLoadingDialog();
                Toast.makeText(AccountManagerActivity.this, "Query bound account error: " + message + "->"
                        + (null == throwable ? "" : throwable), Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * 切换账号
     */
    private void switchAccount() {
        new AlertDialog.Builder(this)
                .setTitle("Choose account type")
                .setItems(mAccountTypeArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String platform = WAConstants.CHANNEL_WA;
                        switch (which) {
                            case 0:
                                platform = WAConstants.CHANNEL_FACEBOOK;
                                break;
                            case 1:
                                platform = WAConstants.CHANNEL_GOOGLE;
                                break;
                            case 2:
                                platform = WAConstants.CHANNEL_VK;
                                break;
                            default:
                                break;
                        }
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
                                Toast.makeText(AccountManagerActivity.this, "Cancel to login with google", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onError(int code, String message, WALoginResult result, Throwable throwable) {
                                cancelLoadingDialog();
                                Toast.makeText(AccountManagerActivity.this, message + "\n"
                                        + (null == throwable ? "" : throwable.getMessage()), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }


    private class BoundAccountAdapter extends BaseAdapter {

        private Context mmContext;
        private List<WAAccount> mmBoundAccounts = new ArrayList<>();

        public BoundAccountAdapter(Context context) {
            this.mmContext = context;
        }

        public void addAll(Collection<WAAccount> accounts, boolean notifyDataSetChanged) {
            if(null == accounts || accounts.isEmpty()) {
                return;
            }
            mmBoundAccounts.addAll(accounts);
            if(notifyDataSetChanged) {
                notifyDataSetChanged();
            }
        }

        public void remove(int position, boolean notifyDataSetChange) {
            if(position < 0) {
                return;
            }
            mmBoundAccounts.remove(position);
            if(notifyDataSetChange) {
                notifyDataSetChanged();
            }
        }

        public void remove(WAAccount account, boolean notifyDataSetChange) {
            if(null == account) {
                return;
            }
            mmBoundAccounts.remove(account);
            if(notifyDataSetChange) {
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
            if(null == convertView) {
                convertView = View.inflate(mmContext, R.layout.item_bound_account, null);
                itemView = new ItemView();
                itemView.account = (TextView) convertView.findViewById(R.id.tv_item_bound_account_id);
                itemView.type = (TextView) convertView.findViewById(R.id.tv_item_bound_account_type);
                itemView.unbind = (Button) convertView.findViewById(R.id.btn_item_bound_account_unbind);
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
