package com.wa.sdk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.utils.LogUtil;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.base.FlavorApiHelper;
import com.wa.sdk.demo.pay.ProductListAdapter;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.pay.WAPayProxy;
import com.wa.sdk.pay.model.WAChannelProduct;
import com.wa.sdk.pay.model.WAPurchaseResult;
import com.wa.sdk.pay.model.WASkuDetails;
import com.wa.sdk.pay.model.WASkuResult;

import java.util.List;
import java.util.Map;

/**
 * 网页支付页面
 *
 */
public class PaymentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment);

        TitleBar titleBar = findViewById(R.id.tb_payment);
        titleBar.setTitleText(R.string.payment);
        titleBar.setLeftButton(android.R.drawable.ic_menu_revert, v -> finish());
        titleBar.setTitleTextColor(R.color.color_white);

        showLoadingDialog("正在查询库存....", null);
        WAPayProxy.queryInventory(new WACallback<WASkuResult>() {
            @Override
            public void onSuccess(int code, String message, WASkuResult result) {
                List<WASkuDetails> waSkuDetailsList = result.getSkuList();
                if (waSkuDetailsList != null)
                    for (WASkuDetails waSkuDetails : waSkuDetailsList) {
                        LogUtil.d(TAG, "Inventory, sku:" + waSkuDetails.getSku() + ", title: " + waSkuDetails.getTitle() + ", price: " + waSkuDetails.getVirtualCurrency());
                    }
                WAPayProxy.queryChannelProduct(FlavorApiHelper.getQueryProductChannel(), new WACallback<Map<String, WAChannelProduct>>() {
                    @Override
                    public void onSuccess(int code, String message, Map<String, WAChannelProduct> map) {
                        if (waSkuDetailsList != null && !waSkuDetailsList.isEmpty()) {//存在商品
                            ProductListAdapter productListAdapter = new ProductListAdapter(PaymentActivity.this, waSkuDetailsList, map);
                            ListView listView = findViewById(R.id.lv_payment_sku);
                            listView.setAdapter(productListAdapter);
                            productListAdapter.setClickListenter(waSku -> payUI(waSku, "extInfo_test"));

                        }

                        cancelLoadingDialog();
                        showLongToast("Query inventory is successful");
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(int code, String message, Map<String, WAChannelProduct> result, Throwable throwable) {
                        LogUtil.d("WAChannelProduct", "error:" + message);
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
                showLongToast("Payment is successful. ProductId:" + result.getWAProductId() + " , ExtInfo:" + result.getExtInfo());
            }

            @Override
            public void onCancel() {
                LogUtil.d(TAG, "pay cancel");
                cancelLoadingDialog();
                showLongToast("Payment has been cancelled.");
            }

            @Override
            public void onError(int code, String message, WAPurchaseResult result, Throwable throwable) {
                cancelLoadingDialog();
                if (WACallback.CODE_NOT_LOGIN == code) {
                    showLoginTips();
                }
                showLongToast("pay error:" + code + " - " + message);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelLoadingDialog();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (WACommonProxy.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
