package com.tuplv.dforum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tuplv.dforum.R;

import java.util.List;

public class CategorySpinnerAdapter extends BaseAdapter {

    List<String> categories;
    Context context;

    public CategorySpinnerAdapter(Context context, List<String> categories) {
        this.categories = categories;
        this.context = context;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int i) {
        return categories.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_spinner, viewGroup, false);

        String categoryName = categories.get(i);

        TextView tvSpinnerName = view.findViewById(R.id.tvSpinnerName);
        tvSpinnerName.setText(categoryName);

        return view;
    }
}
