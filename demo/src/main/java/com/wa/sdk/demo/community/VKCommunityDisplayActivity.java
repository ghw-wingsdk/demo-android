package com.wa.sdk.demo.community;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wa.sdk.WAConstants;
import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.model.WAResult;
import com.wa.sdk.common.utils.StringUtil;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.WADemoConfig;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.social.WASocialProxy;
import com.wa.sdk.social.model.WAGroup;
import com.wa.sdk.social.model.WAGroupResult;
import com.wa.sdk.social.model.WAPlace;
import com.wa.sdk.wa.common.utils.ImageUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * VK社区数据展示页面
 * Created by yinglovezhuzhu@gmail.com on 2016/7/14.
 */
public class VKCommunityDisplayActivity extends BaseActivity {

    public static final int TYPE_SEARCH_BY_ID = 1;
    public static final int TYPE_APP_LINKED = 2;
    public static final int TYPE_CURRENT_USER_JOINED = 3;
    public static final int TYPE_SEARCH_CHOOSE = 4;

    private EditText mEtKeyword;
    private TextView mTvTips;
    private GroupAdapter mAdapter;

    private int mType = TYPE_SEARCH_BY_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vk_community_display);

        if(!initData()) {
            showShortToast(R.string.app_error);
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        initView();

        switch (mType) {
            case TYPE_SEARCH_BY_ID:
                break;
            case TYPE_APP_LINKED:
                getAppLinkedGroup();
                break;
            case TYPE_CURRENT_USER_JOINED:
                getCurrentUserGroup();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!WACommonProxy.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.ibtn_vk_community_display_search) {
            switch (mType) {
                case TYPE_SEARCH_BY_ID:
                case TYPE_SEARCH_CHOOSE:
                    searchById();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private boolean initData() {
        Intent intent = getIntent();
        if(!intent.hasExtra(WADemoConfig.EXTRA_TYPE)) {
            return false;
        }
        mType = intent.getIntExtra(WADemoConfig.EXTRA_TYPE, TYPE_SEARCH_BY_ID);
        return true;
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.tb_vk_community_display);
        titleBar.setTitleText(R.string.vk_community);
        titleBar.setLeftButton(android.R.drawable.ic_menu_revert, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        titleBar.setTitleTextColor(R.color.color_white);

        View searchView = findViewById(R.id.ll_vk_community_display_search_view);
        mEtKeyword = (EditText) findViewById(R.id.et_vk_community_display_keyword);
        ListView lvList = (ListView) findViewById(R.id.lv_vk_communities);
        mAdapter = new GroupAdapter(this);
        lvList.setAdapter(mAdapter);
        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final WAGroup group = mAdapter.getItem(position);
                if(TYPE_SEARCH_CHOOSE == mType) {
                    Intent intent = new Intent();
                    intent.putExtra(WADemoConfig.EXTRA_DATA, group);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    WASocialProxy.openGroupPage(VKCommunityDisplayActivity.this, WAConstants.CHANNEL_VK, group.getScreen_name(), null);
                }
            }
        });
        mTvTips = (TextView) findViewById(R.id.tv_vk_community_display_tip);

        switch (mType) {
            case TYPE_SEARCH_BY_ID:
            case TYPE_SEARCH_CHOOSE:
                searchView.setVisibility(View.VISIBLE);
                mEtKeyword.setHint(R.string.vk_community_search_tip);
                mTvTips.setText(R.string.vk_community_search_tip);
                break;
            case TYPE_APP_LINKED:
            case TYPE_CURRENT_USER_JOINED:
                searchView.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private WACallback<WAGroupResult> mCallback = new WACallback<WAGroupResult>() {
        @Override
        public void onSuccess(int code, String message, WAGroupResult result) {
            dismissLoadingDialog();
            Map<String, WAGroup> groups = result.getGroups();
            if(groups.isEmpty()) {
                showShortToast(R.string.vk_community_no_match);
                return;
            }
            mAdapter.clear(false);
            mAdapter.addAll(groups.values(), true);
        }

        @Override
        public void onCancel() {
            dismissLoadingDialog();
            showShortToast("Canceled");
        }

        @Override
        public void onError(int code, String message, WAGroupResult result, Throwable throwable) {
            dismissLoadingDialog();
            String text = "Error!" + "\n"
                    + "code: " + code + "\n"
                    + "message: " + message;
            showShortToast(text);
        }
    };

    /**
     * 根据id搜索
     */
    private void searchById() {
        String ids = mEtKeyword.getText().toString().trim();
        if(StringUtil.isEmpty(ids)) {
            showShortToast(R.string.vk_community_id_null);
            return;
        }
        String [] idArray = ids.split(",");
        showLoadingDialog(getString(R.string.searching), null);
        WASocialProxy.getGroupByGid(this, WAConstants.CHANNEL_VK, idArray, null, mCallback);
    }



    /**
     * App linked group
     */
    private void getAppLinkedGroup() {
        showLoadingDialog(getString(R.string.searching), null);
        WASocialProxy.getCurrentAppLinkedGroup(this, WAConstants.CHANNEL_VK, null, mCallback);
    }

    /**
     * Current user joined group
     */
    private void getCurrentUserGroup() {
        showLoadingDialog(getString(R.string.searching), null);
        WASocialProxy.getCurrentUserGroup(this, WAConstants.CHANNEL_VK, null, mCallback);
    }

    /**
     * List adapter
     */
    private class GroupAdapter extends BaseAdapter {

        private Context mmContext;
        private List<WAGroup> mmDatas = new ArrayList<>();

        public GroupAdapter(Context context) {
            mmContext = context;
        }

        public void clear(boolean notifyDataSetChanged) {
            mmDatas.clear();
            if(notifyDataSetChanged) {
                notifyDataSetChanged();
            }
        }

        public void addAll(Collection<WAGroup> datas, boolean notifyDataSetChanged) {
            if(null == datas || datas.isEmpty()) {
                return;
            }
            mmDatas.addAll(datas);
            if(notifyDataSetChanged) {
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return mmDatas.size();
        }

        @Override
        public WAGroup getItem(int position) {
            return mmDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if(null == convertView) {
                convertView = View.inflate(mmContext, R.layout.item_group, null);
                viewHolder = new ViewHolder();
                viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_item_group_icon);
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_item_group_name);
                viewHolder.tvId = (TextView) convertView.findViewById(R.id.tv_item_group_id);
                viewHolder.tvMemberCount = (TextView) convertView.findViewById(R.id.tv_item_group_member_count);
                viewHolder.tvPlace = (TextView) convertView.findViewById(R.id.tv_item_group_place);
                viewHolder.tvDescription = (TextView) convertView.findViewById(R.id.tv_item_group_description);
                viewHolder.btnJoin = (Button) convertView.findViewById(R.id.btn_item_group_join);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final WAGroup group = getItem(position);
//            Picasso.get()
//                    .load(group.getPhoto_medium())
//                    .placeholder(R.drawable.ic_launcher)
//                    .into(viewHolder.ivIcon);
            ImageUtils.loadImage(VKCommunityDisplayActivity.this,group.getPhoto_medium(),viewHolder.ivIcon,R.drawable.ic_launcher);
            viewHolder.tvName.setText(group.getName());
            viewHolder.tvId.setText(group.getGid());
            viewHolder.tvMemberCount.setText(String.valueOf(group.getMembers_count()));
            WAPlace city = group.getCity();
            WAPlace country = group.getCountry();
            String place = null == city ? "" : city.getTitle();
            place += null == country ? "" : ((null == city ? "" : ", ") + country.getTitle());
            viewHolder.tvPlace.setText(place);
            viewHolder.tvDescription.setText(group.getDescription());
            final Button btnJoin = viewHolder.btnJoin;
            if(TYPE_SEARCH_CHOOSE == mType) {
                btnJoin.setVisibility(View.GONE);
            } else {
                btnJoin.setVisibility(View.VISIBLE);
                if(1 == group.getIs_member()) {
                    btnJoin.setText(R.string.leave);
                    btnJoin.setEnabled(false);
                } else {
                    btnJoin.setText(R.string.join);
                    btnJoin.setEnabled(true);
                    btnJoin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showLoadingDialog(getString(R.string.waiting), null);
                            WASocialProxy.joinGroup(VKCommunityDisplayActivity.this, WAConstants.CHANNEL_VK, group.getGid(), null, new WACallback<WAResult>() {
                                @Override
                                public void onSuccess(int code, String message, WAResult result) {
                                    dismissLoadingDialog();
                                    showShortToast("Join succeed!");
                                    btnJoin.setText(R.string.leave);
                                    btnJoin.setEnabled(false);
                                }

                                @Override
                                public void onCancel() {
                                    dismissLoadingDialog();

                                }

                                @Override
                                public void onError(int code, String message, WAResult result, Throwable throwable) {
                                    dismissLoadingDialog();
                                    showShortToast("Join failed:" + message);
                                }
                            });
                        }
                    });
                }
                if(1 == group.getIs_closed()) {
                    btnJoin.setEnabled(false);
                    btnJoin.setText(R.string.closed);
                }
            }
            return convertView;
        }

        private class ViewHolder {
            ImageView ivIcon;
            TextView tvName;
            TextView tvId;
            TextView tvMemberCount;
            TextView tvPlace;
            TextView tvDescription;
            Button btnJoin;
        }
    }
}
