package com.tuplv.dforum.activity.post;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuplv.dforum.R;
import com.tuplv.dforum.adapter.PostAdapter;
import com.tuplv.dforum.interf.OnPostClickListener;
import com.tuplv.dforum.model.Forum;
import com.tuplv.dforum.model.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ListPostActivity extends AppCompatActivity implements View.OnClickListener, OnPostClickListener {

    Toolbar tbListPost;
    TextView tvNameForum, tvTotalPostForum, tvDesForum, tvNoPost;
    ImageView imvFilterPost;
    RecyclerView rvListPost;
    FloatingActionButton fabAddPost;

    PostAdapter postAdapter;
    List<Post> posts;
    Forum forum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_post);
        init();
        tbListPost.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (forum != null && forum.getForumId() != 0) {
            posts.clear();
            getPostByForumId();
            return;
        }
        getAllPost();
    }

    private void init() {
        tbListPost = findViewById(R.id.tbListPost);
        setSupportActionBar(tbListPost);
        tvNameForum = findViewById(R.id.tvNameForum);
        tvTotalPostForum = findViewById(R.id.tvTotalPostForum);
        tvDesForum = findViewById(R.id.tvDesForum);
        tvNoPost = findViewById(R.id.tvNoPost);
        imvFilterPost = findViewById(R.id.imvFilterPost);
        rvListPost = findViewById(R.id.rvListPost);
        fabAddPost = findViewById(R.id.fabAddPost);

        posts = new ArrayList<>();
        postAdapter = new PostAdapter(this, R.layout.item_post, posts, this);
        rvListPost.setAdapter(postAdapter);
        rvListPost.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        forum = (Forum) getIntent().getSerializableExtra("forum");
        tvNameForum.setText(forum.getName());
        tvDesForum.setText(forum.getDescription());
        if (forum.getDescription().equals(""))
            tvDesForum.setVisibility(View.GONE);

        fabAddPost.setOnClickListener(this);
        imvFilterPost.setOnClickListener(this);
    }

    private void getAllPost() {
        FirebaseDatabase.getInstance().getReference(OBJ_POST).orderByChild("status").equalTo(STATUS_ENABLE)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        posts.clear();
                        tvTotalPostForum.setText("(" + snapshot.getChildrenCount() + ")");
                        if (snapshot.getChildrenCount() > 0) {
                            tvNoPost.setVisibility(View.GONE);
                            rvListPost.setVisibility(View.VISIBLE);
                        } else {
                            tvNoPost.setVisibility(View.VISIBLE);
                            rvListPost.setVisibility(View.GONE);
                        }
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Post post = dataSnapshot.getValue(Post.class);
                            posts.add(post);
                        }
                        Collections.reverse(posts);
                        postAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void getPostByForumId() {
        FirebaseDatabase.getInstance().getReference(OBJ_POST)
                .orderByChild("forumId").equalTo(Objects.requireNonNull(forum).getForumId())
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dsPost) {
                        posts.clear();
                        tvTotalPostForum.setText("(" + dsPost.getChildrenCount() + ")");
                        if (dsPost.getChildrenCount() > 0) {
                            tvNoPost.setVisibility(View.GONE);
                            rvListPost.setVisibility(View.VISIBLE);
                        } else {
                            tvNoPost.setVisibility(View.VISIBLE);
                            rvListPost.setVisibility(View.GONE);
                        }
                        for (DataSnapshot dataSnapshot : dsPost.getChildren()) {
                            Post post = dataSnapshot.getValue(Post.class);
                            if (Objects.requireNonNull(post).getStatus().equals(STATUS_ENABLE)) {
                                posts.add(post);
                            }
                        }
                        Collections.reverse(posts);
                        postAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabAddPost:
                if (FirebaseAuth.getInstance().getCurrentUser() != null)
                    startActivity(new Intent(this, AddPostActivity.class));
                else
                    Toast.makeText(this, "Bạn cần đăng nhập để sử dụng chức năng này!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.imvFilterPost:
                Toast.makeText(this, "Lọc", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void goToActivityDetail(Post post) {
        HashMap<String, Object> updateView = new HashMap<>();
        updateView.put("view", post.getView() + 1);
        FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(post.getPostId()))
                .updateChildren(updateView);

        Intent intent = new Intent(this, DetailPostActivity.class);
        intent.putExtra("post", post);

        startActivity(intent);
    }
}