package com.wa.sdk.demo.deprecation.invite.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.wa.sdk.user.model.WAUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class FriendsAdapter extends BaseAdapter {

    private Context mContext;
    private List<WAUser> mDatas = new ArrayList<>();

    public FriendsAdapter(Context context) {
        this.mContext = context;
    }

    public void addAll(Collection<WAUser> friends, boolean notifyDataSetChanged) {
        if (null == friends || friends.isEmpty()) {
            return;
        }
        mDatas.addAll(friends);
        if (notifyDataSetChanged) {
            notifyDataSetChanged();
        }
    }

    public void removeAll(Collection<WAUser> friends, boolean notifyDataSetChanged) {
        if (null == friends || friends.isEmpty()) {
            return;
        }
        mDatas.removeAll(friends);
        if (notifyDataSetChanged) {
            notifyDataSetChanged();
        }
    }

    public void clear(boolean notifyDataSetChanged) {
        mDatas.clear();
        if (notifyDataSetChanged) {
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public WAUser getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CheckedTextView checkedTextView = null;
        if (null == convertView) {
            checkedTextView = (CheckedTextView) View.inflate(mContext, android.R.layout.simple_list_item_multiple_choice, null);
            checkedTextView.setTextColor(Color.BLACK);
            checkedTextView.setPadding(20, 20, 20, 20);
            checkedTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18.0f);
        } else {
            checkedTextView = (CheckedTextView) convertView;
        }
        WAUser friend = getItem(position);
        if (null == friend) {
            checkedTextView.setText("no name");
        } else {
            checkedTextView.setText(friend.getName());
        }
        return checkedTextView;
    }
}
