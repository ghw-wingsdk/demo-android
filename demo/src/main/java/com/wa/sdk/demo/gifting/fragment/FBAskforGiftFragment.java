package com.wa.sdk.demo.gifting.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.wa.sdk.WAConstants;
import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.model.WAResult;
import com.wa.sdk.common.utils.LogUtil;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.base.BaseFragment;
import com.wa.sdk.social.WASocialProxy;
import com.wa.sdk.social.model.WAFBGameRequestData;
import com.wa.sdk.social.model.WAFBGameRequestResult;
import com.wa.sdk.social.model.WAFBGraphObject;
import com.wa.sdk.social.model.WARequestSendResult;
import com.wa.sdk.user.model.WAUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 收到的礼物Fragment
 * 
 */
public class FBAskforGiftFragment extends BaseFragment {

    private static final String TAG = LogUtil.TAG + "GIFTING";

    private ListView mLvRequests;

    private RequestAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_gift_request, container, false);
        initView(contentView);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showLoadingDialog(getString(R.string.loading), null);
        WASocialProxy.fbQueryAskForGiftRequests(getActivity(), new WACallback<WAFBGameRequestResult>() {
            @Override
            public void onSuccess(int code, String message, WAFBGameRequestResult result) {
                cancelLoadingDialog();
                if (null != result) {
                    mAdapter.addAll(result.getRequests(), true);
                }
            }

            @Override
            public void onCancel() {
                cancelLoadingDialog();
            }

            @Override
            public void onError(int code, String message, WAFBGameRequestResult result, Throwable throwable) {
                cancelLoadingDialog();
                showShortToast(message + (null == throwable ? "" : ": excption->" + throwable.getMessage()));
            }
        });
    }

    private void initView(View contentView) {
        mLvRequests = (ListView) contentView.findViewById(R.id.lv_gift_requests);
        mAdapter = new RequestAdapter(getActivity());
        mLvRequests.setAdapter(mAdapter);
    }


    private class RequestAdapter extends BaseAdapter {

        private Context mContext;
        private List<WAFBGameRequestData> mDatas = new ArrayList<>();

        public RequestAdapter(Context context) {
            this.mContext = context;
        }

        public void addAll(Collection<WAFBGameRequestData> datas, boolean notifyDataSetChanged) {
            if(null == datas || datas.isEmpty()) {
                return;
            }
            mDatas.addAll(datas);
            if(notifyDataSetChanged) {
                notifyDataSetChanged();
            }
        }

        public void remove(WAFBGameRequestData data, boolean notifyDataSetChanged) {
            mDatas.remove(data);
            if(notifyDataSetChanged) {
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public WAFBGameRequestData getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            final WAFBGameRequestData data = getItem(position);
            if(null == convertView) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.item_ask_for_gift, null);
                viewHolder.message = (TextView) convertView.findViewById(R.id.tv_message);
                viewHolder.send = (Button) convertView.findViewById(R.id.btn_send);
                viewHolder.deny = (Button) convertView.findViewById(R.id.btn_deny);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final WAFBGraphObject object = data.getObject();
            final WAUser from = data.getFrom();
            viewHolder.message.setText(String.format(getString(R.string.ask_for_gift_msg_format),
                    (null == from ? "unknown" : from.getName()),
                    (null == object ? "unknown" : object.getTitle())));
            viewHolder.send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null == object) {
                        // 数据错误
                        showShortToast(R.string.gift_data_error);
                    } else {
                        List<String> ids = new ArrayList<String>();
                        ids.add(from.getId());
                        WASocialProxy.sendRequest(getActivity(), WAConstants.CHANNEL_FACEBOOK,
                                WAConstants.REQUEST_GIFT_SEND, getString(R.string.send_gift),
                                getString(R.string.send_you_a_gift), object.getId(), ids,
                                new WACallback<WARequestSendResult>() {
                                    @Override
                                    public void onSuccess(int code, String message, WARequestSendResult result) {
                                        // 发送礼物成功，删除请求
                                        final ProgressDialog progressDialog = ProgressDialog.show(mContext, null,
                                                getString(R.string.loading), true, true, new DialogInterface.OnCancelListener() {
                                                    @Override
                                                    public void onCancel(DialogInterface dialog) {
//                                        GhwSdkRequestProxy.cancelDeleteRequest();
                                                    }
                                                });
                                        progressDialog.setCanceledOnTouchOutside(false);
                                        WASocialProxy.fbDeleteRequest(getActivity(), data.getId(), new WACallback<WAResult>() {
                                            @Override
                                            public void onSuccess(int code, String message, WAResult result) {
                                                if (progressDialog.isShowing()) {
                                                    progressDialog.dismiss();
                                                }
                                                mAdapter.remove(data, true);
                                                showShortToast(R.string.gift_send_success);
                                            }

                                            @Override
                                            public void onCancel() {
                                                if (progressDialog.isShowing()) {
                                                    progressDialog.dismiss();
                                                }
                                            }

                                            @Override
                                            public void onError(int code, String message, WAResult result, Throwable throwable) {
                                                if (progressDialog.isShowing()) {
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancel() {

                                    }

                                    @Override
                                    public void onError(int code, String message, WARequestSendResult result, Throwable throwable) {

                                    }
                                }, null);



//                        WASocialProxy.fbSendGift(getActivity(), getString(R.string.send_gift),
//                                getString(R.string.send_you_a_gift), object.getId(), ids,
//                                new WACallback<WAGiftingResult>() {
//                                    @Override
//                                    public void onSuccess(int code, String message, WAGiftingResult result) {
//                                        // 发送礼物成功，删除请求
//                                        final ProgressDialog progressDialog = ProgressDialog.show(mContext, null,
//                                                getString(R.string.loading), true, true, new DialogInterface.OnCancelListener() {
//                                                    @Override
//                                                    public void onCancel(DialogInterface dialog) {
////                                        GhwSdkRequestProxy.cancelDeleteRequest();
//                                                    }
//                                                });
//                                        progressDialog.setCanceledOnTouchOutside(false);
//                                        WASocialProxy.fbDeleteRequest(getActivity(), data.getId(), new WACallback<WAResult>() {
//                                            @Override
//                                            public void onSuccess(int code, String message, WAResult result) {
//                                                if (progressDialog.isShowing()) {
//                                                    progressDialog.dismiss();
//                                                }
//                                                mAdapter.remove(data, true);
//                                                showShortToast(R.string.gift_send_success);
//                                            }
//
//                                            @Override
//                                            public void onCancel() {
//                                                if (progressDialog.isShowing()) {
//                                                    progressDialog.dismiss();
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onError(int code, String message, WAResult result, Throwable throwable) {
//                                                if (progressDialog.isShowing()) {
//                                                    progressDialog.dismiss();
//                                                }
//                                            }
//                                        });
//                                    }
//
//                                    @Override
//                                    public void onCancel() {
//
//                                    }
//
//                                    @Override
//                                    public void onError(int code, String message, WAGiftingResult result, Throwable throwable) {
//
//                                    }
//                                });
                    }
                }
            });
            viewHolder.deny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog progressDialog = ProgressDialog.show(mContext, null, getString(R.string.loading), true, true, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
//                            GhwSdkRequestProxy.cancelDeleteRequest();
                        }
                    });
                    progressDialog.setCanceledOnTouchOutside(false);
                    WASocialProxy.fbDeleteRequest(getActivity(), data.getId(), new WACallback<WAResult>() {
                        @Override
                        public void onSuccess(int code, String message, WAResult result) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            mAdapter.remove(data, true);
                            showShortToast(R.string.gift_send_success);
                        }

                        @Override
                        public void onCancel() {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onError(int code, String message, WAResult result, Throwable throwable) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            });
            return convertView;
        }
    }

    private class ViewHolder {
        TextView message;
        Button send;
        Button deny;
    }
}
