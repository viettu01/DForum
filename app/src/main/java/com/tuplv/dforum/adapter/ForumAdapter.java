package com.tuplv.dforum.adapter;

import static android.content.Context.MODE_PRIVATE;
import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.ROLE_ADMIN;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;
import static com.tuplv.dforum.until.Until.formatNumber;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tuplv.dforum.R;
import com.tuplv.dforum.interf.OnForumClickListener;
import com.tuplv.dforum.model.Forum;
import com.tuplv.dforum.model.Post;

import java.util.List;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ViewHolder> {
    Context context;
    int layout;
    List<Forum> forums;
    OnForumClickListener listener;
    SharedPreferences sharedPreferences;

    public ForumAdapter(Context context, int layout, List<Forum> forums, OnForumClickListener listener) {
        this.context = context;
        this.layout = layout;
        this.forums = forums;
        this.listener = listener;
        sharedPreferences = context.getSharedPreferences("account", MODE_PRIVATE);
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

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        Query query = mDatabase.child(OBJ_POST).orderByChild("status").equalTo(STATUS_ENABLE);

        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long postCount = 0;
                long postAllCount = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null && post.getForumId() == forum.getForumId()) {
                        postCount++;
                    }
                    postAllCount++;
                }
                if (forum.getForumId() == 0)
                    holder.tvTotalPost.setText(formatNumber(postAllCount) + " bài viết");
                else
                    holder.tvTotalPost.setText(formatNumber(postCount) + " bài viết");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.goToListPostOfForum(forum);
            }
        };
        holder.llItemListForum.setOnClickListener(onClickListener);
        holder.imgShowForum.setOnClickListener(onClickListener);

        if (sharedPreferences.getString("role", "").equals(ROLE_ADMIN)) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showPopupMenu(holder, forum);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (forums != null)
            return forums.size();
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llItemListForum;
        TextView tvTitle, tvTotalPost;
        ImageView imgShowForum;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llItemListForum = itemView.findViewById(R.id.llItemListForum);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTotalPost = itemView.findViewById(R.id.tvTotalPost);
            imgShowForum = itemView.findViewById(R.id.imvShowForum);
        }
    }

    @SuppressLint("RestrictedApi, NonConstantResourceId")
    private void showPopupMenu(ViewHolder holder, Forum forum) {
        MenuBuilder menuBuilder = new MenuBuilder(context);
        MenuInflater menuInflater = new MenuInflater(context);
        menuInflater.inflate(R.menu.menu_popup_item_forum, menuBuilder);

        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(context, menuBuilder, holder.itemView);
        menuPopupHelper.setForceShowIcon(true);

        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mnuDeleteForum:
                        listener.onDeleteClick(forum);
                        break;
                    case R.id.mnuEditForum:
                        listener.goToActivityUpdate(forum);
                        break;
                }
                return false;
            }

            @Override
            public void onMenuModeChange(@NonNull MenuBuilder menu) {

            }
        });

        menuPopupHelper.show();
    }
}
