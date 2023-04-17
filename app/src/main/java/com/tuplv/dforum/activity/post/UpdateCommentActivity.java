package com.tuplv.dforum.activity.post;

import static com.tuplv.dforum.until.Constant.OBJ_COMMENT;
import static com.tuplv.dforum.until.Constant.OBJ_POST;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.tuplv.dforum.R;
import com.tuplv.dforum.model.Comment;

import java.util.HashMap;
import java.util.Objects;

public class UpdateCommentActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imvAvatar;
    Button btnCancel, btnUpdateComment;
    EditText edtContentComment;
    Toolbar tbUpdateComment;
    Comment comment;
    String postId, avatarUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_comment);

        init();

        if (avatarUri.equals("null")) {
            Objects.requireNonNull(imvAvatar).setImageResource(R.drawable.no_avatar);
        } else
            Picasso.get().load(Uri.parse(avatarUri)).into(imvAvatar);
        Objects.requireNonNull(edtContentComment).setText(comment.getContent());

        tbUpdateComment.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        imvAvatar = findViewById(R.id.imvAvatar);
        btnCancel = findViewById(R.id.btnCancel);
        btnUpdateComment = findViewById(R.id.btnUpdateComment);
        edtContentComment = findViewById(R.id.edtContentComment);
        tbUpdateComment = findViewById(R.id.tbUpdateComment);

        comment = (Comment) getIntent().getSerializableExtra("comment");

        avatarUri = String.valueOf(getIntent().getStringExtra("avatarUri"));
        postId = getIntent().getStringExtra("postId");

        btnCancel.setOnClickListener(this);
        btnUpdateComment.setOnClickListener(this);
        tbUpdateComment = findViewById(R.id.tbUpdateProfile);
        setSupportActionBar(tbUpdateComment);
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