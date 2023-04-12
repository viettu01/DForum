package com.tuplv.dforum.activity;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.OBJ_COMMENT;
import static com.tuplv.dforum.until.Constant.OBJ_FORUM;
import static com.tuplv.dforum.until.Constant.OBJ_POST;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.tuplv.dforum.R;
import com.tuplv.dforum.interf.OnCommentClickListener;
import com.tuplv.dforum.model.Comment;
import com.tuplv.dforum.model.Forum;

import java.util.HashMap;
import java.util.Objects;

public class UpdateCommentActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imvAvatar, imvCancel;
    Button btnCancel, btnUpdateComment;
    EditText edtContentComment;
    Comment comment;
    String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_comment);

        init();
    }

    private void init() {
        imvAvatar = findViewById(R.id.imvAvatar);
        imvCancel = findViewById(R.id.imvCancel);
        btnCancel = findViewById(R.id.btnCancel);
        btnUpdateComment = findViewById(R.id.btnUpdateComment);
        edtContentComment = findViewById(R.id.edtContentComment);

        comment = (Comment) getIntent().getSerializableExtra("comment");
        String avatarUri = String.valueOf(getIntent().getStringExtra("avatarUri"));
        postId = getIntent().getStringExtra("postId");
        if (avatarUri.equals("null")) {
            Objects.requireNonNull(imvAvatar).setImageResource(R.drawable.no_avatar);
        }
        else
            Picasso.get().load(Uri.parse(avatarUri)).into(imvAvatar);
        Objects.requireNonNull(edtContentComment).setText(comment.getContent());

        btnCancel.setOnClickListener(this);
        btnUpdateComment.setOnClickListener(this);
        imvCancel.setOnClickListener(this);
    }

    private void updateComment() {
        String newComment = edtContentComment.getText().toString();
        HashMap<String, Object> updateComment = new HashMap<>();
        updateComment.put("content", newComment);

        FirebaseDatabase.getInstance().getReference(OBJ_POST)
                .child(Objects.requireNonNull(postId))
                .child(OBJ_COMMENT)
                .child(String.valueOf(comment.getCommentId()))
                .updateChildren(updateComment);
        Toast.makeText(this, "Cập nhật bình luận thành công", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imvCancel:
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnUpdateComment:
                updateComment();
                finish();
                break;
        }
    }
}