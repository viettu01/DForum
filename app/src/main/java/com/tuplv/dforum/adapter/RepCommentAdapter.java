package com.tuplv.dforum.adapter;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.OBJ_COMMENT;
import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.OBJ_REP_COMMENT;
import static com.tuplv.dforum.until.Constant.ROLE_ADMIN;
import static com.tuplv.dforum.until.Constant.TYPE_UPDATE_REP_COMMENT;
import static com.tuplv.dforum.until.Until.formatTime;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
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
import com.tuplv.dforum.activity.account.ProfileActivity;
import com.tuplv.dforum.activity.post.UpdateCommentActivity;
import com.tuplv.dforum.interf.OnCommentClickListener;
import com.tuplv.dforum.model.Account;
import com.tuplv.dforum.model.Comment;

import java.util.List;
import java.util.Objects;

public class RepCommentAdapter extends RecyclerView.Adapter<RepCommentAdapter.ViewHolder> {
    Context context;
    int layout;
    OnCommentClickListener listener;
    SharedPreferences sharedPreferences;
    long postId;
    long commentId;

    List<Comment> repComments;

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Account[] account = new Account[1];

    public RepCommentAdapter(Context context, int layout, OnCommentClickListener listener, long postId, long commentId, List<Comment> repComments) {
        this.context = context;
        this.layout = layout;
        this.listener = listener;
        this.postId = postId;
        this.commentId = commentId;
        this.repComments = repComments;
        this.sharedPreferences = context.getSharedPreferences("account", Context.MODE_PRIVATE);
    }
    @NonNull
    @Override
    public RepCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layout, parent, false);

        return new RepCommentAdapter.ViewHolder(view);
    }

    @Override
    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    public void onBindViewHolder(@NonNull RepCommentAdapter.ViewHolder holder, int position) {
        Comment comment = repComments.get(position);

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
        if (repComments != null)
            return repComments.size();
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNameCommentator, tvTimeComment, tvContentComment, tvRepComment;
        ImageView imvAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameCommentator = itemView.findViewById(R.id.tvNameCommentator);
            tvTimeComment = itemView.findViewById(R.id.tvTimeComment);
            tvContentComment = itemView.findViewById(R.id.tvContentComment);
            imvAvatar = itemView.findViewById(R.id.imvAvatar);
            tvRepComment = itemView.findViewById(R.id.tvRepComment);
        }
    }

    @SuppressLint("RestrictedApi, NonConstantResourceId")
    private void showPopupMenu(RepCommentAdapter.ViewHolder holder, Comment repComment) {
        MenuBuilder menuBuilder = new MenuBuilder(context);
        MenuInflater menuInflater = new MenuInflater(context);
        menuInflater.inflate(R.menu.menu_popup_item_comment, menuBuilder);

        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(context, menuBuilder, holder.itemView);
        menuPopupHelper.setForceShowIcon(true);
        MenuItem mnuEditComment = menuBuilder.findItem(R.id.mnuEditComment);
        MenuItem mnuDeleteComment = menuBuilder.findItem(R.id.mnuDeleteComment);

        if (!Objects.requireNonNull(mAuth.getCurrentUser()).getUid().equals(repComment.getAccountId())) {
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
                        deleteRepComment(repComment);
                        break;
                    case R.id.mnuEditComment:
                        Intent intent = new Intent(context, UpdateCommentActivity.class);
                        intent.putExtra("comment", repComment);
                        intent.putExtra("postId", String.valueOf(postId));
                        intent.putExtra("commentId", String.valueOf(commentId));
                        intent.putExtra("typeUpdate", TYPE_UPDATE_REP_COMMENT);
                        context.startActivity(intent);
                        break;
                    case R.id.mnuCopyComment:
                        copyRepCommentToClipboard(repComment.getContent());
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

    // Copy nội dung repComment
    private void copyRepCommentToClipboard(String content) {
        // sao chép nội dung của bình luận vào clipboard
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("comment", content);
        clipboard.setPrimaryClip(clip);

        // hiển thị thông báo cho người dùng
        Toast.makeText(context.getApplicationContext(), "Đã sao chép vào bộ nhớ tạm", Toast.LENGTH_SHORT).show();
    }

    // Xóa 1 repComment
    public void deleteRepComment(Comment repComment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Cảnh báo!");
        builder.setIcon(android.R.drawable.ic_delete);
        builder.setMessage("Bạn có chắc chắn muốn xóa bình luận này?");
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(OBJ_POST)
                        .child(String.valueOf(postId))
                        .child(OBJ_COMMENT)
                        .child(String.valueOf(commentId))
                        .child(OBJ_REP_COMMENT)
                        .child(String.valueOf(repComment.getCommentId()));
                databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Xóa bình luận thành công", Toast.LENGTH_SHORT).show();
                            repComments.remove(repComment);
                            //commentAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, "Lỗi, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }
}
