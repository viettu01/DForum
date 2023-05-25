package com.tuplv.dforum.activity.post;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;
import static com.tuplv.dforum.until.Constant.STATUS_NO_APPROVE_POST;
import static com.tuplv.dforum.until.Until.sendNotifyToAuthor;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.account.ProfileActivity;
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
        imvAvatar.setOnClickListener(this);
        tvNameAuthor.setOnClickListener(this);
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
                //sendNotifyToAuthor(post, TYPE_NOTIFY_APPROVE_POST,null);
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

    private void dialogNoApprove(Post post) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_approve_post);

        // Không cho thoát khi bấm ra ngoài màn hình
        dialog.setCanceledOnTouchOutside(false);

        //ánh xạ
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        EditText edtReason = dialog.findViewById(R.id.edtReason);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reason = edtReason.getText().toString().trim();
                if (reason.isEmpty())
                    Toast.makeText(DetailPostApproveActivity.this, "Vui lòng nhập lý do", Toast.LENGTH_SHORT).show();
                else {
                    HashMap<String, Object> updateView = new HashMap<>();
                    updateView.put("status", STATUS_NO_APPROVE_POST);
                    updateView.put("approveDate", new Date().getTime());
                    FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(post.getPostId()))
                            .updateChildren(updateView);
                    Toast.makeText(DetailPostApproveActivity.this, "Thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                    sendNotifyToAuthor(post, " (Admin) không duyệt bài viết của bạn vì lý do: " + reason, null);
                    dialog.dismiss();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPostApprove:
                approvePost();
                break;
            case R.id.btnNoPostApprove:
                dialogNoApprove(post);
                break;
            case R.id.tvNameAuthor:
            case R.id.imvAvatar:
                // bấm tên ra trang cá nhân
                Intent intent = new Intent(DetailPostApproveActivity.this, ProfileActivity.class);
                intent.putExtra("userId", post.getAccountId());
                startActivity(intent);
                break;
        }
    }
}