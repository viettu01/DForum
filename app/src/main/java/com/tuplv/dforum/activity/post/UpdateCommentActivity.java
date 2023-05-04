package com.tuplv.dforum.activity.post;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.OBJ_COMMENT;
import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.OBJ_REP_COMMENT;
import static com.tuplv.dforum.until.Constant.TYPE_UPDATE_COMMENT;
import static com.tuplv.dforum.until.Constant.TYPE_UPDATE_REP_COMMENT;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tuplv.dforum.R;
import com.tuplv.dforum.model.Account;
import com.tuplv.dforum.model.Comment;

import java.util.HashMap;
import java.util.Objects;

public class UpdateCommentActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imvAvatar;
    Button btnCancel, btnUpdateComment;
    EditText edtContentComment;
    Toolbar tbUpdateComment;
    Comment comment;
    String postId, commentId, avatarUri, typeUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_comment);

        init();

        FirebaseDatabase.getInstance().getReference()
                .child(OBJ_ACCOUNT)
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("avatarUri")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (Objects.equals(snapshot.getValue(String.class), "null"))
                                imvAvatar.setImageResource(R.drawable.no_avatar);
                            else
                                Picasso.get().load(Uri.parse(snapshot.getValue(String.class))).into(imvAvatar);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
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
        setSupportActionBar(tbUpdateComment);

        comment = (Comment) getIntent().getSerializableExtra("comment");

        avatarUri = String.valueOf(getIntent().getStringExtra("avatarUri"));
        postId = getIntent().getStringExtra("postId");
        commentId = getIntent().getStringExtra("commentId");
        typeUpdate = getIntent().getStringExtra("typeUpdate");

        btnCancel.setOnClickListener(this);
        btnUpdateComment.setOnClickListener(this);
    }

    private void updateComment() {
        String newComment = edtContentComment.getText().toString();
        HashMap<String, Object> updateComment = new HashMap<>();
        updateComment.put("content", newComment);

        if (typeUpdate.equals(TYPE_UPDATE_COMMENT)){
            FirebaseDatabase.getInstance().getReference(OBJ_POST)
                    .child(Objects.requireNonNull(postId))
                    .child(OBJ_COMMENT)
                    .child(String.valueOf(comment.getCommentId()))
                    .updateChildren(updateComment);
        } else if (typeUpdate.equals(TYPE_UPDATE_REP_COMMENT)) {
            FirebaseDatabase.getInstance().getReference(OBJ_POST)
                    .child(Objects.requireNonNull(postId))
                    .child(OBJ_COMMENT)
                    .child(String.valueOf(commentId))
                    .child(OBJ_REP_COMMENT)
                    .child(String.valueOf(comment.getCommentId()))
                    .updateChildren(updateComment);
        }

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