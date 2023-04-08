package com.tuplv.dforum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tuplv.dforum.R;
import com.tuplv.dforum.interf.OnPostClickListener;
import com.tuplv.dforum.model.Post;

import java.text.SimpleDateFormat;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    Context context;
    int layout;
    List<Post> posts;
    OnPostClickListener listener;

    public PostsAdapter(Context context, int layout, List<Post> posts) {
        this.context = context;
        this.layout = layout;
        this.posts = posts;
    }

    public PostsAdapter(Context context, int layout, List<Post> posts, OnPostClickListener listener) {
        this.context = context;
        this.layout = layout;
        this.posts = posts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layout, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);

        holder.tvTitlePost.setText(post.getTitle());
        holder.tvView.setText(String.valueOf(post.getView()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:ss");
        holder.tvApprovalDate.setText(dateFormat.format(post.getApprovalDate()));

        holder.llItemListPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.goToActivityDetail(post);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (posts != null)
            return posts.size();

        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitlePost, tvView, tvApprovalDate;
        LinearLayout llItemListPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitlePost = itemView.findViewById(R.id.tvTitlePost);
            tvView = itemView.findViewById(R.id.tvView);
            llItemListPost = itemView.findViewById(R.id.llItemListPost);
            tvApprovalDate = itemView.findViewById(R.id.tvApprovalDate);
        }
    }
}
