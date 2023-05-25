package com.tuplv.dforum.adapter;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.OBJ_COMMENT;
import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.OBJ_REP_COMMENT;
import static com.tuplv.dforum.until.Constant.ROLE_ADMIN;
import static com.tuplv.dforum.until.Until.formatTime;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.account.ProfileActivity;
import com.tuplv.dforum.interf.OnCommentClickListener;
import com.tuplv.dforum.model.Account;
import com.tuplv.dforum.model.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    Context context;
    int layout;
    OnCommentClickListener listener;
    SharedPreferences sharedPreferences;
    long postId;

    RepCommentAdapter repCommentAdapter;

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Account[] account = new Account[1];
    List<Comment> comments, repComments;
    long countRepComment;

    public CommentAdapter(Context context, int layout, List<Comment> comments, OnCommentClickListener onCommentClickListener) {
        this.context = context;
        this.layout = layout;
        this.comments = comments;
        this.listener = onCommentClickListener;
    }

    public CommentAdapter(Context context, int layout, OnCommentClickListener listener, List<Comment> comments, long postId) {
        this.context = context;
        this.layout = layout;
        this.listener = listener;
        this.comments = comments;
        this.postId = postId;
        this.sharedPreferences = context.getSharedPreferences("account", Context.MODE_PRIVATE);
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
                            account[0] = snapshot.getValue(Account.class);
                            if (Objects.requireNonNull(account[0]).getAvatarUri().equals("null"))
                                holder.imvAvatar.setImageResource(R.drawable.no_avatar);
                            else
                                Picasso.get().load(account[0].getAvatarUri()).into(holder.imvAvatar);
                            holder.tvNameCommentator.setText(account[0].getNickName());
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
                if (isShowMore[0]) {
                    holder.tvContentComment.setMaxLines(7);
                } else {
                    holder.tvContentComment.setMaxLines(Integer.MAX_VALUE);
                }
                isShowMore[0] = !isShowMore[0];
            }
        });

        //format thời gian
        holder.tvTimeComment.setText(formatTime(comment.getCommentId()));

        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showPopupMenu(holder, comment);
                return false;
            }
        };
        holder.itemView.setOnLongClickListener(onLongClickListener);
        holder.tvContentComment.setOnLongClickListener(onLongClickListener);

        holder.tvRepComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.goToActivityComment(comment, holder.tvNameCommentator.getText().toString().trim());
            }
        });

        getCountRepComment(holder.tvShowRepComment, comment.getCommentId());
        getAllRepComment(holder.rvRepComment, comment.getCommentId());

        holder.tvShowRepComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.tvShowRepComment.getText().equals("Ẩn")) {
                    getCountRepComment(holder.tvShowRepComment, comment.getCommentId());
                    holder.rvRepComment.setVisibility(View.GONE);
                } else {
                    getAllRepComment(holder.rvRepComment, comment.getCommentId());
                    holder.rvRepComment.setVisibility(View.VISIBLE);
                    holder.tvShowRepComment.setText("Ẩn");
                }

            }
        });

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("userId", comment.getAccountId());
                context.startActivity(intent);
            }
        };

        holder.tvNameCommentator.setOnClickListener(onClickListener);
        holder.imvAvatar.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        if (comments != null)
            return comments.size();
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNameCommentator, tvTimeComment, tvContentComment, tvRepComment, tvShowRepComment;
        ImageView imvAvatar;
        RecyclerView rvRepComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameCommentator = itemView.findViewById(R.id.tvNameCommentator);
            tvTimeComment = itemView.findViewById(R.id.tvTimeComment);
            tvContentComment = itemView.findViewById(R.id.tvContentComment);
            imvAvatar = itemView.findViewById(R.id.imvAvatar);

            tvRepComment = itemView.findViewById(R.id.tvRepComment);
            rvRepComment = itemView.findViewById(R.id.rvRepComment);

            tvShowRepComment = itemView.findViewById(R.id.tvShowRepComment);
        }
    }

    @SuppressLint("RestrictedApi, NonConstantResourceId")
    private void showPopupMenu(CommentAdapter.ViewHolder holder, Comment comment) {
        MenuBuilder menuBuilder = new MenuBuilder(context);
        MenuInflater menuInflater = new MenuInflater(context);
        menuInflater.inflate(R.menu.menu_popup_item_comment, menuBuilder);

        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(context, menuBuilder, holder.itemView);
        menuPopupHelper.setForceShowIcon(true);
        MenuItem mnuEditComment = menuBuilder.findItem(R.id.mnuEditComment);
        MenuItem mnuDeleteComment = menuBuilder.findItem(R.id.mnuDeleteComment);

        if (!Objects.requireNonNull(mAuth.getCurrentUser()).getUid().equals(comment.getAccountId())) {
            mnuEditComment.setVisible(false);
            mnuDeleteComment.setVisible(false);
            if (sharedPreferences.getString("role", "").equals(ROLE_ADMIN))
                mnuDeleteComment.setVisible(true);
        }

        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mnuDeleteComment:
                        listener.onDeleteClick(comment);
                        break;
                    case R.id.mnuEditComment:
                        listener.goToActivityUpdate(comment);
                        break;
                    case R.id.mnuCopyComment:
                        copyCommentToClipboard(comment.getContent());
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

    private void copyCommentToClipboard(String content) {
        // sao chép nội dung của bình luận vào clipboard
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("comment", content);
        clipboard.setPrimaryClip(clip);

        // hiển thị thông báo cho người dùng
        Toast.makeText(context.getApplicationContext(), "Đã sao chép vào bộ nhớ tạm", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getAllRepComment(RecyclerView rvRepComment, long commentId) {
        repComments = new ArrayList<>();
        repCommentAdapter = new RepCommentAdapter(context, R.layout.item_rep_comment, (OnCommentClickListener) context, postId, commentId, repComments);
        rvRepComment.setAdapter(repCommentAdapter);
        rvRepComment.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        reference.child(OBJ_POST).child(String.valueOf(postId))
                .child(OBJ_COMMENT).child(String.valueOf(commentId))
                .child(OBJ_REP_COMMENT)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dsRepComment) {
                        repComments.clear();
                        for (DataSnapshot dataSnapshot : dsRepComment.getChildren()) {
                            Comment comment = dataSnapshot.getValue(Comment.class);
                            repComments.add(comment);
                        }
                        repCommentAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
                    }
                });
        repCommentAdapter.notifyDataSetChanged();
    }

    private void getCountRepComment(TextView tvShowRepComment, long commentId) {
        reference.child(OBJ_POST).child(String.valueOf(postId))
                .child(OBJ_COMMENT).child(String.valueOf(commentId))
                .child(OBJ_REP_COMMENT).addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        countRepComment = dataSnapshot.getChildrenCount();
                        if (countRepComment == 0)
                            tvShowRepComment.setVisibility(View.GONE);
                        else {
                            tvShowRepComment.setText("Xem " + countRepComment + " phản hồi");
                            tvShowRepComment.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }


}
