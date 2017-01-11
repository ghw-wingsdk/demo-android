package com.wa.sdk.demo.gifting.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.wa.sdk.WAConstants;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.WADemoConfig;
import com.wa.sdk.demo.base.BaseFragment;
import com.wa.sdk.demo.gifting.FBGiftChooserActivity;
import com.wa.sdk.social.WASocialProxy;
import com.wa.sdk.social.model.WAFBGraphObject;
import com.wa.sdk.social.model.WAFriendsResult;
import com.wa.sdk.social.model.WAGiftingResult;
import com.wa.sdk.social.model.WARequestSendResult;
import com.wa.sdk.user.model.WAUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 赠送/索要礼物Fragment
 * Created by ghw_zhangyy on 2015/7/16.
 */
public class FBGiftingFragment extends BaseFragment {

    private Button mBtnChooseGift;
    private EditText mEtInput;
    private Button mBtnSearch;
    private Button mBtnSendGift;
    private Button mBtnAskForGift;

    private ListView mLvFriends;

    private FriendsAdapter mAdapter;

    private WAFBGraphObject mObject = null;

    private WACallback<WAFriendsResult> mFriendsCallback = new WACallback<WAFriendsResult>() {
        @Override
        public void onSuccess(int code, String message, WAFriendsResult result) {
            if(null != result) {
                mBtnSearch.setEnabled(false);
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
            Toast.makeText(getActivity(), message + (null == throwable ? "" : throwable.getMessage()), Toast.LENGTH_LONG).show();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_gifting, container, false);
        initView(contentView);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        queryFriends();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(Activity.RESULT_OK == resultCode && 100 == requestCode) {
            mObject = data.getParcelableExtra(WADemoConfig.EXTRA_DATA);
            if(null != mObject) {
                mBtnChooseGift.setText(String.format("%s \n %s", mObject.getTitle(), mObject.getDescription()));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void queryFriends() {
        showLoadingDialog(getString(R.string.loading), null);
        WASocialProxy.queryFriends(getActivity(), WAConstants.CHANNEL_FACEBOOK, mFriendsCallback);
    }

    private void initView(View contentView) {
        mBtnChooseGift = (Button) contentView.findViewById(R.id.btn_choose_gift);
        if(null != mObject) {
            mBtnChooseGift.setText(String.format("%s \n %s", mObject.getTitle(), mObject.getDescription()));
        }
        mEtInput = (EditText) contentView.findViewById(R.id.et_input);
        mBtnSearch = (Button) contentView.findViewById(R.id.btn_search);
        mBtnSendGift = (Button) contentView.findViewById(R.id.btn_send_gift);
        mBtnAskForGift = (Button) contentView.findViewById(R.id.btn_ask_for_gift);
        mLvFriends = (ListView) contentView.findViewById(R.id.lv_fb_invitable_friends);
        mLvFriends.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        mAdapter = new FriendsAdapter(getActivity());
        mLvFriends.setAdapter(mAdapter);
        // FIXME 测试需要
        mBtnSendGift.setEnabled(true);
        mBtnAskForGift.setEnabled(true);

        mBtnChooseGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), FBGiftChooserActivity.class), 100);
            }
        });

        mLvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mBtnSendGift.setEnabled(mLvFriends.getCheckedItemCount() > 0);
                mBtnAskForGift.setEnabled(mLvFriends.getCheckedItemCount() > 0);
            }
        });
        mBtnSendGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FIXME 测试需要，注释掉好友id判空
//                if (mLvFriends.getCheckedItemCount() < 1) {
//                    return;
//                }
                if(null == mObject) {
                    Toast.makeText(getActivity(), getString(R.string.choose_gift_first), Toast.LENGTH_LONG).show();
                    return;
                }
                final List<String> ids = new ArrayList<>();
                SparseBooleanArray checkedItemPositions = mLvFriends.getCheckedItemPositions();
                for (int i = 0; i < checkedItemPositions.size(); i++) {
                    if (checkedItemPositions.valueAt(i)) {
                        ids.add(mAdapter.getItem(checkedItemPositions.keyAt(i)).getId());
                    }
                }

//                WASocialProxy.fbSendGift(getActivity(),
//                        getString(R.string.send_gift),
//                        getString(R.string.send_you_a_gift),
//                        mObject.getId(),
//                        ids,
//                        new WACallback<WAGiftingResult>() {
//                            @Override
//                            public void onSuccess(int code, String message, WAGiftingResult result) {
//                                showLongToast(R.string.gift_send_success);
//                                mLvFriends.clearChoices();
//                                mAdapter.notifyDataSetChanged();
//                                mBtnSendGift.setEnabled(false);
//                                mBtnAskForGift.setEnabled(false);
//                            }
//
//                            @Override
//                            public void onCancel() {
//                                showLongToast(R.string.gift_send_cancel);
//                                mLvFriends.clearChoices();
//                                mAdapter.notifyDataSetChanged();
//                                mBtnSendGift.setEnabled(false);
//                                mBtnAskForGift.setEnabled(false);
//                            }
//
//                            @Override
//                            public void onError(int code, String message, WAGiftingResult result, Throwable throwable) {
//                                showLongToast(getString(R.string.gift_send_error) + ":" + message);
//                            }
//                        });
                WASocialProxy.sendRequest(getActivity(), WAConstants.CHANNEL_FACEBOOK,
                        WAConstants.REQUEST_GIFT_SEND,
                        getString(R.string.send_gift),
                        getString(R.string.send_you_a_gift),
                        mObject.getId(),
                        ids, new WACallback<WARequestSendResult>() {
                            @Override
                            public void onSuccess(int code, String message, WARequestSendResult result) {
                                showLongToast(R.string.gift_send_success);
                                mLvFriends.clearChoices();
                                mAdapter.notifyDataSetChanged();
                                mBtnSendGift.setEnabled(false);
                                mBtnAskForGift.setEnabled(false);
                            }

                            @Override
                            public void onCancel() {
                                showLongToast(R.string.gift_send_cancel);
                                mLvFriends.clearChoices();
                                mAdapter.notifyDataSetChanged();
                                mBtnSendGift.setEnabled(false);
                                mBtnAskForGift.setEnabled(false);
                            }

                            @Override
                            public void onError(int code, String message, WARequestSendResult result, Throwable throwable) {
                                showLongToast(getString(R.string.gift_send_error) + ":" + message);
                            }
                        }, null);

            }
        });

        mBtnAskForGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FIXME 测试需要，注释掉好友id判空
//                if(mLvFriends.getCheckedItemCount() < 1) {
//                    return;
//                }
                if(null == mObject) {
                    showShortToast(R.string.choose_gift_first);
                    return;
                }
                final List<String> ids = new ArrayList<>();
                SparseBooleanArray checkedItemPositions = mLvFriends.getCheckedItemPositions();
                for (int i = 0; i < checkedItemPositions.size(); i++) {
                    if (checkedItemPositions.valueAt(i)) {
                        ids.add(mAdapter.getItem(checkedItemPositions.keyAt(i)).getId());
                    }
                }

//                WASocialProxy.fbAskForGift(getActivity(),
//                        getString(R.string.ask_for_gift),
//                        getString(R.string.send_me_a_gift),
//                        mObject.getId(),
//                        ids,
//                        new WACallback<WAGiftingResult>() {
//                            @Override
//                            public void onSuccess(int code, String message, WAGiftingResult result) {
//                                showLongToast(R.string.ask_for_gift_success);
//                                mLvFriends.clearChoices();
//                                mAdapter.notifyDataSetChanged();
//                                mBtnSendGift.setEnabled(false);
//                                mBtnAskForGift.setEnabled(false);
//                            }
//
//                            @Override
//                            public void onCancel() {
//                                showLongToast(R.string.ask_for_gift_cancel);
//                                mLvFriends.clearChoices();
//                                mAdapter.notifyDataSetChanged();
//                                mBtnSendGift.setEnabled(false);
//                                mBtnAskForGift.setEnabled(false);
//                            }
//
//                            @Override
//                            public void onError(int code, String message, WAGiftingResult result, Throwable throwable) {
//                                showLongToast(getString(R.string.ask_for_gift_error) + ":" + message);
//                            }
//                        });
                WASocialProxy.sendRequest(getActivity(), WAConstants.CHANNEL_FACEBOOK,
                        WAConstants.REQUEST_GIFT_ASK,
                        getString(R.string.ask_for_gift),
                        getString(R.string.send_me_a_gift),
                        mObject.getId(),
                        ids, new WACallback<WARequestSendResult>() {
                            @Override
                            public void onSuccess(int code, String message, WARequestSendResult result) {
                                showLongToast(R.string.ask_for_gift_success);
                                mLvFriends.clearChoices();
                                mAdapter.notifyDataSetChanged();
                                mBtnSendGift.setEnabled(false);
                                mBtnAskForGift.setEnabled(false);
                            }

                            @Override
                            public void onCancel() {
                                showLongToast(R.string.ask_for_gift_cancel);
                                mLvFriends.clearChoices();
                                mAdapter.notifyDataSetChanged();
                                mBtnSendGift.setEnabled(false);
                                mBtnAskForGift.setEnabled(false);
                            }

                            @Override
                            public void onError(int code, String message, WARequestSendResult result, Throwable throwable) {
                                showLongToast(getString(R.string.ask_for_gift_error) + ":" + message);
                            }
                        }, null);

            }
        });
    }

    private class FriendsAdapter extends BaseAdapter {

        private Context mmContext;
        private List<WAUser> mmDatas = new ArrayList<>();

        public FriendsAdapter(Context context) {
            this.mmContext = context;
        }

        public void addAll(Collection<WAUser> friends, boolean notifyDataSetChanged) {
            if(null == friends || friends.isEmpty()) {
                return;
            }
            mmDatas.addAll(friends);
            if(notifyDataSetChanged) {
                notifyDataSetChanged();
            }
        }

        public void removeAll(Collection<WAUser> friends, boolean notifyDataSetChanged) {
            if(null == friends || friends.isEmpty()) {
                return;
            }
            mmDatas.removeAll(friends);
            if(notifyDataSetChanged) {
                notifyDataSetChanged();
            }
        }

        public void clear(boolean notifyDataSetChanged) {
            mmDatas.clear();
            if(notifyDataSetChanged) {
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return mmDatas.size();
        }

        @Override
        public WAUser getItem(int position) {
            return mmDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CheckedTextView checkedTextView = null;
            if(null == convertView) {
                checkedTextView = (CheckedTextView) View.inflate(mmContext, android.R.layout.simple_list_item_multiple_choice, null);
                checkedTextView.setTextColor(Color.BLACK);
                checkedTextView.setPadding(5, 10, 5, 10);
            } else {
                checkedTextView = (CheckedTextView) convertView;
            }
            WAUser friend = getItem(position);
            String text = friend.getName() + "\npUserId:" + friend.getId() + "\nghwUserId:" + friend.getGhwUserId();
            checkedTextView.setText(text);
            return checkedTextView;
        }
    }
}
