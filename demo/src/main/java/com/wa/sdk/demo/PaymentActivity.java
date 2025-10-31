package com.wa.sdk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.demo.adapter.ProductListAdapter;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.base.FlavorApiHelper;
import com.wa.sdk.pay.WAPayProxy;
import com.wa.sdk.pay.model.WAChannelProduct;
import com.wa.sdk.pay.model.WAPurchaseResult;
import com.wa.sdk.pay.model.WASkuDetails;
import com.wa.sdk.pay.model.WASkuResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支付
 */
public class PaymentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setTitleBar(R.string.payment);

        // 查询 SDK 商品
        querySdkInventory();
    }

    /**
     * 查询 SDK 商品
     */
    private void querySdkInventory() {
        showLoadingDialog("正在查询商品....", null);
        // 查询 SDK 后台商品
        WAPayProxy.queryInventory(new WACallback<WASkuResult>() {
            @Override
            public void onSuccess(int code, String message, WASkuResult result) {
                List<WASkuDetails> waSkuDetailsList = result.getSkuList();
                // 查询渠道后台商品，比如 Google 后台配置的商品（不建议直接使用渠道后台商品）
                WAPayProxy.queryChannelProduct(FlavorApiHelper.getQueryProductChannel(), new WACallback<Map<String, WAChannelProduct>>() {
                    @Override
                    public void onSuccess(int code, String message, Map<String, WAChannelProduct> map) {
                        setAdapter(waSkuDetailsList, map);
                        cancelLoadingDialog();
                        showLongToast("渠道商品查询成功");
                    }

                    @Override
                    public void onCancel() {
                        setAdapter(waSkuDetailsList, new HashMap<>());
                        cancelLoadingDialog();
                    }

                    @Override
                    public void onError(int code, String message, Map<String, WAChannelProduct> result, Throwable throwable) {
                        setAdapter(waSkuDetailsList, new HashMap<>());
                        cancelLoadingDialog();
                        showLongToast("渠道商品查询失败：" + code + " , " + message);
                    }

                    private void setAdapter(List<WASkuDetails> waSkuDetailsList, Map<String, WAChannelProduct> channelProductMap) {
                        if (waSkuDetailsList != null && !waSkuDetailsList.isEmpty()) {//存在商品
                            ProductListAdapter productListAdapter = new ProductListAdapter(PaymentActivity.this, waSkuDetailsList, channelProductMap);
                            ListView listView = findViewById(R.id.lv_payment_sku);
                            listView.setAdapter(productListAdapter);
                            productListAdapter.setClickListenter(sdkProductId -> payUI(sdkProductId, "CpOrderId:12345"));
                        } else {
                            showLongToast("未找到 SDK 商品");
                        }
                    }
                });
            }

            @Override
            public void onCancel() {
                cancelLoadingDialog();
                showLongToast("取消查询商品");
            }

            @Override
            public void onError(int code, String message, WASkuResult result, Throwable throwable) {
                cancelLoadingDialog();
                showLongToast("查询商品失败：" + code + " , " + message);
            }
        });
    }

    /**
     * 发起支付
     *
     * @param sdkProductId SDK商品ID
     * @param extInfo      透传参数，该信息会在支付成功后原样通知到 CP服务器，供 CP 用于检验
     */
    private void payUI(String sdkProductId, String extInfo) {
        if (!WAPayProxy.isPayServiceAvailable(this)) {
            showShortToast("Pay service not available");
            return;
        }

        WAPayProxy.payUI(this, sdkProductId, extInfo, new WACallback<WAPurchaseResult>() {
            @Override
            public void onSuccess(int code, String message, WAPurchaseResult result) {
                cancelLoadingDialog();
                logD("Payment Success:\n" + result);
                showLongToast("Payment is successful. ProductId:" + result.getWAProductId() + " , ExtInfo:" + result.getExtInfo());
            }

            @Override
            public void onCancel() {
                cancelLoadingDialog();
                logD("Payment Cancel");
                showLongToast("Payment has been cancelled.");
            }

            @Override
            public void onError(int code, String message, WAPurchaseResult result, Throwable throwable) {
                cancelLoadingDialog();
                if (WACallback.CODE_NOT_LOGIN == code) {
                    showLoginTips();
                }

                String text = "Payment failed, code:" + code + ", msg:" + message;
                showLongToast(text);
                logE(text);
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
