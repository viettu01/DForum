package com.tuplv.dforum.activity.account;

import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.OBJ_COMMENT;
import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tuplv.dforum.R;
import com.tuplv.dforum.activity.post.DetailPostActivity;
import com.tuplv.dforum.adapter.PostAdapter;
import com.tuplv.dforum.interf.OnPostClickListener;
import com.tuplv.dforum.model.Account;
import com.tuplv.dforum.model.Comment;
import com.tuplv.dforum.model.Post;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ProfileUserActivity extends AppCompatActivity implements OnPostClickListener {

    TextView tvNickName, tvEmail, tvCreatedDate, tvTotalPost, tvTotalComment, tvNoPost;
    ImageView imvAvatar;
    Toolbar tbProfileUser;
    RecyclerView rvMyPost;
    List<Post> myPost;
    PostAdapter myPostAdapter;
    Account account;
    String userId;
    //firebase
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        init();

        if (userId != null) {
            getProfile();
            getMyPost();
            getTotalComment();
        } else
            Toast.makeText(this, "Có lỗi xảy ra thử lại sau !", Toast.LENGTH_SHORT).show();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProfile();
                Intent intent = new Intent(ProfileUserActivity.this, ShowAvatarActivity.class);
                intent.putExtra("avatarUri", account.getAvatarUri());
                startActivity(intent);
            }
        };
        imvAvatar.setOnClickListener(listener);

        tbProfileUser.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        tvEmail = findViewById(R.id.tvEmail);
        tvCreatedDate = findViewById(R.id.tvCreatedDate);
        tvNickName = findViewById(R.id.tvNickName);
        imvAvatar = findViewById(R.id.imvAvatar);

        rvMyPost = findViewById(R.id.rvMyPost);
        tvTotalPost = findViewById(R.id.tvTotalPost);
        tvTotalComment = findViewById(R.id.tvTotalComment);
        tvNoPost = findViewById(R.id.tvNoPost);

        tbProfileUser = findViewById(R.id.tbProfileUser);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
    }

    @SuppressLint("SimpleDateFormat")
    private void getProfile() {
        reference.child(OBJ_ACCOUNT).child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            account = snapshot.getValue(Account.class);
                            if (account != null) {
                                if (account.getAvatarUri().equals("null"))
                                    imvAvatar.setImageResource(R.drawable.no_avatar);
                                else
                                    Picasso.get().load(account.getAvatarUri()).into(imvAvatar);
                                tvNickName.setText(account.getNickName());

                                tvEmail.setText(account.getEmail());

                                Date date = new Date(account.getCreatedDate());
                                SimpleDateFormat fMonth = new SimpleDateFormat("M");
                                SimpleDateFormat fYear = new SimpleDateFormat("yyyy");
                                String month = fMonth.format(date);
                                String year = fYear.format(date);

                                tvCreatedDate.setText("Tham gia vào tháng " + month + " năm " + year);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProfileUserActivity.this, "Có lỗi xảy ra, thử lại sau", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getTotalComment() {
        List<Comment> comments = new ArrayList<>();
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference(OBJ_POST);
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot commentSnapshot : postSnapshot.child(OBJ_COMMENT).getChildren()) {
                        Comment comment = commentSnapshot.getValue(Comment.class);
                        assert comment != null;
                        if (String.valueOf(comment.getAccountId()).equals(userId)) {
                            comments.add(comment);
                        }
                    }
                    tvTotalComment.setText(String.valueOf(comments.size()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // xử lý khi có lỗi xảy ra
            }
        });

    }

    private void getMyPost() {
        myPost = new ArrayList<>();
        myPostAdapter = new PostAdapter(ProfileUserActivity.this, R.layout.item_post, myPost, this);
        rvMyPost.setAdapter(myPostAdapter);
        rvMyPost.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        FirebaseDatabase.getInstance().getReference(OBJ_POST).orderByChild("status").equalTo(STATUS_ENABLE)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        myPost.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Post post = dataSnapshot.getValue(Post.class);
                            if (Objects.requireNonNull(post).getAccountId().equals(userId)) {
                                myPost.add(post);
                            }
                        }
                        tvTotalPost.setText(String.valueOf(myPost.size()));
                        myPostAdapter.notifyDataSetChanged();

                        if (myPost.size() == 0)
                            tvNoPost.setVisibility(View.VISIBLE);
                        else
                            tvNoPost.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProfileUserActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void goToActivityDetail(Post post) {
        HashMap<String, Object> updateView = new HashMap<>();
        updateView.put("view", post.getView() + 1);
        FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(post.getPostId()))
                .updateChildren(updateView);

        Intent intent = new Intent(ProfileUserActivity.this, DetailPostActivity.class);
        intent.putExtra("post", post);

        startActivity(intent);
    }
}