package com.tuplv.dforum.activity.post;

import static com.tuplv.dforum.until.Constant.CHIA_SE_KIEN_THUC;
import static com.tuplv.dforum.until.Constant.HOI_DAP;
import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.OBJ_FORUM;
import static com.tuplv.dforum.until.Constant.OBJ_NOTIFY;
import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.ROLE_ADMIN;
import static com.tuplv.dforum.until.Constant.STATUS_DISABLE;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;
import static com.tuplv.dforum.until.Constant.TYPE_NOTIFY_ADD_POST;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuplv.dforum.R;
import com.tuplv.dforum.adapter.CategorySpinnerAdapter;
import com.tuplv.dforum.adapter.ForumSpinnerAdapter;
import com.tuplv.dforum.model.Account;
import com.tuplv.dforum.model.Forum;
import com.tuplv.dforum.model.Notify;
import com.tuplv.dforum.model.Post;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AddPostsActivity extends AppCompatActivity {

    Spinner spnCategory, spnForum;
    EditText edtTitlePost, edtContentPost;
    Toolbar tbAddPost;

    ForumSpinnerAdapter forumSpinnerAdapter;
    CategorySpinnerAdapter categorySpinnerAdapter;
    List<Forum> forums;
    SharedPreferences sharedPreferences;
    Account account;
    List<Account> accounts;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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
        getDataAccount();
    }

    private void init() {
        spnCategory = findViewById(R.id.spnCategory);
        spnForum = findViewById(R.id.spnForum);
        edtTitlePost = findViewById(R.id.edtTitlePost);
        edtContentPost = findViewById(R.id.edtContentPost);
        tbAddPost = findViewById(R.id.tbAddPost);
        sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
        accounts = new ArrayList<>();
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

    private void getDataAccount() {
        FirebaseDatabase.getInstance().getReference(OBJ_ACCOUNT).child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            account = snapshot.getValue(Account.class);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        FirebaseDatabase.getInstance().getReference(OBJ_ACCOUNT)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Account account1 = ds.getValue(Account.class);
                            accounts.add(account1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    //Thêm bài viết mới
    public void create() {
        String message = "";
        Forum forum = (Forum) spnForum.getSelectedItem();
        Post post = new Post();
        post.setPostId(new Date().getTime());
        post.setAccountId(user.getUid());
        post.setCategoryName(spnCategory.getSelectedItem().toString());
        post.setForumId(forum.getForumId());
        post.setTitle(edtTitlePost.getText().toString());
        post.setContent(edtContentPost.getText().toString());
        post.setCreatedDate(new Date().getTime());
        post.setView(0);
        if (sharedPreferences.getString("role", "").equals(ROLE_ADMIN)) {
            message = "Thêm bài viết thành công";
            post.setStatus(STATUS_ENABLE);
            post.setApprovalDate(new Date().getTime());
        } else {
            message = "Bài viết của bạn đang chờ kiểm duyệt";
            post.setStatus(STATUS_DISABLE);
        }

        String finalMessage = message;
        FirebaseDatabase.getInstance().getReference(OBJ_POST).child(String.valueOf(post.getPostId())).setValue(post)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddPostsActivity.this, finalMessage, Toast.LENGTH_SHORT).show();
                            finish();
                        } else
                            Toast.makeText(AddPostsActivity.this, "Thêm bài viết thất bại", Toast.LENGTH_SHORT).show();
                    }
                });

        if (sharedPreferences.getString("role", "").equals(ROLE_ADMIN)) {
            Notify notify = new Notify();
            notify.setNotifyId(new Date().getTime());
            notify.setPostId(post.getPostId());
            notify.setAccountId(user.getUid());
            notify.setStatus(STATUS_DISABLE);
            notify.setTypeNotify(TYPE_NOTIFY_ADD_POST);
            for (Account account : accounts) {
                HashMap<String, Object> updateNotify = new HashMap<>();
                updateNotify.put(OBJ_NOTIFY, notify);
                if (!account.getAccountId().equals(user.getUid()))
                    FirebaseDatabase.getInstance().getReference(OBJ_ACCOUNT).child(account.getAccountId())
                            .child(OBJ_NOTIFY).child(String.valueOf(notify.getNotifyId())).setValue(notify);
            }
        }
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