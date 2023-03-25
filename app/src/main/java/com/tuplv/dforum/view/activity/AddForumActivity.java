package com.tuplv.dforum.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tuplv.dforum.R;
import com.tuplv.dforum.model.Forum;
import com.tuplv.dforum.viewmodel.ForumViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddForumActivity extends AppCompatActivity {

    EditText edtNameForum, edtDesForum;
    Button btnAddForum;
    Toolbar tbAddNewForum;
    ForumViewModel forumViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_posts);

        init();
        setSupportActionBar(tbAddNewForum);

        tbAddNewForum.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void init() {
        edtNameForum = findViewById(R.id.edtNameForum);
        edtDesForum = findViewById(R.id.edtDesForum);
        btnAddForum = findViewById(R.id.btnAddForum);
        tbAddNewForum = findViewById(R.id.tbAddNewForum);
        forumViewModel = new ForumViewModel(this);

        btnAddForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Forum forum = new Forum();
//                forum.setForumId(new Date().getTime());
//                forum.setName(edtNameForum.getText().toString());
//                forum.setDescription(edtDesForum.getText().toString());
//                forumService.create(forum);
//                finish();

                List<Forum> forums = new ArrayList<>();
                forumViewModel.findAll(forums);

                Toast.makeText(AddForumActivity.this, "size" + String.valueOf(forums.size()), Toast.LENGTH_SHORT).show();
            }
        });
    }


}