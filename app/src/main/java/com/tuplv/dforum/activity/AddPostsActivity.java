package com.tuplv.dforum.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tuplv.dforum.R;

public class AddPostsActivity extends AppCompatActivity {

    Spinner spnCategory, spnForum;
    EditText edtTitlePost, edtContentPost;
    Toolbar tbAddPost;

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
    }

    private void init() {
        spnCategory = findViewById(R.id.spnCategory);
        spnForum = findViewById(R.id.spnForum);
        edtTitlePost = findViewById(R.id.edtTitlePost);
        edtContentPost = findViewById(R.id.edtContentPost);
        tbAddPost = findViewById(R.id.tbAddPost);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_add_post, menu);

        return super.onCreateOptionsMenu(menu);
    }
}