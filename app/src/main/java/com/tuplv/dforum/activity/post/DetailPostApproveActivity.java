package com.tuplv.dforum.activity.post;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class DetailPostApproveActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar tbDetailPostApprove;
    ImageView imvAvatar;
    TextView tvNameAuthor, tvCreatedDatePost, tvTitlePost, tvContentPost;
    Button btnPostApprove, btnNoPostApprove;
    Post post;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_post_approve);
        init();
        getDetailPostApprove();
        tbDetailPostApprove.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        tbDetailPostApprove = findViewById(R.id.tbDetailPostApprove);
        setSupportActionBar(tbDetailPostApprove);
        imvAvatar = findViewById(R.id.imvAvatar);
        tvNameAuthor = findViewById(R.id.tvNameAuthor);
        tvCreatedDatePost = findViewById(R.id.tvCreatedDatePost);
        tvTitlePost = findViewById(R.id.tvTitlePost);
        tvContentPost = findViewById(R.id.tvContentPost);
        btnPostApprove = findViewById(R.id.btnPostApprove);
        btnNoPostApprove = findViewById(R.id.btnNoPostApprove);

        builder = new AlertDialog.Builder(this);
        post = (Post) getIntent().getSerializableExtra("post");

        btnPostApprove.setOnClickListener(this);
        btnNoPostApprove.setOnClickListener(this);
    }

    // Xem chi tiết bài viết
    @SuppressLint("SimpleDateFormat")
    private void getDetailPostApprove() {
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
                                tvNameAuthor.setText(account.getNickName());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm");
            tvCreatedDatePost.setText(dateFormat.format(new Date(post.getCreatedDate())));
            tvTitlePost.setText(post.getTitle());
            tvContentPost.setText(post.getContent());
        }
    }

    private void approvePost() {
        builder.setTitle("Thông báo!");
        builder.setIcon(R.drawable.ic_round_info_24);
        builder.setMessage("Bạn có chắc chắn muốn duyệt bài viết này?");
        builder.setPositiveButton("Duyệt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                HashMap<String, Object> updateView = new HashMap<>();
                updateView.put("status", STATUS_ENABLE);
                updateView.put("approveDate", new Date().getTime());
                FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(post.getPostId()))
                        .updateChildren(updateView);
                Toast.makeText(DetailPostApproveActivity.this, "Bài viết đã được phê duyệt", Toast.LENGTH_SHORT).show();
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

    private void noApprovePost() {
        builder.setTitle("Cảnh báo!");
        builder.setIcon(R.drawable.ic_round_warning_yellow_24);
        builder.setMessage("Bạn có chắc chắn không phê duyệt bài viết này?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(post.getPostId()));
                databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(DetailPostApproveActivity.this, "Bài viết không được phê duyệt", Toast.LENGTH_SHORT).show();
                            finish();

                        } else {
                            Toast.makeText(DetailPostApproveActivity.this, "Lỗi, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPostApprove:
                approvePost();
                break;
            case R.id.btnNoPostApprove:
                noApprovePost();
                break;
        }
    }
}