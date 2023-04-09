package com.tuplv.dforum.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tuplv.dforum.R;
import com.tuplv.dforum.interf.OnPostApproveClickListener;
import com.tuplv.dforum.interf.OnPostClickListener;
import com.tuplv.dforum.model.Post;

import java.text.SimpleDateFormat;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    Context context;
     int layout;
    List<Post> posts;
    OnPostClickListener listener;
    OnPostApproveClickListener onPostApproveClickListener;

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

    public PostsAdapter(Context context, int layout, List<Post> posts, OnPostApproveClickListener onPostApproveClickListener) {
        this.context = context;
        this.layout = layout;
        this.posts = posts;
        this.onPostApproveClickListener = onPostApproveClickListener;
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:ss");
        holder.tvTitlePost.setText(post.getTitle());

        if (layout == R.layout.item_posts) {
            holder.tvView.setText(String.valueOf(post.getView()));
            holder.tvApprovalDate.setText(dateFormat.format(post.getApprovalDate()));
            holder.llItemListPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.goToActivityDetail(post);
                }
            });
        }
        if (layout == R.layout.item_post_approve){
            holder.tvCreateDate.setText(dateFormat.format(post.getCreatedDate()));

            holder.llItemListPostApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   onPostApproveClickListener.goToActivityDetail(post);
                }
            });
            holder.imvPostApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPostApproveClickListener.postApprove(post);
                }
            });
            holder.imvNoPostApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPostApproveClickListener.noPostApprove(post);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (posts != null)
            return posts.size();

        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitlePost, tvView, tvApprovalDate, tvCreateDate;
        LinearLayout llItemListPost, llItemListPostApprove;
        ImageView imvPostApprove, imvNoPostApprove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitlePost = itemView.findViewById(R.id.tvTitlePost);
            if (layout == R.layout.item_posts) {
                tvView = itemView.findViewById(R.id.tvView);
                llItemListPost = itemView.findViewById(R.id.llItemListPost);
                tvApprovalDate = itemView.findViewById(R.id.tvApprovalDate);
            }
            if (layout == R.layout.item_post_approve) {
                tvCreateDate = itemView.findViewById(R.id.tvCreateDate);
                llItemListPostApprove = itemView.findViewById(R.id.llItemListPostApprove);
                imvPostApprove = itemView.findViewById(R.id.imvPostApprove);
                imvNoPostApprove = itemView.findViewById(R.id.imvNoPostApprove);
            }
        }
    }
}