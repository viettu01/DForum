package com.tuplv.dforum.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tuplv.dforum.R;
import com.tuplv.dforum.model.Post;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;

    private int layout;

    private List<Post> posts;

    public PostsAdapter(Context context, int layout, List<Post> posts) {
        this.context = context;
        this.layout = layout;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);

        holder.tvTitlePost.setText(post.getTitle());
        holder.tvView.setText(String.valueOf(post.getView()));
    }

    @Override
    public int getItemCount() {
        if (posts != null) {
            return posts.size();
        }

        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitlePost, tvView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitlePost = itemView.findViewById(R.id.tvTitlePost);
            tvView = itemView.findViewById(R.id.tvView);
        }
    }
}
