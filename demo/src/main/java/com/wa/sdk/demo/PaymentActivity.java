package com.wa.sdk.demo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wa.sdk.WAConstants;
import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.model.WAResult;
import com.wa.sdk.common.utils.LogUtil;
import com.wa.sdk.common.utils.StringUtil;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.pay.ProductListAdapter;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.pay.WAPayProxy;
import com.wa.sdk.pay.model.WAChannelProduct;
import com.wa.sdk.pay.model.WAPurchaseResult;
import com.wa.sdk.pay.model.WASkuDetails;
import com.wa.sdk.pay.model.WASkuResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网页支付页面
 * Created by ghw on 16/5/8.
 */
public class PaymentActivity extends BaseActivity {

    private Context mContext;

    private final String TAG = "PaymentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.activity_payment);

        TitleBar titleBar = (TitleBar) findViewById(R.id.tb_payment);
        titleBar.setTitleText(R.string.payment);
        titleBar.setLeftButton(android.R.drawable.ic_menu_revert, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.setTitleTextColor(R.color.color_white);

        WAPayProxy.initialize(this, new WACallback<WAResult>() {

            @Override
            public void onSuccess(int code, String message, WAResult result) {
                LogUtil.d(TAG, "WAPayProxy.initialize success");
                showLongToast("PayUIActitivy:Payment is successful.");
            }

            @Override
            public void onCancel() {
                LogUtil.d(TAG, "PayUIActitivy:WAPayProxy.initialize has been cancelled.");
            }

            @Override
            public void onError(int code, String message, WAResult result, Throwable throwable) {
                LogUtil.d(TAG, "WAPayProxy.initialize error");
                showLongToast("PayUIActitivy:Payment initialization fail.");
            }
        });

        showLoadingDialog("正在查询库存....", null);
        WAPayProxy.queryInventory(new WACallback<WASkuResult>() {
            @Override
            public void onSuccess(int code, String message, WASkuResult result) {
                List<WASkuDetails> waSkuDetailsList= result.getSkuList();
                WAPayProxy.queryChannelProduct(WAConstants.CHANNEL_GOOGLE, new WACallback<Map<String, WAChannelProduct>>() {
                    @Override
                    public void onSuccess(int code, String message, Map<String, WAChannelProduct> map) {
                        if (waSkuDetailsList.size() > 0) {//存在商品
                            ProductListAdapter productListAdapter=new ProductListAdapter(PaymentActivity.this,waSkuDetailsList,map);
                            ListView listView = (ListView) findViewById(R.id.lv_payment_sku);
                            listView.setAdapter(productListAdapter);
                            productListAdapter.setClickListenter(new ProductListAdapter.ClickListenter() {
                                @Override
                                public void onItemClick(String waSku) {
                                    payUI(waSku, "extInfotest");
                                }
                            });

                        }

                        cancelLoadingDialog();
                        showLongToast("Query inventory is successful");
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(int code, String message, Map<String, WAChannelProduct> result, Throwable throwable) {
                        LogUtil.d("WAChannelProduct", "error:"+message);
                    }
                });


            }

            @Override
            public void onCancel() {
                cancelLoadingDialog();
                showLongToast("Query inventory has been cancelled");
            }

            @Override
            public void onError(int code, String message, WASkuResult result, Throwable throwable) {
                cancelLoadingDialog();
                showLongToast("Query inventory fail, please try again later");
            }
        });

    }

    private void payUI(String waProductId, String extInfo) {
        if (!WAPayProxy.isPayServiceAvailable(this)) {
            showShortToast("Pay service not available");
            return;
        }
//        showLoadingDialog("支付中...", null);
        WAPayProxy.payUI(this, waProductId, extInfo, new WACallback<WAPurchaseResult>() {
            @Override
            public void onSuccess(int code, String message, WAPurchaseResult result) {
                LogUtil.d(TAG, "pay success");
                LogUtil.d(TAG, result.toString());
                cancelLoadingDialog();
                showLongToast("Payment is successful.");
            }

            @Override
            public void onCancel() {
                LogUtil.d(TAG, "pay cancel");
                cancelLoadingDialog();
                showLongToast("Payment has been cancelled.");
            }

            @Override
            public void onError(int code, String message, WAPurchaseResult result, Throwable throwable) {
                LogUtil.d(TAG, "pay error");
                cancelLoadingDialog();
                if (WACallback.CODE_NOT_LOGIN == code) {
                    new AlertDialog.Builder(PaymentActivity.this)
                            .setTitle(R.string.warming)
                            .setMessage(R.string.not_login_yet)
                            .setPositiveButton(R.string.login_now, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(PaymentActivity.this, LoginActivity.class);
                                    intent.putExtra("auto_finish", true);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                }
                showLongToast(StringUtil.isEmpty(message) ? "Billing service is not available at this moment." : message);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelLoadingDialog();
        WAPayProxy.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (WACommonProxy.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
