package com.tuplv.dforum.adapter;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.OBJ_COMMENT;
import static com.tuplv.dforum.until.Constant.OBJ_FORUM;
import static com.tuplv.dforum.until.Constant.ROLE_ADMIN;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.ListForumActivity;
import com.tuplv.dforum.interf.OnCommentClickListener;
import com.tuplv.dforum.interf.OnForumClickListener;
import com.tuplv.dforum.model.Account;
import com.tuplv.dforum.model.Comment;
import com.tuplv.dforum.model.Forum;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private final Context context;
    private final int layout;
    OnCommentClickListener listener;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final List<Comment> comments;

    public CommentAdapter(Context context, int layout, OnCommentClickListener listener, List<Comment> comments) {
        this.context = context;
        this.layout = layout;
        this.listener = listener;
        this.comments = comments;
    }

    public CommentAdapter(Context context, int layout, List<Comment> comments) {
        this.context = context;
        this.layout = layout;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layout, parent, false);

        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        Comment comment = comments.get(position);

        holder.tvContentComment.setText(comment.getContent());

        reference.child(OBJ_ACCOUNT).child(comment.getAccountId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Account account = snapshot.getValue(Account.class);
                            if (Objects.requireNonNull(account).getAvatarUri().equals("null"))
                                holder.imvAvatar.setImageResource(R.drawable.no_avatar);
                            else
                                Picasso.get().load(account.getAvatarUri()).into(holder.imvAvatar);
                            holder.tvNameCommentator.setText(account.getNickName());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        final boolean[] isShowMore = {false};
        holder.tvContentComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShowMore[0]){
                    holder.tvContentComment.setMaxLines(3);
                }
                else {
                    holder.tvContentComment.setMaxLines(Integer.MAX_VALUE);
                }
                isShowMore[0] = !isShowMore[0];
            }
        });
        Date commentDate = new Date(comment.getCommentId());
        long diffInMillis = new Date().getTime() - commentDate.getTime();
        long secondsAgo = TimeUnit.SECONDS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        if (secondsAgo < 60) {
            holder.tvTimeComment.setText("Vừa xong");
        } else if (secondsAgo < 3600) {
            long minutesAgo = TimeUnit.MINUTES.convert(diffInMillis, TimeUnit.MILLISECONDS);
            holder.tvTimeComment.setText(minutesAgo + " phút");
        } else if (secondsAgo < 86400) {
            long hoursAgo = TimeUnit.HOURS.convert(diffInMillis, TimeUnit.MILLISECONDS);
            holder.tvTimeComment.setText(hoursAgo + " giờ");
        } else if (secondsAgo < 604800) {
            long daysAgo = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
            holder.tvTimeComment.setText(daysAgo + " ngày");
        } else if (secondsAgo < 31536000) {
            long diffInWeeks = diffInMillis / (7 * 24 * 60 * 60 * 1000);
            holder.tvTimeComment.setText(diffInWeeks + " tuần");
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(commentDate);

            int commentMonth = calendar.get(Calendar.MONTH);
            int currentMonth = Calendar.getInstance().get(Calendar.MONTH);

            int commentDate2 = calendar.get(Calendar.DATE);
            int currentDate = Calendar.getInstance().get(Calendar.DATE);

            int diffInYears = Calendar.getInstance().get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
            if (currentMonth < commentMonth || (currentMonth == commentMonth && currentDate < commentDate2)) {
                diffInYears--;
            }
            holder.tvTimeComment.setText(diffInYears + " năm");
        }

        if (Objects.requireNonNull(mAuth.getCurrentUser()).getUid().equals(comment.getAccountId())) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showPopupMenu(holder, comment);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (comments != null) {
            return comments.size();
        }

        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNameCommentator, tvTimeComment, tvContentComment;
        ImageView imvAvatar;
        Button btnReadMore;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameCommentator = itemView.findViewById(R.id.tvNameCommentator);
            tvTimeComment = itemView.findViewById(R.id.tvTimeComment);
            tvContentComment = itemView.findViewById(R.id.tvContentComment);
            imvAvatar = itemView.findViewById(R.id.imvAvatar);

            btnReadMore = itemView.findViewById(R.id.btn_read_more);
        }
    }
    @SuppressLint("RestrictedApi, NonConstantResourceId")
    private void showPopupMenu(CommentAdapter.ViewHolder holder, Comment comment) {
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
                        listener.onDeleteClick(comment);
                        Toast.makeText(context, "Xóa bình luận", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.mnuEditForum:
                        //listener.goToActivityUpdate(forum);
                        Toast.makeText(context, "Chỉnh sửa bình luận", Toast.LENGTH_SHORT).show();
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
