package com.tuplv.dforum.activity;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.OBJ_FORUM;
import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.STATUS_DISABLE;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tuplv.dforum.R;
import com.tuplv.dforum.model.Account;
import com.tuplv.dforum.model.Post;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class ViewPostApproveActivity extends AppCompatActivity {
    Toolbar tbViewPostsApprove;
    ImageView imvAvatar;
    TextView tvNamePoster, tvDatePostApprove, tvTitlePost, tvContentPosts;
    Button btnPostsApprove, btnNoPostApprove;
    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post_approve);
        init();
        loadData();
        tbViewPostsApprove.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnPostsApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewPostApproveActivity.this);
                builder.setTitle("Cảnh báo!");
                builder.setIcon(android.R.drawable.ic_delete);
                builder.setMessage("Bạn có chắc chắn muốn duyệt bài viết này?");
                builder.setPositiveButton("Duyệt", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        HashMap<String, Object> updateView = new HashMap<>();
                        updateView.put("status", STATUS_ENABLE);
                        FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(post.getPostId()))
                                .updateChildren(updateView);
                        Toast.makeText(ViewPostApproveActivity.this, "Bài viết đã được phê duyệt", Toast.LENGTH_SHORT).show();
                            finish();
                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();

            }
        });
        btnNoPostApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewPostApproveActivity.this);
                builder.setTitle("Cảnh báo!");
                builder.setIcon(android.R.drawable.ic_delete);
                builder.setMessage("Bạn có chắc chắn không phê duyệt bài viết này?");
                builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(post.getPostId()));
                        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ViewPostApproveActivity.this, "Bài viết không được phê duyệt", Toast.LENGTH_SHORT).show();
                                    finish();

                                } else {
                                    Toast.makeText(ViewPostApproveActivity.this, "Lỗi, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
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
        });
    }

    private void init() {
        tbViewPostsApprove = findViewById(R.id.tbViewPostsApprove);
        setSupportActionBar(tbViewPostsApprove);
        imvAvatar = findViewById(R.id.imvAvatar);
        tvNamePoster = findViewById(R.id.tvNamePoster);
        tvDatePostApprove = findViewById(R.id.tvDatePostApprove);
        tvTitlePost = findViewById(R.id.tvTitlePost);
        tvContentPosts = findViewById(R.id.tvContentPosts);
        btnPostsApprove = findViewById(R.id.btnPostsApprove);
        btnNoPostApprove = findViewById(R.id.btnNoPostApprove);

        post = (Post) getIntent().getSerializableExtra("post");
    }

    @SuppressLint("SimpleDateFormat")
    private void loadData() {
        if (post != null) {
            FirebaseDatabase.getInstance().getReference(OBJ_ACCOUNT).child(post.getAccountId())
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
            tvDatePostApprove.setText(dateFormat.format(new Date(post.getApprovalDate())));
            tvTitlePost.setText(post.getTitle());
            tvContentPosts.setText(post.getContent());
        }
    }
}