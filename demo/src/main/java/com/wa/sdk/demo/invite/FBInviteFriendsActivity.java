package com.wa.sdk.demo.invite;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.wa.sdk.WAConstants;
import com.wa.sdk.common.WACommonProxy;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.demo.R;
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
public class FBInviteFriendsActivity extends BaseActivity {

    private TitleBar mTitleBar;
//    private EditText mEtInput;
//    private Button mBtnSearch;
    private Button mBtnInvite;

    private ListView mLvFriends;

    private FriendsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_invite_friends);

        initView();

        queryInvitableFriends();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        WACommonProxy.onActivityResult(requestCode, resultCode, data);
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
            Toast.makeText(FBInviteFriendsActivity.this, message + (null == throwable ? "" : throwable.getMessage()), Toast.LENGTH_LONG).show();
        }
    };

    public void queryInvitableFriends() {
        showLoadingDialog("Loading", null);
        // 排除邀请的时间间隔为30分钟
//        WASocialProxy.queryInvitableFriends(this, WAConstants.CHANNEL_FACEBOOK, 60 * 1000 * 30, mFriendsCallback);
        WASocialProxy.queryInvitableFriends(this, WAConstants.CHANNEL_FACEBOOK, 0, mFriendsCallback);
//        WASocialProxy.queryFriends(this, WAConstants.CHANNEL_FACEBOOK, mFriendsCallback);
    }



    public void invite(View view) {
        final List<String> ids = new ArrayList<>();
        final SparseBooleanArray checkedItemPositions = mLvFriends.getCheckedItemPositions();
        for(int i = 0; i < checkedItemPositions.size(); i++) {
            if(checkedItemPositions.valueAt(i)) {
                ids.add(mAdapter.getItem(checkedItemPositions.keyAt(i)).getId());
            }
        }
        // FIXME 测试需要，去掉好友id判空
//        if(ids.isEmpty()) {
//            return;
//        }

        showLoadingDialog(null, null);
        WASocialProxy.sendRequest(this, WAConstants.CHANNEL_FACEBOOK, WAConstants.REQUEST_INVITE,
                "Your friends invite you to join it",
                "This is game is very funning, come and join with me!", null, ids, new WACallback<WARequestSendResult>() {
                    @Override
                    public void onSuccess(int code, String message, WARequestSendResult result) {
                        cancelLoadingDialog();
                        Toast.makeText(FBInviteFriendsActivity.this, "Invite send success", Toast.LENGTH_LONG).show();
                        mLvFriends.clearChoices();
                        List<WAUser> checkedFriends = new ArrayList<WAUser>();
                        for (int i = 0; i < checkedItemPositions.size(); i++) {
                            if (checkedItemPositions.valueAt(i)) {
                                checkedFriends.add(mAdapter.getItem(checkedItemPositions.keyAt(i)));
                            }
                        }
                        mAdapter.removeAll(checkedFriends, true);
                        WASocialProxy.createInviteRecord(WAConstants.CHANNEL_FACEBOOK,
                                result.getRequestId(), result.getRecipients(), null);
                    }

                    @Override
                    public void onCancel() {
                        cancelLoadingDialog();
                        Toast.makeText(FBInviteFriendsActivity.this, "Invite canceled", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(int code, String message, WARequestSendResult result, Throwable throwable) {
                        cancelLoadingDialog();
                        Toast.makeText(FBInviteFriendsActivity.this, "Invite error:" + message, Toast.LENGTH_LONG).show();
                    }
                }, null);
//        WASocialProxy.gameInvite(FBInviteFriendsActivity.this, WAConstants.CHANNEL_FACEBOOK,
//                "Your friends invite you to join it",
//                "This is game is very funning, come and join with me!",
//                ids,
//                new WACallback<WAInviteResult>() {
//                    @Override
//                    public void onSuccess(int code, String message, WAInviteResult result) {
//                        cancelLoadingDialog();
//                        Toast.makeText(FBInviteFriendsActivity.this, "Invite send success", Toast.LENGTH_LONG).show();
//                        mLvFriends.clearChoices();
//                        List<WAUser> checkedFriends = new ArrayList<WAUser>();
//                        for (int i = 0; i < checkedItemPositions.size(); i++) {
//                            if (checkedItemPositions.valueAt(i)) {
//                                checkedFriends.add(mAdapter.getItem(checkedItemPositions.keyAt(i)));
//                            }
//                        }
//                        mAdapter.removeAll(checkedFriends, true);
//                        WASocialProxy.createInviteRecord(FBInviteFriendsActivity.this, WAConstants.CHANNEL_FACEBOOK,
//                                result.getRequestId(), result.getRecipients(), null);
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        cancelLoadingDialog();
//                        Toast.makeText(FBInviteFriendsActivity.this, "Invite canceled", Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onError(int code, String message, WAInviteResult result, Throwable throwable) {
//                        cancelLoadingDialog();
//                        Toast.makeText(FBInviteFriendsActivity.this, "Invite error:" + message, Toast.LENGTH_LONG).show();
//                    }
//                });
    }

    private void initView() {

        mTitleBar = (TitleBar) findViewById(R.id.tb_fb_invite_friends);
        mTitleBar.setTitleText(R.string.fb_invite);
        mTitleBar.setLeftButton(android.R.drawable.ic_menu_revert, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleBar.setTitleTextColor(R.color.color_white);

//        mEtInput = (EditText) findViewById(R.id.et_input);
//        mBtnSearch = (Button) findViewById(R.id.btn_search);
        mBtnInvite = (Button) findViewById(R.id.btn_invite);
        mBtnInvite.setEnabled(false);
        mLvFriends = (ListView) findViewById(R.id.lv_fb_invitable_friends);
        mLvFriends.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        mAdapter = new FriendsAdapter(this);
        mLvFriends.setAdapter(mAdapter);

        // FIXME 测试需要
        mBtnInvite.setEnabled(true);

        mLvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final List<String> ids = new ArrayList<>();
                final SparseBooleanArray checkedItemPositions = mLvFriends.getCheckedItemPositions();
                for (int i = 0; i < checkedItemPositions.size(); i++) {
                    if (checkedItemPositions.valueAt(i)) {
                        ids.add(mAdapter.getItem(checkedItemPositions.keyAt(i)).getId());
                    }
                }
                mBtnInvite.setEnabled(!ids.isEmpty());
            }
        });
    }

    public void search(View view) {
        queryInvitableFriends();
    }

}
