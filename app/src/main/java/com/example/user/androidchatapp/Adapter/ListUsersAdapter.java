package com.example.user.androidchatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

/**
 * Created by User on 20/12/2019.
 */

public class ListUsersAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<QBUser> QBUserArrayList;

    public ListUsersAdapter(Context mContext, ArrayList<QBUser> QBUserArrayList) {
        this.mContext = mContext;
        this.QBUserArrayList = QBUserArrayList;
    }

    @Override
    public int getCount() {
        return QBUserArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return QBUserArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_list_item_multiple_choice, null);
            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(QBUserArrayList.get(position).getFullName());
        }
        return view;
    }
}
