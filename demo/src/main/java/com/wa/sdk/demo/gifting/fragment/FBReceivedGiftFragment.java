package com.wa.sdk.demo.gifting.fragment;

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

import com.wa.sdk.common.model.WACallback;
import com.wa.sdk.common.model.WAResult;
import com.wa.sdk.common.utils.LogUtil;
import com.wa.sdk.demo.R;
import com.wa.sdk.demo.base.BaseFragment;
import com.wa.sdk.social.WASocialProxy;
import com.wa.sdk.social.model.WAFBGameRequestData;
import com.wa.sdk.social.model.WAFBGameRequestResult;
import com.wa.sdk.social.model.WAFBGraphObject;
import com.wa.sdk.user.model.WAUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 收到的礼物Fragment
 * 
 */
public class FBReceivedGiftFragment extends BaseFragment {

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
        WASocialProxy.fbQueryReceivedGifts(getActivity(), new WACallback<WAFBGameRequestResult>() {
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
                showLongToast(message + (null == throwable ? "" : ": excption->" + throwable.getMessage()));
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
            ViewHoder viewHoder = null;
            final WAFBGameRequestData data = getItem(position);
            if(null == convertView) {
                viewHoder = new ViewHoder();
                convertView = View.inflate(mContext, R.layout.item_received_gift, null);
                viewHoder.message = (TextView) convertView.findViewById(R.id.tv_message);
                viewHoder.receive = (Button) convertView.findViewById(R.id.btn_receive);
                convertView.setTag(viewHoder);
            } else {
                viewHoder = (ViewHoder) convertView.getTag();
            }
            WAFBGraphObject object = data.getObject();
            WAUser from = data.getFrom();
            viewHoder.message.setText(String.format(getString(R.string.gift_received_msg_format),
                    (null == from ? "unknown" : from.getName()),
                    (null == object ? "unknown" : object.getTitle())));
            viewHoder.receive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLoadingDialog(getString(R.string.loading) , new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
//                            GhwSdkRequestProxy.cancelDeleteRequest();
                        }
                    });
                    WASocialProxy.fbDeleteRequest(getActivity(), data.getId(), new WACallback<WAResult>() {
                        @Override
                        public void onSuccess(int code, String message, WAResult result) {
                            cancelLoadingDialog();
                            mAdapter.remove(data, true);
                            showShortToast(R.string.gift_send_success);
                        }

                        @Override
                        public void onCancel() {
                            cancelLoadingDialog();
                        }

                        @Override
                        public void onError(int code, String message, WAResult result, Throwable throwable) {
                            cancelLoadingDialog();
                            showShortToast(message + (null == throwable ? "" : ": excption->" + throwable.getMessage()));
                        }
                    });
                }
            });
            return convertView;
        }
    }

    private class ViewHoder {
        TextView message;
        Button receive;
    }
}
