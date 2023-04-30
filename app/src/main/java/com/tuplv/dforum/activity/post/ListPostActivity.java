package com.tuplv.dforum.activity.post;

import static com.tuplv.dforum.until.Constant.CHIA_SE_KIEN_THUC;
import static com.tuplv.dforum.until.Constant.HOI_DAP;
import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
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
    TextView tvNameForum, tvDesForum, tvNoPost, tvFilterPost;
    ImageView imvFilterPost, imvSortPost;
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
        if (forum != null && forum.getForumId() != 0)
            getPostByForumId(null);
        else if (forum != null && forum.getForumId() == 0)
            getAllPost(null);
        else
            getAllPost(null);
    }

    private void init() {
        tbListPost = findViewById(R.id.tbListPost);
        setSupportActionBar(tbListPost);
        tvNameForum = findViewById(R.id.tvNameForum);
        tvDesForum = findViewById(R.id.tvDesForum);
        tvNoPost = findViewById(R.id.tvNoPost);
        tvFilterPost = findViewById(R.id.tvFilterPost);
        imvFilterPost = findViewById(R.id.imvFilterPost);
        rvListPost = findViewById(R.id.rvListPost);
        fabAddPost = findViewById(R.id.fabAddPost);
        imvSortPost = findViewById(R.id.imvSortPost);

        posts = new ArrayList<>();
        postAdapter = new PostAdapter(this, R.layout.item_post, posts, this);
        rvListPost.setAdapter(postAdapter);
        rvListPost.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        forum = (Forum) getIntent().getSerializableExtra("forum");
//        tvNameForum.setText(forum.getName());
        tvDesForum.setText(forum.getDescription());
        if (forum.getDescription().equals(""))
            tvDesForum.setVisibility(View.GONE);

        imvFilterPost.setOnClickListener(this);
        imvSortPost.setOnClickListener(this);
        fabAddPost.setOnClickListener(this);
    }

    private void getAllPost(String filter) {
        FirebaseDatabase.getInstance().getReference(OBJ_POST).orderByChild("status").equalTo(STATUS_ENABLE)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        posts.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Post post = dataSnapshot.getValue(Post.class);
                            if (filter == null) {
                                posts.add(post);
                            } else {
                                if (filter.equals(HOI_DAP)) {
                                    if (post.getCategoryName().equalsIgnoreCase(HOI_DAP)) {
                                        posts.add(post);
                                    }
                                }
                                if (filter.equals(CHIA_SE_KIEN_THUC)) {
                                    if (post.getCategoryName().equalsIgnoreCase(CHIA_SE_KIEN_THUC)) {
                                        posts.add(post);
                                    }
                                }
                            }
                        }
                        if (posts.size() > 0) {
                            tvNoPost.setVisibility(View.GONE);
                            rvListPost.setVisibility(View.VISIBLE);
                        } else {
                            tvNoPost.setVisibility(View.VISIBLE);
                            rvListPost.setVisibility(View.GONE);
                        }
                        tvNameForum.setText(forum.getName() + " (" + posts.size() + ")");
                        Collections.reverse(posts);
                        postAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void getPostByForumId(String filter) {
        FirebaseDatabase.getInstance().getReference(OBJ_POST)
                .orderByChild("forumId").equalTo(Objects.requireNonNull(forum).getForumId())
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dsPost) {
                        posts.clear();
                        for (DataSnapshot dataSnapshot : dsPost.getChildren()) {
                            Post post = dataSnapshot.getValue(Post.class);
                            if (Objects.requireNonNull(post).getStatus().equals(STATUS_ENABLE)) {
                                if (filter == null) {
                                    posts.add(post);
                                } else {
                                    if (filter.equals(HOI_DAP)) {
                                        if (post.getCategoryName().equalsIgnoreCase(HOI_DAP)) {
                                            posts.add(post);
                                        }
                                    }
                                    if (filter.equals(CHIA_SE_KIEN_THUC)) {
                                        if (post.getCategoryName().equalsIgnoreCase(CHIA_SE_KIEN_THUC)) {
                                            posts.add(post);
                                        }
                                    }
                                }
                            }
                        }
                        if (posts.size() > 0) {
                            tvNoPost.setVisibility(View.GONE);
                            rvListPost.setVisibility(View.VISIBLE);
                        } else {
                            tvNoPost.setVisibility(View.VISIBLE);
                            rvListPost.setVisibility(View.GONE);
                        }
                        tvNameForum.setText(forum.getName() + " (" + posts.size() + ")");
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
            case R.id.imvFilterPost:
                showPopupMenu();
                break;
            case R.id.imvSortPost:
                showPopupMenuSort();
                break;
            case R.id.fabAddPost:
                if (FirebaseAuth.getInstance().getCurrentUser() != null)
                    startActivity(new Intent(this, AddPostActivity.class));
                else
                    Toast.makeText(this, "Bạn cần đăng nhập để sử dụng chức năng này!", Toast.LENGTH_SHORT).show();
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

    @SuppressLint("RestrictedApi, NonConstantResourceId, SetTextI18n")
    private void showPopupMenu() {
        MenuBuilder menuBuilder = new MenuBuilder(this);
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.menu_popup_list_post, menuBuilder);

        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(this, menuBuilder, imvFilterPost);
        menuPopupHelper.setForceShowIcon(true);

        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mnuDeleteFilter:
                        tvFilterPost.setText("");
                        if (forum.getForumId() == 0)
                            getAllPost(null);
                        else
                            getPostByForumId(null);
                        break;
                    case R.id.mnuFilterQA:
                        tvFilterPost.setText("Đang lọc theo: " + HOI_DAP);
                        if (forum.getForumId() == 0)
                            getAllPost(HOI_DAP);
                        else
                            getPostByForumId(HOI_DAP);
                        break;
                    case R.id.mnuFilterShareKnowledge:
                        tvFilterPost.setText("Đang lọc theo: " + CHIA_SE_KIEN_THUC);
                        if (forum.getForumId() == 0)
                            getAllPost(CHIA_SE_KIEN_THUC);
                        else
                            getPostByForumId(CHIA_SE_KIEN_THUC);
                        break;
                }
                return false;
            }

            @Override
            public void onMenuModeChange(@NonNull MenuBuilder menu) {

            }
        });

        menuPopupHelper.show();
    }

    @SuppressLint("RestrictedApi")
    private void showPopupMenuSort() {
        MenuBuilder menuBuilder = new MenuBuilder(this);
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.menu_popup_sort_post, menuBuilder);

        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(this, menuBuilder, imvSortPost);
        menuPopupHelper.setForceShowIcon(true);
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mnuDeleteSort:
                        break;
                    case R.id.mnuSortEarliest:
                        break;
                    case R.id.mnuSortOldest:
                        break;
                    case R.id.mnuSortIncreaseViews:
                        break;
                    case R.id.mnuSortDecreaseViews:
                        break;
                }
                return false;
            }

            @Override
            public void onMenuModeChange(@NonNull MenuBuilder menu) {

            }
        });
        menuPopupHelper.show();
    }
}