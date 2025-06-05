package com.wa.sdk.demo.deprecation.gifting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.utils.StringUtil;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.utils.WADemoConfig;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.social.WASocialProxy;
import com.wa.sdk.social.model.WAFBGraphObject;
import com.wa.sdk.social.model.WAFBGraphObjectResult;
import com.wa.sdk.wa.common.utils.ImageUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 选择礼物（Demo)
 */
public class FBGiftChooserActivity extends BaseActivity {

    private GiftAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gift_chooser);

        initView();

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void initView() {

        TitleBar titleBar = (TitleBar) findViewById(R.id.tb_gift_chooser);
        titleBar.setTitleText(R.string.gift_chooser);
        titleBar.setLeftButton(android.R.drawable.ic_menu_revert, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        titleBar.setTitleTextColor(R.color.color_white);

        final ListView lv = (ListView) findViewById(R.id.lv_gift_chooser);
        mAdapter = new GiftAdapter(this);
        lv.setAdapter(mAdapter);
        showLoadingDialog(getString(R.string.loading), null);
        WASocialProxy.queryFBGraphObjects(this, "com_ghw_sdk:gift", new WACallback<WAFBGraphObjectResult>() {
            @Override
            public void onSuccess(int code, String message, WAFBGraphObjectResult result) {
                if (null != result) {
                    mAdapter.addAll(result.getObjects(), true);
                }
                cancelLoadingDialog();
            }

            @Override
            public void onCancel() {
                cancelLoadingDialog();
            }

            @Override
            public void onError(int code, String message, WAFBGraphObjectResult result, Throwable throwable) {
                cancelLoadingDialog();
                showShortToast("Error: " + code + "--" + message);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WAFBGraphObject object = mAdapter.getItem(position);
                Intent intent = new Intent();
                intent.putExtra(WADemoConfig.EXTRA_DATA, object);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private class GiftAdapter extends BaseAdapter {

        private Context mmContext;
        private List<WAFBGraphObject> mmDatas = new ArrayList<>();

        public GiftAdapter(Context context) {
            this.mmContext = context;
        }

        public void addAll(Collection<WAFBGraphObject> datas, boolean notifyDataSetChanged) {
            if (null == datas || datas.isEmpty()) {
                return;
            }
            mmDatas.addAll(datas);
            if (notifyDataSetChanged) {
                notifyDataSetChanged();
            }
        }


        public void clear(boolean notifyDataSetChanged) {
            mmDatas.clear();
            if (notifyDataSetChanged) {
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return mmDatas.size();
        }

        @Override
        public WAFBGraphObject getItem(int position) {
            return mmDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ItemView itemView = null;
            if (null == convertView) {
                itemView = new ItemView();
                convertView = View.inflate(mmContext, R.layout.item_gift_chooser, null);
                itemView.icon = (ImageView) convertView.findViewById(R.id.iv_gift_icon);
                itemView.name = (TextView) convertView.findViewById(R.id.tv_gift_name);
                itemView.description = (TextView) convertView.findViewById(R.id.tv_gift_description);
                convertView.setTag(itemView);
            } else {
                itemView = (ItemView) convertView.getTag();
            }

            WAFBGraphObject object = getItem(position);

            String iconUrl = object.getImageUrl();
            if (!StringUtil.isEmpty(iconUrl)) {
//                Picasso.get()
//                        .load(Uri.parse(iconUrl))
//                        .placeholder(R.drawable.ic_launcher)
//                        .error(R.drawable.ic_launcher)
//                        .resize(100, 100)
//                        .into(itemView.icon);

                ImageUtils.loadImage(FBGiftChooserActivity.this, iconUrl, itemView.icon, 100, 100, R.drawable.ic_launcher);
            }
            itemView.name.setText(object.getTitle());
            itemView.description.setText(object.getDescription());

            return convertView;
        }
    }


    private static class ItemView {
        ImageView icon;
        TextView name;
        TextView description;
    }
}
