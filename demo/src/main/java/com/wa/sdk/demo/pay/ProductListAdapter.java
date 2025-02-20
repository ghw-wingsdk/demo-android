package com.wa.sdk.demo.pay;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wa.sdk.demo.R;
import com.wa.sdk.pay.model.WAChannelProduct;
import com.wa.sdk.pay.model.WASkuDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Desc:
 * 
 */
public class ProductListAdapter extends BaseAdapter {


    private Context mContext;
    private List<WASkuDetails> mWaSkuDetailsList = new ArrayList<>();//wa商品列表信息
    private Map<String, WAChannelProduct> mWaSku2WAChannelProductMap = new HashMap<>();//wa商品id->渠道商品信息的映射

    public ProductListAdapter(Context context, List<WASkuDetails> waSkuDetailsList, Map<String, WAChannelProduct> map) {
        this.mContext = context;
        mWaSkuDetailsList = waSkuDetailsList;
        mWaSku2WAChannelProductMap = map;
    }

    @Override
    public int getCount() {
        return mWaSkuDetailsList.size();
    }

    @Override
    public Object getItem(int i) {
        return mWaSkuDetailsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder = null;
        if (view == null) {
            view = View.inflate(mContext, R.layout.payui_item, null);
            viewHolder = new ViewHolder();
            viewHolder.productId = view.findViewById(R.id.tv_productId);
            viewHolder.otherProductInfo = view.findViewById(R.id.tv_productOtherInfo);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        //赋值
        String waSku = mWaSkuDetailsList.get(i).getSku();
        viewHolder.productId.setText(waSku);
        if (!mWaSku2WAChannelProductMap.isEmpty()) {
            WAChannelProduct waChannelProduct = mWaSku2WAChannelProductMap.get(waSku);
            if(waChannelProduct!=null){
                viewHolder.otherProductInfo.setText(waChannelProduct.getPrice() + "  " + waChannelProduct.getPriceCurrencyCode());
            }else {
                viewHolder.otherProductInfo.setText("");
            }
        }

        //监听器
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListenter != null) {
                    mClickListenter.onItemClick(waSku);
                }
            }
        });


        return view;
    }

    class ViewHolder {
        public TextView productId;
        public TextView otherProductInfo;
    }

    private ClickListenter mClickListenter;

    public void setClickListenter(ClickListenter clickListenter) {
        this.mClickListenter = clickListenter;
    }

    public interface ClickListenter {
        void onItemClick(String waProductId);
    }
}
