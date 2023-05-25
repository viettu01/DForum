package com.tuplv.dforum.activity.post;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.OBJ_COMMENT;
import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.OBJ_REP_COMMENT;
import static com.tuplv.dforum.until.Constant.STATUS_DISABLE;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;
import static com.tuplv.dforum.until.Constant.TYPE_NOTIFY_ADD_COMMENT;
import static com.tuplv.dforum.until.Constant.TYPE_UPDATE_COMMENT;
import static com.tuplv.dforum.until.Until.sendNotifyToAuthor;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.account.ProfileActivity;
import com.tuplv.dforum.adapter.CommentAdapter;
import com.tuplv.dforum.interf.OnCommentClickListener;
import com.tuplv.dforum.model.Account;
import com.tuplv.dforum.model.Comment;
import com.tuplv.dforum.model.Post;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DetailPostActivity extends AppCompatActivity implements OnCommentClickListener, View.OnClickListener {

    Toolbar tbDetailPost;
    TextView tvNameAuthor, tvDatePost, tvTitlePost, tvContentPost, tvNameAuthorRepComment, tvCancelRepComment;
    LinearLayout llRepComment, llComment;
    RecyclerView rvComment;
    EditText edtComment;
    ImageView imvAvatar, imvSendComment, imvNotify;
    CommentAdapter commentAdapter;
    List<Comment> comments;

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Post post;

    long commentId;
    String accountIdComment; // id của tài khoản bình luận

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_post);
        init();

        getAuthorPost();
        getAllComment();

        edtComment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                llComment.setVisibility(View.VISIBLE);
            }
        });

        tbDetailPost.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Copy tiêu đề và nội dung bài viết vào bộ nhớ tạm
        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                copyPostToClipboard(tvTitlePost.getText().toString().trim() + " " + tvContentPost.getText().toString().trim());
                return false;
            }
        };
        tvContentPost.setOnLongClickListener(onLongClickListener);
        tvTitlePost.setOnLongClickListener(onLongClickListener);
    }

    @Override
    public void onBackPressed() {
        if (edtComment.hasFocus()) {
            edtComment.clearFocus();
            llComment.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    private void init() {
        tbDetailPost = findViewById(R.id.tbDetailPost);
        setSupportActionBar(tbDetailPost);
        imvAvatar = findViewById(R.id.imvAvatar);
        tvNameAuthor = findViewById(R.id.tvNameAuthor);
        tvDatePost = findViewById(R.id.tvDatePost);
        tvTitlePost = findViewById(R.id.tvTitlePost);
        tvContentPost = findViewById(R.id.tvContentPost);
        rvComment = findViewById(R.id.rvComment);

        edtComment = findViewById(R.id.edtComment);
        imvSendComment = findViewById(R.id.imvSendComment);

        llRepComment = findViewById(R.id.llRepComment);
        llComment = findViewById(R.id.llComment);

        tvNameAuthorRepComment = findViewById(R.id.tvNameAuthorRepComment);
        tvCancelRepComment = findViewById(R.id.tvCancelRepComment);

        imvNotify = findViewById(R.id.imvNotify);

        post = (Post) getIntent().getSerializableExtra("post");
        if (post.getStatusNotify() == null) {
            updateStatusNotifyPost(STATUS_ENABLE);
            post.setStatusNotify(STATUS_ENABLE);
        }

        FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(post.getPostId())).child("statusNotify")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dsPost) {
                        if (dsPost.exists()) {
                            post.setStatusNotify(dsPost.getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        if (post.getStatusNotify().equals(STATUS_DISABLE))
            imvNotify.setImageResource(R.drawable.notifications_active_24);
        else
            imvNotify.setImageResource(R.drawable.notifications_off_24);

        if (user == null || !user.getUid().equals(post.getAccountId()))
            imvNotify.setVisibility(View.GONE);

        tvNameAuthor.setOnClickListener(this);
        imvAvatar.setOnClickListener(this);
        imvNotify.setOnClickListener(this);
        imvSendComment.setOnClickListener(this);
        tvCancelRepComment.setOnClickListener(this);
    }

    // Lấy thông tin chi tiết bài viết
    @SuppressLint("SimpleDateFormat")
    private void getAuthorPost() {
        if (post != null) {
            reference.child(OBJ_ACCOUNT).child(post.getAccountId())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Account account = snapshot.getValue(Account.class);
                                if (Objects.requireNonNull(account).getAvatarUri().equals("null"))
                                    imvAvatar.setImageResource(R.drawable.no_avatar);
                                else
                                    Picasso.get().load(account.getAvatarUri()).into(imvAvatar);
                                tvNameAuthor.setText(account.getNickName());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm");
            tvDatePost.setText(dateFormat.format(new Date(post.getApproveDate())));
            tvTitlePost.setText(post.getTitle());
            tvContentPost.setText(post.getContent());
        }
    }

    // Thêm bình luận
    private void addComment() {
        if (!edtComment.getText().toString().trim().isEmpty()) {
            Comment comment = new Comment();
            comment.setCommentId(new Date().getTime());
            comment.setAccountId(user.getUid());
            comment.setContent(edtComment.getText().toString().trim());

            reference.child(OBJ_POST).child(String.valueOf(post.getPostId()))
                    .child(OBJ_COMMENT).child(String.valueOf(comment.getCommentId())).setValue(comment)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(DetailPostActivity.this, "Bình luận thành công!", Toast.LENGTH_SHORT).show();
                                edtComment.setText("");
                            } else
                                Toast.makeText(DetailPostActivity.this, "Có lỗi xảy ra, thử lại sau!", Toast.LENGTH_SHORT).show();
                        }
                    });

            sendNotifyToAuthor(post, TYPE_NOTIFY_ADD_COMMENT, null);
        }
    }

    // Đóng bàn phím
    private void closeKeyBoard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();
        if (focusedView == null) {
            inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        } else
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    // Lấy toàn bộ bình luận của bài viết
    @SuppressLint("NotifyDataSetChanged")
    private void getAllComment() {
        comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(DetailPostActivity.this, R.layout.item_comment, this, comments, post.getPostId());
        rvComment.setAdapter(commentAdapter);
        rvComment.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        reference.child(OBJ_POST).child(String.valueOf(post.getPostId())).child(OBJ_COMMENT)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dsComment) {
                        comments.clear();
                        for (DataSnapshot dataSnapshot : dsComment.getChildren()) {
                            Comment comment = dataSnapshot.getValue(Comment.class);
                            comments.add(comment);
                        }
                        commentAdapter.notifyDataSetChanged();
                        Collections.reverse(comments);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DetailPostActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                    }
                });
        commentAdapter.notifyDataSetChanged();
    }

    @Override
    public void goToActivityUpdate(Comment comment) {
        Intent intent = new Intent(this, UpdateCommentActivity.class);
        intent.putExtra("comment", comment);
        intent.putExtra("postId", String.valueOf(post.getPostId()));
        intent.putExtra("typeUpdate", TYPE_UPDATE_COMMENT);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Comment comment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cảnh báo!");
        builder.setIcon(android.R.drawable.ic_delete);
        builder.setMessage("Bạn có chắc chắn muốn xóa bình luận này?");
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(post.getPostId())).child(OBJ_COMMENT).child(String.valueOf(comment.getCommentId()));
                databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(DetailPostActivity.this, "Xóa bình luận thành công", Toast.LENGTH_SHORT).show();
                            comments.remove(comment);
                            commentAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(DetailPostActivity.this, "Lỗi, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
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

    @Override
    public void goToActivityComment(Comment comment, String nameAuthorRepComment) {
        edtComment.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtComment, InputMethodManager.SHOW_IMPLICIT);

        llRepComment.setVisibility(View.VISIBLE);
        tvNameAuthorRepComment.setText(nameAuthorRepComment);
        commentId = comment.getCommentId();
        accountIdComment = comment.getAccountId();
    }

    // Trả lời comment
    public void addRepComment(long commentId) {
        if (!edtComment.getText().toString().trim().isEmpty()) {
            Comment repComment = new Comment();
            repComment.setCommentId(new Date().getTime());
            repComment.setAccountId(Objects.requireNonNull(user.getUid()));
            repComment.setContent(edtComment.getText().toString().trim());

            reference.child(OBJ_POST).child(String.valueOf(post.getPostId()))
                    .child(OBJ_COMMENT).child(String.valueOf(commentId))
                    .child(OBJ_REP_COMMENT).child(String.valueOf(repComment.getCommentId())).setValue(repComment)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(DetailPostActivity.this, "Trả lời bình luận thành công!", Toast.LENGTH_SHORT).show();
                                edtComment.setText("");
                            } else
                                Toast.makeText(DetailPostActivity.this, "Có lỗi xảy ra, thử lại sau!", Toast.LENGTH_SHORT).show();
                        }
                    });
            sendNotifyToAuthor(post, TYPE_NOTIFY_ADD_COMMENT, accountIdComment);
        }
    }

    private void copyPostToClipboard(String content) {
        // sao chép nội dung của bài viết vào clipboard
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("comment", content);
        clipboard.setPrimaryClip(clip);

        // hiển thị thông báo cho người dùng
        Toast.makeText(DetailPostActivity.this, "Đã sao chép vào bộ nhớ tạm", Toast.LENGTH_SHORT).show();
    }

    //cập nhật trạng thái thông bái bài viết
    private void updateStatusNotifyPost(String statusNotify) {
        HashMap<String, Object> updateStatusNotifyPost = new HashMap<>();
        updateStatusNotifyPost.put("statusNotify", statusNotify);
        reference.child(OBJ_POST).child(String.valueOf(post.getPostId())).updateChildren(updateStatusNotifyPost);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imvSendComment:
                // Kiểm tra xem người dùng đang bình luận hay trả lời bình luận
                closeKeyBoard();
                edtComment.clearFocus();
                if (user == null)
                    Toast.makeText(DetailPostActivity.this, "Bạn cần đăng nhập để sử dụng chức năng này!", Toast.LENGTH_SHORT).show();
                else {
                    if (llRepComment.getVisibility() == View.VISIBLE) {
                        addRepComment(commentId);
                        llRepComment.setVisibility(View.GONE);
                    } else if (llRepComment.getVisibility() == View.GONE)
                        addComment();
                }
                break;
            case R.id.tvCancelRepComment:
                // Tắt chế độ trả lời comment
                closeKeyBoard();
                edtComment.clearFocus();
                llComment.setVisibility(View.GONE);
                llRepComment.setVisibility(View.GONE);
                break;
            case R.id.imvNotify:
                if (post.getStatusNotify().equals(STATUS_ENABLE)) {
                    post.setStatusNotify(STATUS_DISABLE);
                    imvNotify.setImageResource(R.drawable.notifications_active_24);
                } else {
                    post.setStatusNotify(STATUS_ENABLE);
                    imvNotify.setImageResource(R.drawable.notifications_off_24);
                }
                updateStatusNotifyPost(post.getStatusNotify());
                break;
            case R.id.tvNameAuthor:
            case R.id.imvAvatar:
                // bấm tên ra trang cá nhân
                Intent intent = new Intent(DetailPostActivity.this, ProfileActivity.class);
                intent.putExtra("userId", post.getAccountId());
                startActivity(intent);
                break;
        }
    }
}