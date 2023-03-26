package com.tuplv.dforum.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tuplv.dforum.R;
import com.tuplv.dforum.model.Forum;

import java.util.List;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ViewHolder> {
    Context context;
    int layout;
    List<Forum> forums;

    public ForumAdapter(Context context, int layout, List<Forum> forums) {
        this.context = context;
        this.layout = layout;
        this.forums = forums;
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
        Forum forum = forums.get(position);

        holder.tvTitle.setText(forum.getName());
    }

    @Override
    public int getItemCount() {
        if (forums != null)
            return forums.size();

        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTotalView, tvTotalPosts;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTotalView = itemView.findViewById(R.id.tvTotalView);
            tvTotalPosts = itemView.findViewById(R.id.tvTotalPosts);
        }
    }
}
