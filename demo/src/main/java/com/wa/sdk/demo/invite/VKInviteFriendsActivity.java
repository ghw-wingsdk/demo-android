package com.wa.sdk.demo.invite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.wa.sdk.WAConstants;
import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.WASharedPrefHelper;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.WADemoConfig;
import com.wa.sdk.demo.base.BaseActivity;
import com.wa.sdk.demo.invite.adapter.FriendsAdapter;
import com.wa.sdk.demo.widget.TitleBar;
import com.wa.sdk.social.WASocialProxy;
import com.wa.sdk.social.model.WAFriendsResult;
import com.wa.sdk.social.model.WARequestSendResult;
import com.wa.sdk.user.model.WAUser;

import java.util.ArrayList;
import java.util.List;

/**
 * 给好友发送邀请
 * Created by yinglovezhuzhu@gmail.com on 2016/3/23.
 */
public class VKInviteFriendsActivity extends BaseActivity {

    private TitleBar mTitleBar;
//    private EditText mEtInput;
//    private ImageButton mBtnSearch;
    private Button mBtnInvite;

    private ListView mLvFriends;

    private FriendsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vk_invite_friends);

        initView();

        queryInvitableFriends();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!WACommonProxy.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private WACallback<WAFriendsResult> mFriendsCallback = new WACallback<WAFriendsResult>() {
        @Override
        public void onSuccess(int code, String message, WAFriendsResult result) {
            if(null != result) {
//                mBtnSearch.setEnabled(false);
                mAdapter.addAll(result.getFriends(), true);
            }
            cancelLoadingDialog();
        }

        @Override
        public void onCancel() {
            cancelLoadingDialog();
        }

        @Override
        public void onError(int code, String message, WAFriendsResult result, Throwable throwable) {
            cancelLoadingDialog();
            Toast.makeText(VKInviteFriendsActivity.this, message + (null == throwable ? "" : throwable.getMessage()), Toast.LENGTH_LONG).show();
        }
    };

    public void queryInvitableFriends() {
        showLoadingDialog("Loading", null);
        // 排除邀请的时间间隔为30分钟
//        WASocialProxy.queryInvitableFriends(this, WAConstants.CHANNEL_VK, 0, mFriendsCallback);
        WASocialProxy.queryFriends(this, WAConstants.CHANNEL_VK, mFriendsCallback);
    }



    public void invite(View view) {
        final int checkedPosition = mLvFriends.getCheckedItemPosition();
        if(ListView.INVALID_POSITION == checkedPosition) {
            return;
        }
        WAUser user = mAdapter.getItem(checkedPosition);
        if(null == user) {
            return;
        }

        List<String> ids = new ArrayList<>();
        ids.add(user.getId());
        WASocialProxy.sendRequest(this, WAConstants.CHANNEL_VK, WAConstants.REQUEST_REQUEST,
                "Help", "Help me", "", ids, new WACallback<WARequestSendResult>() {
            @Override
            public void onSuccess(int code, String message, WARequestSendResult result) {
                showShortToast("Request send succeed: requestId--" + result.getRequestId());
                mLvFriends.clearChoices();
                WASocialProxy.createInviteRecord(WAConstants.CHANNEL_VK,
                        result.getRequestId(), result.getRecipients(), null);
            }

            @Override
            public void onCancel() {
                showShortToast("Request send canceled");
            }

            @Override
            public void onError(int code, String message, WARequestSendResult result, Throwable throwable) {
                showShortToast("Request send failed: \ncode: " + code + "\nmessage: " + message);
            }
        }, null);
    }

    private void initView() {

        mTitleBar = (TitleBar) findViewById(R.id.tb_vk_invite_friends);
        mTitleBar.setTitleText(R.string.vk_invite);
        mTitleBar.setLeftButton(android.R.drawable.ic_menu_revert, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleBar.setTitleTextColor(R.color.color_white);

//        mEtInput = (EditText) findViewById(R.id.et_vk_invite_input);
//        mBtnSearch = (ImageButton) findViewById(R.id.btn_vk_invite_search);
        mBtnInvite = (Button) findViewById(R.id.btn_vk_invite);
        mBtnInvite.setEnabled(false);
        mLvFriends = (ListView) findViewById(R.id.lv_vk_invitable_friends);
        mLvFriends.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mAdapter = new FriendsAdapter(this);
        mLvFriends.setAdapter(mAdapter);

        mLvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int checkedPosition = mLvFriends.getCheckedItemPosition();
                mBtnInvite.setEnabled(ListView.INVALID_POSITION != checkedPosition);
            }
        });
    }

    public void search(View view) {
        queryInvitableFriends();
    }

}
