package com.tuplv.dforum.activity;

import static com.tuplv.dforum.until.Constant.CHIA_SE_KIEN_THUC;
import static com.tuplv.dforum.until.Constant.HOI_DAP;
import static com.tuplv.dforum.until.Constant.OBJ_FORUM;
import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.ROLE_ADMIN;
import static com.tuplv.dforum.until.Constant.STATUS_DISABLE;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuplv.dforum.R;
import com.tuplv.dforum.adapter.CategorySpinnerAdapter;
import com.tuplv.dforum.adapter.ForumSpinnerAdapter;
import com.tuplv.dforum.model.Forum;
import com.tuplv.dforum.model.Post;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddPostsActivity extends AppCompatActivity {

    Spinner spnCategory, spnForum;
    EditText edtTitlePost, edtContentPost;
    Toolbar tbAddPost;

    ForumSpinnerAdapter forumSpinnerAdapter;
    CategorySpinnerAdapter categorySpinnerAdapter;
    List<Forum> forums;

    SharedPreferences sharedPreferences;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_posts);

        init();
        setSupportActionBar(tbAddPost);

        tbAddPost.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        loadDataToSpinnerCategory();
        loadDataToSpinnerForum();
    }

    private void init() {
        spnCategory = findViewById(R.id.spnCategory);
        spnForum = findViewById(R.id.spnForum);
        edtTitlePost = findViewById(R.id.edtTitlePost);
        edtContentPost = findViewById(R.id.edtContentPost);
        tbAddPost = findViewById(R.id.tbAddPost);
        sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
    }

    //Đổ dữ liệu ra spinner chuyên mục
    private void loadDataToSpinnerCategory() {
        List<String> categoryName = new ArrayList<>();
        categoryName.add(HOI_DAP);
        categoryName.add(CHIA_SE_KIEN_THUC);
        categorySpinnerAdapter = new CategorySpinnerAdapter(this, categoryName);
        spnCategory.setAdapter(categorySpinnerAdapter);
    }

    //Đổ dữ liệu ra spinner diễn đàn
    private void loadDataToSpinnerForum() {
        forums = new ArrayList<>();
        forumSpinnerAdapter = new ForumSpinnerAdapter(this, forums);
        spnForum.setAdapter(forumSpinnerAdapter);

        FirebaseDatabase.getInstance().getReference(OBJ_FORUM).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Forum forum = dataSnapshot.getValue(Forum.class);
                    forums.add(forum);
                }
                forumSpinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //Thêm bài viết mới
    public void create() {
        Forum forum = (Forum) spnForum.getSelectedItem();
        Post post = new Post();
        post.setPostId(new Date().getTime());
        post.setAccountId(sharedPreferences.getString("accountId", ""));
        post.setCategoryName(spnCategory.getSelectedItem().toString());
        post.setForumId(forum.getForumId());
        post.setTitle(edtTitlePost.getText().toString());
        post.setContent(edtContentPost.getText().toString());
        post.setCreatedDate(new Date().getTime());
        post.setView(0);
        if (sharedPreferences.getString("role", "").equals(ROLE_ADMIN))
            post.setStatus(STATUS_ENABLE);
        else
            post.setStatus(STATUS_DISABLE);

        FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(post.getPostId())).setValue(post)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddPostsActivity.this, "Thêm bài viết thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        } else
                            Toast.makeText(AddPostsActivity.this, "Thêm bài viết thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_add_post, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mnuSavePost) {
            if (edtTitlePost.getText().toString().isEmpty() || edtContentPost.getText().toString().isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tiêu đề và nội dung bài viết", Toast.LENGTH_SHORT).show();
            } else {
                create();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}