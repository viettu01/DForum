package com.tuplv.dforum.adapter;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;
import static com.tuplv.dforum.until.Constant.STATUS_NO_APPROVE_POST;
import static com.tuplv.dforum.until.Until.formatNotify;
import static com.tuplv.dforum.until.Until.formatTime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tuplv.dforum.R;
import com.tuplv.dforum.interf.OnNotifyClickListener;
import com.tuplv.dforum.model.Account;
import com.tuplv.dforum.model.Notify;
import com.tuplv.dforum.model.Post;

import java.util.List;
import java.util.Objects;

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.ViewHolder> {

    Context context;
    int layout;
    List<Notify> notifies;
    OnNotifyClickListener listener;

    public NotifyAdapter(Context context, int layout, List<Notify> notifies, OnNotifyClickListener listener) {
        this.context = context;
        this.layout = layout;
        this.notifies = notifies;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layout, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("SimpleDateFormat, SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notify notify = notifies.get(position);
        if (notify.getStatus().equals(STATUS_ENABLE))
            holder.llItemListNotify.setBackgroundColor(android.R.attr.colorPrimary);

        FirebaseDatabase.getInstance().getReference(OBJ_ACCOUNT).child(notify.getAccountId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Account account = snapshot.getValue(Account.class);
                            if (Objects.requireNonNull(account).getAvatarUri().equals("null"))
                                holder.imvAvatar.setImageResource(R.drawable.no_avatar);
                            else
                                Picasso.get().load(account.getAvatarUri()).into(holder.imvAvatar);
                            holder.tvContentNotify.setText(account.getNickName() + formatNotify(notify.getTypeNotify()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        if (notify.getPostId() != 0)
            FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(notify.getPostId()))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Post post = snapshot.getValue(Post.class);
                            if (!Objects.requireNonNull(post).getStatus().equals(STATUS_NO_APPROVE_POST))
                                holder.tvContentNotify.setText(holder.tvContentNotify.getText().toString() + post.getTitle());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        if (notify.getForumId() != 0) {
            holder.tvContentNotify.setText(holder.tvContentNotify.getText().toString() + notify.getTypeNotify());
        }


        holder.tvDateNotify.setText(formatTime(notify.getNotifyId()));

        holder.llItemListNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notify.getPostId() != 0)
                    listener.goToDetailPostActivity(notify);

                if (notify.getForumId() != 0)
                    listener.goToListPostActivity(notify);
            }
        });

        holder.imvShowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder, notify);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (notifies != null)
            return notifies.size();
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llItemListNotify;
        ImageView imvAvatar, imvShowMore;
        TextView tvContentNotify, tvDateNotify;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llItemListNotify = itemView.findViewById(R.id.llItemListNotify);
            imvAvatar = itemView.findViewById(R.id.imvAvatar);
            imvShowMore = itemView.findViewById(R.id.imvShowMore);
            tvContentNotify = itemView.findViewById(R.id.tvContentNotify);
            tvDateNotify = itemView.findViewById(R.id.tvDateNotify);
        }
    }

    @SuppressLint("RestrictedApi, NonConstantResourceId")
    private void showPopupMenu(ViewHolder holder, Notify notify) {
        MenuBuilder menuBuilder = new MenuBuilder(context);
        MenuInflater menuInflater = new MenuInflater(context);
        menuInflater.inflate(R.menu.menu_popup_item_notify, menuBuilder);

        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(context, menuBuilder, holder.itemView);
        menuPopupHelper.setGravity(Gravity.END);
        menuPopupHelper.setForceShowIcon(true);

        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mnuCheckNotify:
                        listener.onCheckNotify(notify);
                        break;
                    case R.id.mnuDeleteNotify:
                        listener.onDeleteNotify(notify);
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
