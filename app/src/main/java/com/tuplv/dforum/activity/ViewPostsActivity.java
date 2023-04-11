package com.tuplv.dforum.activity;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.OBJ_COMMENT;
import static com.tuplv.dforum.until.Constant.OBJ_NOTIFY;
import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.STATUS_DISABLE;
import static com.tuplv.dforum.until.Constant.TYPE_NOTIFY_ADD_COMMENT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tuplv.dforum.R;
import com.tuplv.dforum.adapter.CommentAdapter;
import com.tuplv.dforum.interf.OnCommentClickListener;
import com.tuplv.dforum.model.Account;
import com.tuplv.dforum.model.Comment;
import com.tuplv.dforum.model.Notify;
import com.tuplv.dforum.model.Post;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPostsActivity extends AppCompatActivity implements OnCommentClickListener {

    Toolbar tbViewPosts;
    CircleImageView imvAvatar;
    TextView tvNamePoster, tvDatePost, tvTitlePost, tvContentPosts;
    RecyclerView rvComment;
    EditText edtComment;
    ImageView imvSendComment;
    LinearLayout ll_comment;
    CommentAdapter commentAdapter;
    List<Comment> comments;

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        init();

        if (user == null)
            ll_comment.setVisibility(View.GONE);

        tbViewPosts.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        post = (Post) getIntent().getSerializableExtra("post");
        loadData();

        getComment();

        imvSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyBoard();
                addComment();
                edtComment.setText("");
            }
        });
    }

    private void init() {
        tbViewPosts = findViewById(R.id.tbViewPosts);
        setSupportActionBar(tbViewPosts);
        imvAvatar = findViewById(R.id.imvAvatar);
        tvNamePoster = findViewById(R.id.tvNamePoster);
        tvDatePost = findViewById(R.id.tvDatePost);
        tvTitlePost = findViewById(R.id.tvTitlePost);
        tvContentPosts = findViewById(R.id.tvContentPosts);
        rvComment = findViewById(R.id.rvComment);

        edtComment = findViewById(R.id.edtComment);
        imvSendComment = findViewById(R.id.imvSendComment);
        ll_comment = findViewById(R.id.ll_comment);
    }

    @SuppressLint("SimpleDateFormat")
    private void loadData() {
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
                                tvNamePoster.setText(account.getNickName());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm");
            tvDatePost.setText(dateFormat.format(new Date(post.getApprovalDate())));
            tvTitlePost.setText(post.getTitle());
            tvContentPosts.setText(post.getContent());
        }
    }

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
                            task.isSuccessful();
                        }
                    });

            if (!post.getAccountId().equals(user.getUid())) {
                Notify notify = new Notify();
                notify.setNotifyId(new Date().getTime());
                notify.setPostId(post.getPostId());
                notify.setAccountId(user.getUid());
                notify.setStatus(STATUS_DISABLE);
                notify.setTypeNotify(TYPE_NOTIFY_ADD_COMMENT);
                reference.child(OBJ_ACCOUNT).child(post.getAccountId())
                        .child(OBJ_NOTIFY).child(String.valueOf(notify.getNotifyId())).setValue(notify)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                task.isSuccessful();
                            }
                        });
            }
        }
    }

    // đóng bàn phím
    private void closeKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getComment() {
        comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(ViewPostsActivity.this, R.layout.item_comment, this, comments);
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
                        Toast.makeText(ViewPostsActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                    }
                });
        commentAdapter.notifyDataSetChanged();
    }

    @SuppressLint("InflateParams")
    private void dialogEditComment() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.dialog_bottom_sheet_edit_comment, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        bottomSheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheetDialog.show();
    }

    @Override
    public void goToActivityUpdate(Comment comment) {
        dialogEditComment();
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
                            Toast.makeText(ViewPostsActivity.this, "Xóa bình luận thành công", Toast.LENGTH_SHORT).show();
                            comments.remove(comment);
                            commentAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(ViewPostsActivity.this, "Lỗi, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
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