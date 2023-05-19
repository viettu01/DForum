package com.tuplv.dforum.activity.post;

import static com.tuplv.dforum.until.Constant.CHIA_SE_KIEN_THUC;
import static com.tuplv.dforum.until.Constant.HOI_DAP;
import static com.tuplv.dforum.until.Constant.OBJ_ACCOUNT;
import static com.tuplv.dforum.until.Constant.OBJ_FORUM;
import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.ROLE_ADMIN;
import static com.tuplv.dforum.until.Constant.STATUS_DISABLE;
import static com.tuplv.dforum.until.Constant.STATUS_ENABLE;
import static com.tuplv.dforum.until.Constant.TYPE_NOTIFY_ADMIN_ADD_POST;
import static com.tuplv.dforum.until.Until.sendNotifyAllAccount;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.tuplv.dforum.model.Post;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AddPostActivity extends AppCompatActivity {

    Spinner spnCategory, spnForum;
    EditText edtTitlePost, edtContentPost;
    Toolbar tbAddPost;

    ForumSpinnerAdapter forumSpinnerAdapter;
    CategorySpinnerAdapter categorySpinnerAdapter;
    List<Forum> forums;
    Forum forum;
    SharedPreferences sharedPreferences;
    Account currentAccountLogin;
    List<Account> accounts;
    List<Post> posts;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        init();
        setSupportActionBar(tbAddPost);

        tbAddPost.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setCategoryToSpinner();
        setForumToSpinner();
        getAllAccount();
        // check tiêu đề đã tồn tại
//        spnForum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Forum forum1 = (Forum) adapterView.getItemAtPosition(i);
//                getAllPost(forum1.getForumId());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
       getAllPost();
    }

    private void init() {
        spnCategory = findViewById(R.id.spnCategory);
        spnForum = findViewById(R.id.spnForum);
        edtTitlePost = findViewById(R.id.edtTitlePost);
        edtContentPost = findViewById(R.id.edtContentPost);
        tbAddPost = findViewById(R.id.tbAddPost);
        sharedPreferences = getSharedPreferences("account", MODE_PRIVATE);
        accounts = new ArrayList<>();
        posts = new ArrayList<>();

        forum = (Forum) getIntent().getSerializableExtra("forum");
    }

    //Đổ dữ liệu ra spinner chuyên mục
    private void setCategoryToSpinner() {
        List<String> categoryName = new ArrayList<>();
        categoryName.add(HOI_DAP);
        categoryName.add(CHIA_SE_KIEN_THUC);
        categorySpinnerAdapter = new CategorySpinnerAdapter(this, categoryName);
        spnCategory.setAdapter(categorySpinnerAdapter);
    }

    //Đổ dữ liệu ra spinner diễn đàn
    private void setForumToSpinner() {
        forums = new ArrayList<>();
        forumSpinnerAdapter = new ForumSpinnerAdapter(this, forums);
        spnForum.setAdapter(forumSpinnerAdapter);

        FirebaseDatabase.getInstance().getReference(OBJ_FORUM).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                forums.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Forum forum = dataSnapshot.getValue(Forum.class);
                    forums.add(forum);
                }
                forumSpinnerAdapter.notifyDataSetChanged();

                if (forum != null) {
                    for (int i = 0; i < spnForum.getCount(); i++) {
                        Forum f = (Forum) spnForum.getItemAtPosition(i);
                        if (f.getName().equals(forum.getName()))
                            spnForum.setSelection(i);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // Lấy danh sách tài khoản và lấy thông tin tài khoản đang đăng nhập
    private void getAllAccount() {
        FirebaseDatabase.getInstance().getReference(OBJ_ACCOUNT)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Account account = ds.getValue(Account.class);
                            if (Objects.requireNonNull(account).getAccountId().equals(user.getUid()))
                                currentAccountLogin = account;
                            accounts.add(account);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void getAllPost() {
        FirebaseDatabase.getInstance().getReference(OBJ_POST)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        posts.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Post post = dataSnapshot.getValue(Post.class);
                           //thêm trong hàm long forumId if (Objects.requireNonNull(post).getForumId() == forumId)
                            posts.add(post);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    //Thêm bài viết mới
    public void addPost() {
        String message = "";
        Forum forum = (Forum) spnForum.getSelectedItem();
        Post post = new Post();
        post.setPostId(new Date().getTime());
        post.setAccountId(user.getUid());
        post.setCategoryName(spnCategory.getSelectedItem().toString().trim());
        post.setForumId(forum.getForumId());
        post.setTitle(edtTitlePost.getText().toString().trim());
        post.setContent(edtContentPost.getText().toString().trim());
        post.setCreatedDate(new Date().getTime());
        post.setView(0);

        if (sharedPreferences.getString("role", "").equals(ROLE_ADMIN)) {
            message = "Thêm bài viết thành công";
            post.setStatus(STATUS_ENABLE);
            post.setApproveDate(new Date().getTime());
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
                            Toast.makeText(AddPostActivity.this, finalMessage, Toast.LENGTH_SHORT).show();
                            finish();
                        } else
                            Toast.makeText(AddPostActivity.this, "Thêm bài viết thất bại", Toast.LENGTH_SHORT).show();
                    }
                });

        sendNotifyAllAccount(sharedPreferences.getString("role", ""), null, post, accounts, TYPE_NOTIFY_ADMIN_ADD_POST);
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
                return false;
            }
            //check tiêu đề đã tồn tại
            for (Post post : posts) {
                if (edtTitlePost.getText().toString().trim().equals(post.getTitle())) {
                    Toast.makeText(this, "Tiêu đề đã tồn tại", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            addPost();
        }
        return super.onOptionsItemSelected(item);
    }
}