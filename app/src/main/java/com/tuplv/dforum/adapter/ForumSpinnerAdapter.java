package com.tuplv.dforum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tuplv.dforum.R;
import com.tuplv.dforum.model.Forum;

import java.util.List;

public class ForumSpinnerAdapter extends BaseAdapter {

    List<Forum> forums;
    Context context;

    public ForumSpinnerAdapter(Context context, List<Forum> forums) {
        this.forums = forums;
        this.context = context;
    }

    @Override
    public int getCount() {
        return forums.size();
    }

    @Override
    public Object getItem(int i) {
        return forums.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_spinner, viewGroup, false);

        Forum forum = forums.get(i);

        TextView tvNameForum = view.findViewById(R.id.tvSpinnerName);
        tvNameForum.setText(forum.getName());

        return view;
    }
}
