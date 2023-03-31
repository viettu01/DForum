package com.tuplv.dforum.activity;

import static com.tuplv.dforum.until.Constant.CHIA_SE_KIEN_THUC;
import static com.tuplv.dforum.until.Constant.HOI_DAP;
import static com.tuplv.dforum.until.Constant.OBJ_FORUM;
import static com.tuplv.dforum.until.Constant.OBJ_POST;
import static com.tuplv.dforum.until.Constant.STATUS_DISABLE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
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
import com.tuplv.dforum.model.Forum;
import com.tuplv.dforum.model.Post;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddPostsActivity extends AppCompatActivity {

    Spinner spnCategory, spnForum;
    EditText edtTitlePost, edtContentPost;
    WebView wvContentPost;
    Toolbar tbAddPost;

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

        // wvContentPost = findViewById(R.id.wvContentPost);
        // wvContentPost.loadUrl("file:///android_asset/ckeditor/index.html");
        // WebSettings webSettings = wvContentPost.getSettings();
        // webSettings.setJavaScriptEnabled(true);
        // wvContentPost.setWebViewClient(new WebViewClient());
    }

    //Đổ dữ liệu ra spinner chuyên mục
    private void loadDataToSpinnerCategory() {
        String[] items = {HOI_DAP, CHIA_SE_KIEN_THUC};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(adapter);
    }

    //Đổ dữ liệu ra spinner diễn đàn
    private void loadDataToSpinnerForum() {
        List<String> items = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnForum.setAdapter(adapter);
        FirebaseDatabase.getInstance().getReference(OBJ_FORUM).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Forum forum = dataSnapshot.getValue(Forum.class);
                    items.add(forum.getName());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //Thêm bài viết mới
    public void create(Post post) {
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
            Post post = new Post();
            post.setPostId(new Date().getTime());
            post.setAccountId(sharedPreferences.getLong("accountId", 0));
            post.setCategoryName(spnCategory.getSelectedItem().toString());
            post.setForumName(spnForum.getSelectedItem().toString());
            post.setTitle(edtTitlePost.getText().toString());
            post.setContent(edtContentPost.getText().toString());
            post.setCreatedDate(new Date().getTime());
            post.setView(0);
            post.setStatus(STATUS_DISABLE);

//            wvContentPost.evaluateJavascript("CKEDITOR.instances['editor'].getData();", new ValueCallback<String>() {
//                @Override
//                public void onReceiveValue(String value) {
//                    // value chứa dữ liệu từ trình soạn thảo CKEditor
//                    Log.d("CKEditor", value);
//                }
//            });
            create(post);
        }
        return super.onOptionsItemSelected(item);
    }
}