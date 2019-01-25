package com.wa.sdk.demo.community;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wa.sdk.WAConstants;
import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.WADemoConfig;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.social.WASocialProxy;
import com.wa.sdk.social.model.WAGroup;
import com.wa.sdk.social.model.WAPlace;
import com.wa.sdk.user.WAUserProxy;
import com.wa.sdk.user.model.WAUser;

/**
 * 社区/群组等功能测试界面
 * Created by yinglovezhuzhu@gmail.com on 2016/7/14.
 */
public class VKCommunityActivity extends BaseActivity {

    private static final int RC_SEARCH_AND_CHOOSE = 0x100;

    private ImageView mIvAvatar;
    private TextView mTvName;
    private TextView mTvId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vk_community);

        initView();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_vk_community_get_account_info:
                getAccountInfo();
                break;
            case R.id.btn_vk_community_search_by_id:
                searchById();
                break;
            case R.id.btn_vk_community_app_linked:
                appLinkedGroup();
                break;
            case R.id.btn_vk_community_user_group:
                currentUserGroup();
                break;
            case R.id.btn_vk_community_is_member:
                currentUserIsMemberOfGroup();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!WACommonProxy.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
            if(RC_SEARCH_AND_CHOOSE == requestCode && RESULT_OK == resultCode) {
                if(data.hasExtra(WADemoConfig.EXTRA_DATA)) {
                    final WAGroup group = data.getParcelableExtra(WADemoConfig.EXTRA_DATA);
                    checkCurrentUserIsMemberOfGroup(group);
                }
            }
        }
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.tb_vk_community);
        titleBar.setTitleText(R.string.vk_community);
        titleBar.setLeftButton(android.R.drawable.ic_menu_revert, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.setTitleTextColor(R.color.color_white);

        mIvAvatar = (ImageView) findViewById(R.id.iv_vk_community_avatar);
        mTvName = (TextView) findViewById(R.id.tv_vk_community_name);
        mTvId = (TextView) findViewById(R.id.tv_vk_community_id);
    }

    private void getAccountInfo() {
        showLoadingDialog(getString(R.string.loading), null);
        WAUserProxy.getAccountInfo(this, WAConstants.CHANNEL_VK, new WACallback<WAUser>() {
            @Override
            public void onSuccess(int code, String message, WAUser result) {
                dismissLoadingDialog();
                Picasso.get()
                        .load(result.getPicture())
                        .placeholder(R.drawable.ic_avatar_default)
                        .into(mIvAvatar);
                mTvName.setText(String.format(getString(R.string.name_format), result.getName()));
                mTvId.setText(String.format(getString(R.string.id_format), result.getId()));
            }

            @Override
            public void onCancel() {
                dismissLoadingDialog();
            }

            @Override
            public void onError(int code, String message, WAUser result, Throwable throwable) {
                dismissLoadingDialog();
                mTvName.setText(R.string.error_loading_data);
                mTvId.setText(code + "--" + message);
            }
        });

    }

    private void searchById() {
        Intent intent = new Intent(this, VKCommunityDisplayActivity.class);
        intent.putExtra(WADemoConfig.EXTRA_TYPE, VKCommunityDisplayActivity.TYPE_SEARCH_BY_ID);
        startActivity(intent);
    }

    private void appLinkedGroup() {
        Intent intent = new Intent(this, VKCommunityDisplayActivity.class);
        intent.putExtra(WADemoConfig.EXTRA_TYPE, VKCommunityDisplayActivity.TYPE_APP_LINKED);
        startActivity(intent);
    }

    private void currentUserGroup() {
        Intent intent = new Intent(this, VKCommunityDisplayActivity.class);
        intent.putExtra(WADemoConfig.EXTRA_TYPE, VKCommunityDisplayActivity.TYPE_CURRENT_USER_JOINED);
        startActivity(intent);
    }

    private void currentUserIsMemberOfGroup() {
        Intent intent = new Intent(this, VKCommunityDisplayActivity.class);
        intent.putExtra(WADemoConfig.EXTRA_TYPE, VKCommunityDisplayActivity.TYPE_SEARCH_CHOOSE);
        startActivityForResult(intent, RC_SEARCH_AND_CHOOSE);
    }

    private void checkCurrentUserIsMemberOfGroup(final WAGroup group) {
        showLoadingDialog(getString(R.string.searching), null);
        WASocialProxy.isCurrentUserGroupMember(this, WAConstants.CHANNEL_VK, group.getGid(), null, new WACallback<Boolean>() {
            @Override
            public void onSuccess(int code, String message, Boolean result) {
                dismissLoadingDialog();
                showResultDialog(group, result);
            }

            @Override
            public void onCancel() {
                dismissLoadingDialog();

            }

            @Override
            public void onError(int code, String message, Boolean result, Throwable throwable) {
                dismissLoadingDialog();
                showShortToast(R.string.error_loading_data);
            }
        });
    }

    private void showResultDialog(WAGroup group, boolean isMember) {
        View view = View.inflate(this, R.layout.item_group, null);
        ImageView ivIcon = (ImageView) view.findViewById(R.id.iv_item_group_icon);
        TextView tvName = (TextView) view.findViewById(R.id.tv_item_group_name);
        TextView tvId = (TextView) view.findViewById(R.id.tv_item_group_id);
        TextView tvMemberCount = (TextView) view.findViewById(R.id.tv_item_group_member_count);
        TextView tvPlace = (TextView) view.findViewById(R.id.tv_item_group_place);
        TextView tvDescription = (TextView) view.findViewById(R.id.tv_item_group_description);
        Button btnJoin = (Button) view.findViewById(R.id.btn_item_group_join);
        Picasso.get()
                .load(group.getPhoto_medium())
                .placeholder(R.drawable.ic_launcher)
                .into(ivIcon);
        tvName.setText(group.getName());
        tvId.setText(group.getGid());
        tvMemberCount.setText(String.valueOf(group.getMembers_count()));
        WAPlace city = group.getCity();
        WAPlace country = group.getCountry();
        String place = null == city ? "" : city.getTitle();
        place += null == country ? "" : ((null == city ? "" : ", ") + country.getTitle());
        tvPlace.setText(place);
        tvDescription.setText(group.getDescription());
        btnJoin.setEnabled(false);
        btnJoin.setText(isMember ? R.string.joined : R.string.not_joined);
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(view);
        new AlertDialog.Builder(VKCommunityActivity.this)
                .setTitle(R.string.community)
                .setView(scrollView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
